package com.newbranch.backend.domain.goal.service;

import com.newbranch.backend.domain.goal.dto.DailyStudyTimeRequest;
import com.newbranch.backend.domain.goal.dto.DailyStudyTimeResponse;
import com.newbranch.backend.domain.goal.dto.LearningGoalRequest;
import com.newbranch.backend.domain.goal.dto.LearningGoalResponse;
import com.newbranch.backend.domain.goal.dto.MemorizationPlanResponse;
import com.newbranch.backend.domain.goal.dto.RetrievalPlanResponse;
import com.newbranch.backend.domain.goal.dto.StudyPlanResponse;
import com.newbranch.backend.domain.goal.entity.DailyStudyTime;
import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.goal.entity.MemorizationPlan;
import com.newbranch.backend.domain.goal.entity.RetrievalPlan;
import com.newbranch.backend.domain.goal.entity.StudyPlan;
import com.newbranch.backend.domain.goal.repository.DailyStudyTimeRepository;
import com.newbranch.backend.domain.goal.repository.LearningGoalRepository;
import com.newbranch.backend.domain.goal.repository.MemorizationPlanRepository;
import com.newbranch.backend.domain.goal.repository.RetrievalPlanRepository;
import com.newbranch.backend.domain.goal.repository.StudyPlanRepository;
import com.newbranch.backend.domain.note.entity.Note;
import com.newbranch.backend.domain.note.repository.NoteRepository;
import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.domain.user.repository.UserRepository;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LearningGoalService {

    private final LearningGoalRepository learningGoalRepository;
    private final MemorizationPlanRepository memorizationPlanRepository;
    private final RetrievalPlanRepository retrievalPlanRepository;
    private final StudyPlanRepository studyPlanRepository;
    private final DailyStudyTimeRepository dailyStudyTimeRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    // F-GO-001: 학습 목표 목록 조회
    public List<LearningGoalResponse> getGoals(String email) {
        User user = findUserByEmail(email);
        return learningGoalRepository.findAllByUserAndIsActiveTrueOrderByCreatedAtDesc(user)
                .stream()
                .map(LearningGoalResponse::from)
                .toList();
    }

    // F-GO-002: 학습 목표 생성 (암기/인출/기타학습/공부시간 포함)
    @Transactional
    public LearningGoalResponse createGoal(String email, LearningGoalRequest request) {
        User user = findUserByEmail(email);

        List<Note> notes = noteRepository.findAllByIdInAndIsActiveTrue(request.getNoteIds());
        if (notes.size() != request.getNoteIds().size()) {
            throw new BusinessException(ErrorCode.NOTE_NOT_FOUND);
        }
        boolean hasUnauthorized = notes.stream()
                .anyMatch(n -> !n.getUser().getId().equals(user.getId()));
        if (hasUnauthorized) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        LearningGoal goal = learningGoalRepository.save(LearningGoal.builder()
                .user(user)
                .title(request.getTitle())
                .purpose(request.getPurpose())
                .targetScore(request.getTargetScore())
                .maxScore(request.getMaxScore())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .studyMethod(request.getStudyMethod())
                .build());

        notes.forEach(note -> note.linkToGoal(goal));

        if (request.getMemorizationPlan() != null) {
            var mp = request.getMemorizationPlan();
            memorizationPlanRepository.save(MemorizationPlan.builder()
                    .user(user)
                    .learningGoal(goal)
                    .startDate(mp.getStartDate())
                    .endDate(mp.getEndDate())
                    .repetitionCount(mp.getRepetitionCount())
                    .method(mp.getMethod())
                    .build());
        }

        if (request.getRetrievalPlan() != null) {
            var rp = request.getRetrievalPlan();
            retrievalPlanRepository.save(RetrievalPlan.builder()
                    .user(user)
                    .learningGoal(goal)
                    .startDate(rp.getStartDate())
                    .endDate(rp.getEndDate())
                    .repetitionCount(rp.getRepetitionCount())
                    .method(rp.getMethod())
                    .build());
        }

        if (request.getStudyPlan() != null) {
            var sp = request.getStudyPlan();
            studyPlanRepository.save(StudyPlan.builder()
                    .user(user)
                    .learningGoal(goal)
                    .startDate(sp.getStartDate())
                    .endDate(sp.getEndDate())
                    .repetitionCount(sp.getRepetitionCount())
                    .method(sp.getMethod())
                    .build());
        }

        if (request.getDailyStudyTimes() != null && !request.getDailyStudyTimes().isEmpty()) {
            List<DailyStudyTime> times = request.getDailyStudyTimes().stream()
                    .map(entry -> DailyStudyTime.builder()
                            .user(user)
                            .learningGoal(goal)
                            .studyDate(entry.getStudyDate())
                            .availableMinutes(entry.getAvailableMinutes())
                            .build())
                    .toList();
            dailyStudyTimeRepository.saveAll(times);
        }

        return LearningGoalResponse.from(goal);
    }

    // F-GO-003: 학습 목표 상세 조회
    public LearningGoalResponse getGoal(String email, Long lgId) {
        User user = findUserByEmail(email);
        LearningGoal goal = findGoalByIdAndUser(lgId, user);

        List<MemorizationPlanResponse> mpList = memorizationPlanRepository
                .findAllByLearningGoalAndIsActiveTrue(goal).stream()
                .map(MemorizationPlanResponse::from).toList();
        List<RetrievalPlanResponse> rpList = retrievalPlanRepository
                .findAllByLearningGoalAndIsActiveTrue(goal).stream()
                .map(RetrievalPlanResponse::from).toList();
        List<StudyPlanResponse> spList = studyPlanRepository
                .findAllByLearningGoalAndIsActiveTrue(goal).stream()
                .map(StudyPlanResponse::from).toList();
        List<DailyStudyTimeResponse> dstList = dailyStudyTimeRepository
                .findAllByLearningGoalAndIsActiveTrueOrderByStudyDateAsc(goal).stream()
                .map(DailyStudyTimeResponse::from).toList();

        return LearningGoalResponse.fromDetail(goal, mpList, rpList, spList, dstList);
    }

    // F-GO-004: 학습 목표 검색
    public List<LearningGoalResponse> searchGoals(String email, String keyword) {
        User user = findUserByEmail(email);
        return learningGoalRepository.searchByKeyword(user, keyword)
                .stream()
                .map(LearningGoalResponse::from)
                .toList();
    }

    // F-GO-005: 학습 목표 삭제
    @Transactional
    public void deleteGoal(String email, Long lgId) {
        User user = findUserByEmail(email);
        findGoalByIdAndUser(lgId, user).deactivate();
    }

    // F-GO-006: 학습 목표 수정
    @Transactional
    public LearningGoalResponse updateGoal(String email, Long lgId, LearningGoalRequest request) {
        User user = findUserByEmail(email);
        LearningGoal goal = findGoalByIdAndUser(lgId, user);
        goal.update(request.getTitle(), request.getPurpose(), request.getTargetScore(), request.getMaxScore(),
                request.getStartDate(), request.getEndDate(), request.getStudyMethod());
        return LearningGoalResponse.from(goal);
    }

    // 일별 공부 시간 수정
    @Transactional
    public List<DailyStudyTimeResponse> updateDailyStudyTimes(String email, Long lgId, DailyStudyTimeRequest request) {
        User user = findUserByEmail(email);
        LearningGoal goal = findGoalByIdAndUser(lgId, user);

        dailyStudyTimeRepository.deleteAllByLearningGoal(goal);

        List<DailyStudyTime> times = request.getStudyTimes().stream()
                .map(entry -> DailyStudyTime.builder()
                        .user(user)
                        .learningGoal(goal)
                        .studyDate(entry.getStudyDate())
                        .availableMinutes(entry.getAvailableMinutes())
                        .build())
                .toList();
        return dailyStudyTimeRepository.saveAll(times).stream()
                .map(DailyStudyTimeResponse::from)
                .toList();
    }

    LearningGoal findGoalByIdAndUser(Long goalId, User user) {
        LearningGoal goal = learningGoalRepository.findByIdAndIsActiveTrue(goalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GOAL_NOT_FOUND));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        return goal;
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}