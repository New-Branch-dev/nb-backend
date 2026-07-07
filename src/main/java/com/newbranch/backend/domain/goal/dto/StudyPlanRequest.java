package com.newbranch.backend.domain.goal.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class StudyPlanRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer repetitionCount;
    private String method;
}