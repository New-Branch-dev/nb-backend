package com.newbranch.backend.domain.goal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class LearningGoalRequest {

    @NotNull
    private List<Long> attachmentIds;

    @NotBlank
    private String title;

    private String purpose;

    private Integer targetScore;

    private Integer maxScore;

    private LocalDate startDate;

    private LocalDate endDate;

    private List<String> methods;

    private List<ByDayStudyTimeEntry> byDayStudyTimes;

    private List<LocalDate> excludeDates;

    @Getter
    public static class ByDayStudyTimeEntry {
        @NotNull
        @Min(1) @Max(7)
        private Integer dayOfWeek;  // 1=월 ~ 7=일
        @NotNull
        @Min(0) @Max(1440)
        private Integer availableMinutes;
    }
}
