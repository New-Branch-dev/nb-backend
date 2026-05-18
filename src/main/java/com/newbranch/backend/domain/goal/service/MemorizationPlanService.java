package com.newbranch.backend.domain.goal.service;

import com.newbranch.backend.domain.goal.dto.MemorizationPlanRequest;
import com.newbranch.backend.domain.goal.dto.MemorizationPlanResponse;
import com.newbranch.backend.domain.goal.entity.MemorizationPlan;
import com.newbranch.backend.domain.goal.repository.MemorizationPlanRepository;
import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.domain.user.repository.UserRepository;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemorizationPlanService {

    private final MemorizationPlanRepository memorizationPlanRepository;
    private final LearningGoalService learningGoalService;
    private final UserRepository userRepository;

    @Transactional
    public MemorizationPlanResponse updatePlan(String email, Long lgId, Long mpId, MemorizationPlanRequest request) {
        User user = findUserByEmail(email);
        learningGoalService.findGoalByIdAndUser(lgId, user);
        MemorizationPlan plan = memorizationPlanRepository.findByIdAndIsActiveTrue(mpId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMORIZATION_PLAN_NOT_FOUND));
        plan.update(request.getStartDate(), request.getEndDate(), request.getRepetitionCount(), request.getMethod());
        return MemorizationPlanResponse.from(plan);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
