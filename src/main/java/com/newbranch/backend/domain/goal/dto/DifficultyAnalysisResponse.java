package com.newbranch.backend.domain.goal.dto;

import com.newbranch.backend.domain.goal.entity.LearningGoal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DifficultyAnalysisResponse {

    private Long lgId;
    private String difficultyAnalysis;
    private String trendAnalysis;
    private String recommendedMethod;

    public static DifficultyAnalysisResponse from(LearningGoal goal) {
        return DifficultyAnalysisResponse.builder()
                .lgId(goal.getId())
                .difficultyAnalysis(goal.getDifficultyAnalysis())
                .trendAnalysis(goal.getTrendAnalysis())
                .recommendedMethod(goal.getRecommendedMethod())
                .build();
    }
}