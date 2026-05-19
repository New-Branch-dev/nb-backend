package com.newbranch.backend.domain.goal.dto;

import com.newbranch.backend.domain.goal.entity.LearningGoal;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class LearningGoalResponse {

    private Long lgId;
    private String title;
    private String purpose;
    private Integer targetScore;
    private Integer maxScore;
    private LocalDate startDate;
    private LocalDate endDate;
    private String studyMethod;
    private String difficultyAnalysis;
    private String trendAnalysis;
    private String recommendedMethod;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<MemorizationPlanResponse> memorizationPlans;
    private List<RetrievalPlanResponse> retrievalPlans;
    private List<StudyPlanResponse> studyPlans;
    private List<DailyStudyTimeResponse> dailyStudyTimes;

    public static LearningGoalResponse from(LearningGoal goal) {
        return LearningGoalResponse.builder()
                .lgId(goal.getId())
                .title(goal.getTitle())
                .purpose(goal.getPurpose())
                .targetScore(goal.getTargetScore())
                .maxScore(goal.getMaxScore())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .studyMethod(goal.getStudyMethod())
                .difficultyAnalysis(goal.getDifficultyAnalysis())
                .trendAnalysis(goal.getTrendAnalysis())
                .recommendedMethod(goal.getRecommendedMethod())
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .build();
    }

    public static LearningGoalResponse fromDetail(
            LearningGoal goal,
            List<MemorizationPlanResponse> memorizationPlans,
            List<RetrievalPlanResponse> retrievalPlans,
            List<StudyPlanResponse> studyPlans,
            List<DailyStudyTimeResponse> dailyStudyTimes) {
        return LearningGoalResponse.builder()
                .lgId(goal.getId())
                .title(goal.getTitle())
                .purpose(goal.getPurpose())
                .targetScore(goal.getTargetScore())
                .maxScore(goal.getMaxScore())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .studyMethod(goal.getStudyMethod())
                .difficultyAnalysis(goal.getDifficultyAnalysis())
                .trendAnalysis(goal.getTrendAnalysis())
                .recommendedMethod(goal.getRecommendedMethod())
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .memorizationPlans(memorizationPlans)
                .retrievalPlans(retrievalPlans)
                .studyPlans(studyPlans)
                .dailyStudyTimes(dailyStudyTimes)
                .build();
    }
}
