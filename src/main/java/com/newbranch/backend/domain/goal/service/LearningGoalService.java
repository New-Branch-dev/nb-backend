package com.newbranch.backend.domain.goal.service;

import com.newbranch.backend.domain.attachment.entity.Attachment;
import com.newbranch.backend.domain.attachment.repository.AttachmentRepository;
import com.newbranch.backend.domain.goal.dto.ByDayStudyTimeResponse;
import com.newbranch.backend.domain.goal.dto.LearningGoalRequest;
import com.newbranch.backend.domain.goal.dto.LearningGoalResponse;
import com.newbranch.backend.domain.goal.entity.*;
import com.newbranch.backend.domain.goal.repository.*;
import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.domain.user.service.UserQueryService;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LearningGoalService {

    private final LearningGoalRepository learningGoalRepository;
    private final ByDayStudyTimeRepository byDayStudyTimeRepository;
    private final LearningGoalExcludeRepository learningGoalExcludeRepository;
    private final LearningGoalMethodRepository learningGoalMethodRepository;
    private final AttachmentRepository attachmentRepository;
    private final UserQueryService userQueryService;

    /**
     * 사용자의 활성 학습 목표 목록을 최신순으로 조회합니다.
     */
    public List<LearningGoalResponse> getGoals(String email) {
        User user = userQueryService.findByLoginId(email);
        return learningGoalRepository.findAllByUserAndIsActiveTrueOrderByCreatedAtDesc(user)
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    /**
     * 새 학습 목표를 생성하고 첨부파일, 학습 방법, 요일별 시간, 제외 날짜를 함께 저장합니다.
     */
    @Transactional
    public LearningGoalResponse createGoal(String email, LearningGoalRequest request) {
        User user = userQueryService.findByLoginId(email);
        List<Attachment> attachments = validateAndFetchAttachments(request.getAttachmentIds(), user);

        LearningGoal goal = saveGoal(user, request);
        attachments.forEach(a -> a.linkToGoal(goal));
        saveMethods(goal, request.getMethods());
        saveByDayStudyTimes(user, goal, request.getByDayStudyTimes());
        saveExcludeDates(goal, request.getExcludeDates());

        List<String> methods = request.getMethods() != null ? request.getMethods() : List.of();
        return LearningGoalResponse.from(goal, methods);
    }

    /**
     * 학습 목표 상세 정보를 조회합니다 (요일별 공부 시간, 제외 날짜 포함).
     */
    public LearningGoalResponse getGoal(String email, Long lgId) {
        User user = userQueryService.findByLoginId(email);
        LearningGoal goal = findGoalByIdAndUser(lgId, user);

        List<String> methods = fetchMethodNames(goal);
        List<ByDayStudyTimeResponse> byDayStudyTimes = fetchByDayStudyTimes(goal);
        List<LocalDate> excludeDates = fetchExcludeDates(goal);

        return LearningGoalResponse.fromDetail(goal, methods, byDayStudyTimes, excludeDates);
    }

    /**
     * 제목 키워드로 학습 목표를 검색합니다.
     */
    public List<LearningGoalResponse> searchGoals(String email, String keyword) {
        User user = userQueryService.findByLoginId(email);
        return learningGoalRepository.searchByKeyword(user, keyword)
                .stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    /**
     * 학습 목표를 소프트 삭제합니다.
     */
    @Transactional
    public void deleteGoal(String email, Long lgId) {
        User user = userQueryService.findByLoginId(email);
        findGoalByIdAndUser(lgId, user).deactivate();
    }

    /**
     * 학습 목표 기본 정보 및 학습 방법을 수정합니다.
     */
    @Transactional
    public LearningGoalResponse updateGoal(String email, Long lgId, LearningGoalRequest request) {
        User user = userQueryService.findByLoginId(email);
        LearningGoal goal = findGoalByIdAndUser(lgId, user);
        goal.update(request.getTitle(), request.getPurpose(), request.getTargetScore(),
                request.getMaxScore(), request.getStartDate(), request.getEndDate());
        updateMethods(goal, request.getMethods());
        return LearningGoalResponse.from(goal, fetchMethodNames(goal));
    }

    /**
     * 학습 목표의 요일별 공부 가능 시간을 교체합니다.
     */
    @Transactional
    public List<ByDayStudyTimeResponse> updateByDayStudyTimes(String email, Long lgId,
                                                               List<LearningGoalRequest.ByDayStudyTimeEntry> entries) {
        User user = userQueryService.findByLoginId(email);
        LearningGoal goal = findGoalByIdAndUser(lgId, user);
        byDayStudyTimeRepository.deleteAllByLearningGoal(goal);
        saveByDayStudyTimes(user, goal, entries);
        return fetchByDayStudyTimes(goal);
    }

    /**
     * 학습 목표의 제외 날짜를 교체합니다.
     */
    @Transactional
    public List<LocalDate> updateExcludeDates(String email, Long lgId, List<LocalDate> excludeDates) {
        User user = userQueryService.findByLoginId(email);
        LearningGoal goal = findGoalByIdAndUser(lgId, user);
        learningGoalExcludeRepository.deleteAllByLearningGoal(goal);
        saveExcludeDates(goal, excludeDates);
        return excludeDates;
    }

    // ── 외부 도메인 공개 (AiStudyPlanService에서 사용) ──

    public LearningGoal findGoalByIdAndUser(Long goalId, User user) {
        LearningGoal goal = learningGoalRepository.findByIdAndIsActiveTrue(goalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GOAL_NOT_FOUND));
        if (!goal.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        return goal;
    }

    // ── private 헬퍼 ─────────────────────────────────────────────────────────

    private LearningGoalResponse toSummaryResponse(LearningGoal goal) {
        return LearningGoalResponse.from(goal, fetchMethodNames(goal));
    }

    private List<Attachment> validateAndFetchAttachments(List<Long> ids, User user) {
        List<Attachment> attachments = attachmentRepository.findAllByIdInAndIsActiveTrue(ids);
        if (attachments.size() != ids.size()) {
            throw new BusinessException(ErrorCode.ATTACHMENT_NOT_FOUND);
        }
        if (attachments.stream().anyMatch(a -> !a.getUser().getId().equals(user.getId()))) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        return attachments;
    }

    private LearningGoal saveGoal(User user, LearningGoalRequest request) {
        return learningGoalRepository.save(LearningGoal.builder()
                .user(user)
                .title(request.getTitle())
                .purpose(request.getPurpose())
                .targetScore(request.getTargetScore())
                .maxScore(request.getMaxScore())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build());
    }

    private void saveMethods(LearningGoal goal, List<String> methods) {
        if (methods == null) return;
        methods.forEach(method -> learningGoalMethodRepository.save(
                LearningGoalMethod.builder().learningGoal(goal).methodName(method).build()));
    }

    private void saveByDayStudyTimes(User user, LearningGoal goal,
                                      List<LearningGoalRequest.ByDayStudyTimeEntry> entries) {
        if (entries == null) return;
        entries.forEach(entry -> byDayStudyTimeRepository.save(
                ByDayStudyTime.builder()
                        .user(user).learningGoal(goal)
                        .dayOfWeek(entry.getDayOfWeek())
                        .availableMinutes(entry.getAvailableMinutes()).build()));
    }

    private void saveExcludeDates(LearningGoal goal, List<LocalDate> dates) {
        if (dates == null) return;
        dates.forEach(date -> learningGoalExcludeRepository.save(
                LearningGoalExclude.builder().learningGoal(goal).excludeDate(date).build()));
    }

    private void updateMethods(LearningGoal goal, List<String> methods) {
        if (methods == null) return;
        learningGoalMethodRepository.deleteAllByLearningGoal(goal);
        saveMethods(goal, methods);
    }

    private List<String> fetchMethodNames(LearningGoal goal) {
        return learningGoalMethodRepository.findAllByLearningGoalAndIsActiveTrue(goal)
                .stream().map(LearningGoalMethod::getMethodName).toList();
    }

    private List<ByDayStudyTimeResponse> fetchByDayStudyTimes(LearningGoal goal) {
        return byDayStudyTimeRepository.findAllByLearningGoalAndIsActiveTrueOrderByDayOfWeekAsc(goal)
                .stream().map(ByDayStudyTimeResponse::from).toList();
    }

    private List<LocalDate> fetchExcludeDates(LearningGoal goal) {
        return learningGoalExcludeRepository.findAllByLearningGoalAndIsActiveTrueOrderByExcludeDateAsc(goal)
                .stream().map(LearningGoalExclude::getExcludeDate).toList();
    }
}
