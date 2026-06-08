package com.newbranch.backend.domain.goal.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AiStudyPlanResponse {

    private Long lgId;
    private List<DailyPlan> dailyPlans;

    public static AiStudyPlanResponse of(Long lgId, List<DailyPlan> dailyPlans) {
        AiStudyPlanResponse response = new AiStudyPlanResponse();
        response.lgId = lgId;
        response.dailyPlans = dailyPlans;
        return response;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class DailyPlan {
        private String date;
        private List<Task> tasks;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Task {
        private String startTime;
        private Integer durationMinutes;
        private String category;
        private String activity;
        private String method;
    }
}