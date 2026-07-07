package com.newbranch.backend.domain.goal.dto;

import com.newbranch.backend.domain.goal.entity.StudyPlan;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class StudyPlanResponse {

    private Long spId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer repetitionCount;
    private String method;
    private String recommendedMethod;

    public static StudyPlanResponse from(StudyPlan plan) {
        return StudyPlanResponse.builder()
                .spId(plan.getId())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .repetitionCount(plan.getRepetitionCount())
                .method(plan.getMethod())
                .recommendedMethod(plan.getRecommendedMethod())
                .build();
    }
}