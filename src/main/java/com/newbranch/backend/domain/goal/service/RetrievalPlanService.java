package com.newbranch.backend.domain.goal.service;

import com.newbranch.backend.domain.goal.dto.RetrievalPlanRequest;
import com.newbranch.backend.domain.goal.dto.RetrievalPlanResponse;
import com.newbranch.backend.domain.goal.entity.RetrievalPlan;
import com.newbranch.backend.domain.goal.repository.RetrievalPlanRepository;
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
public class RetrievalPlanService {

    private final RetrievalPlanRepository retrievalPlanRepository;
    private final LearningGoalService learningGoalService;
    private final UserRepository userRepository;

    @Transactional
    public RetrievalPlanResponse updatePlan(String email, Long goalId, Long planId, RetrievalPlanRequest request) {
        User user = findUserByEmail(email);
        learningGoalService.findGoalByIdAndUser(goalId, user);
        RetrievalPlan plan = retrievalPlanRepository.findByIdAndIsActiveTrue(planId)
                .orElseThrow(() -> new BusinessException(ErrorCode.RETRIEVAL_PLAN_NOT_FOUND));
        plan.update(request.getStartDate(), request.getEndDate(), request.getRepetitionCount(), request.getMethod());
        return RetrievalPlanResponse.from(plan);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
