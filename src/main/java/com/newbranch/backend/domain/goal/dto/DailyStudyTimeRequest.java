package com.newbranch.backend.domain.goal.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class DailyStudyTimeRequest {

    @NotNull
    private List<Entry> studyTimes;

    @Getter
    public static class Entry {
        @NotNull
        private LocalDate studyDate;
        @NotNull
        private Integer availableMinutes;
    }
}
