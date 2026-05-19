package com.newbranch.backend.domain.goal.service;

import com.newbranch.backend.domain.goal.dto.DailyStudyTimeRequest;
import com.newbranch.backend.domain.goal.dto.DailyStudyTimeResponse;
import com.newbranch.backend.domain.goal.dto.StudyPlanRequest;
import com.newbranch.backend.domain.goal.dto.StudyPlanResponse;
import com.newbranch.backend.domain.goal.entity.DailyStudyTime;
import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.goal.entity.StudyPlan;
import com.newbranch.backend.domain.goal.repository.DailyStudyTimeRepository;
import com.newbranch.backend.domain.goal.repository.StudyPlanRepository;
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
public class StudyPlanService {

    private final StudyPlanRepository studyPlanRepository;
    private final DailyStudyTimeRepository dailyStudyTimeRepository;
    private final LearningGoalService learningGoalService;
    private final UserRepository userRepository;

    // 기타학습 계획 수정
    @Transactional
    public StudyPlanResponse updatePlan(String email, Long lgId, Long spId, StudyPlanRequest request) {
        User user = findUserByEmail(email);
        learningGoalService.findGoalByIdAndUser(lgId, user);
        StudyPlan plan = studyPlanRepository.findByIdAndIsActiveTrue(spId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDY_PLAN_NOT_FOUND));
        plan.update(request.getStartDate(), request.getEndDate(), request.getRepetitionCount(), request.getMethod());
        return StudyPlanResponse.from(plan);
    }

    // 일별 공부시간 설정 (벌크 저장)
    @Transactional
    public List<DailyStudyTimeResponse> saveStudyTimes(String email, Long lgId, DailyStudyTimeRequest request) {
        User user = findUserByEmail(email);
        LearningGoal goal = learningGoalService.findGoalByIdAndUser(lgId, user);
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

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}