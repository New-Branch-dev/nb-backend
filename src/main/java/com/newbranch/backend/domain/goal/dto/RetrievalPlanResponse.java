package com.newbranch.backend.domain.goal.dto;

import com.newbranch.backend.domain.goal.entity.RetrievalPlan;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RetrievalPlanResponse {

    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer repetitionCount;
    private String method;
    private String recommendedMethod;

    public static RetrievalPlanResponse from(RetrievalPlan plan) {
        return RetrievalPlanResponse.builder()
                .id(plan.getId())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .repetitionCount(plan.getRepetitionCount())
                .method(plan.getMethod())
                .recommendedMethod(plan.getRecommendedMethod())
                .build();
    }
}
