package com.newbranch.backend.domain.goal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class LearningGoalRequest {

    @NotNull
    private List<Long> noteIds;

    @NotBlank
    private String title;

    private String purpose;

    private Integer targetScore;

    private Integer maxScore;

    private LocalDate startDate;

    private LocalDate endDate;

    private String studyMethod;

    private List<DailyStudyTimeEntry> dailyStudyTimes;

    private MemorizationPlanRequest memorizationPlan;

    private RetrievalPlanRequest retrievalPlan;

    private StudyPlanRequest studyPlan;

    @Getter
    public static class DailyStudyTimeEntry {
        private LocalDate studyDate;
        private Integer availableMinutes;
    }
}