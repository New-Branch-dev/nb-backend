package com.newbranch.backend.domain.goal.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class MemorizationPlanRequest {
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer repetitionCount;
    private String method;
}