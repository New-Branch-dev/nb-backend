package com.newbranch.backend.domain.goal.dto;

import com.newbranch.backend.domain.goal.entity.MemorizationPlan;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class MemorizationPlanResponse {

    private Long mpId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer repetitionCount;
    private String method;
    private String recommendedMethod;

    public static MemorizationPlanResponse from(MemorizationPlan plan) {
        return MemorizationPlanResponse.builder()
                .mpId(plan.getId())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .repetitionCount(plan.getRepetitionCount())
                .method(plan.getMethod())
                .recommendedMethod(plan.getRecommendedMethod())
                .build();
    }
}