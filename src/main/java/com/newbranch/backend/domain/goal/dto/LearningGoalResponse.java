package com.newbranch.backend.domain.goal.dto;

import com.newbranch.backend.domain.goal.entity.LearningGoal;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
public class LearningGoalResponse {

    private Long lgId;
    private String title;
    private String purpose;
    private Integer targetScore;
    private Integer maxScore;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> methods;
    private Boolean hasAiStudyPlan;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<ByDayStudyTimeResponse> byDayStudyTimes;
    private List<LocalDate> excludeDates;

    /**
     * 목록 조회용 응답 생성 (상세 정보 제외)
     */
    public static LearningGoalResponse from(LearningGoal goal, List<String> methods) {
        return LearningGoalResponse.builder()
                .lgId(goal.getId())
                .title(goal.getTitle())
                .purpose(goal.getPurpose())
                .targetScore(goal.getTargetScore())
                .maxScore(goal.getMaxScore())
                .startDate(goal.getStartDate())
                .endDate(goal.getEndDate())
                .methods(methods)
                .hasAiStudyPlan(goal.getAiStudyPlan() != null)
                .createdAt(goal.getCreatedAt())
                .updatedAt(goal.getUpdatedAt())
                .build();
    }

    /**
     * 상세 조회용 응답 생성 (요일별 공부 시간, 제외 날짜 포함)
     */
    public static LearningGoalResponse fromDetail(LearningGoal goal, List<String> methods,
                                                   List<ByDayStudyTimeResponse> byDayStudyTimes,
                                                   List<LocalDate> excludeDates) {
        return from(goal, methods).toBuilder()
                .byDayStudyTimes(byDayStudyTimes)
                .excludeDates(excludeDates)
                .build();
    }
}
