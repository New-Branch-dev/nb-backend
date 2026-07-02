package com.newbranch.backend.domain.goal.controller;

import com.newbranch.backend.domain.goal.dto.*;
import com.newbranch.backend.domain.goal.service.AiStudyPlanService;
import com.newbranch.backend.domain.goal.service.LearningGoalService;
import com.newbranch.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Learning Goal", description = "학습 목표 API")
@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class LearningGoalController {

    private final LearningGoalService learningGoalService;
    private final AiStudyPlanService aiStudyPlanService;

    @Operation(summary = "학습 목표 목록 조회")
    @GetMapping
    public ApiResponse<List<LearningGoalResponse>> getGoals(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(learningGoalService.getGoals(userDetails.getUsername()));
    }

    @Operation(summary = "학습 목표 생성")
    @PostMapping
    public ApiResponse<LearningGoalResponse> createGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody LearningGoalRequest request) {
        return ApiResponse.success(learningGoalService.createGoal(userDetails.getUsername(), request));
    }

    @Operation(summary = "학습 목표 상세 조회")
    @GetMapping("/{lgId}")
    public ApiResponse<LearningGoalResponse> getGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId) {
        return ApiResponse.success(learningGoalService.getGoal(userDetails.getUsername(), lgId));
    }

    @Operation(summary = "학습 목표 검색")
    @GetMapping("/search")
    public ApiResponse<List<LearningGoalResponse>> searchGoals(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String keyword) {
        return ApiResponse.success(learningGoalService.searchGoals(userDetails.getUsername(), keyword));
    }

    @Operation(summary = "학습 목표 삭제")
    @DeleteMapping("/{lgId}")
    public ApiResponse<Void> deleteGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId) {
        learningGoalService.deleteGoal(userDetails.getUsername(), lgId);
        return ApiResponse.success();
    }

    @Operation(summary = "학습 목표 수정")
    @PutMapping("/{lgId}")
    public ApiResponse<LearningGoalResponse> updateGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId,
            @Valid @RequestBody LearningGoalRequest request) {
        return ApiResponse.success(learningGoalService.updateGoal(userDetails.getUsername(), lgId, request));
    }

    @Operation(summary = "요일별 공부 시간 수정")
    @PutMapping("/{lgId}/byday-study-times")
    public ApiResponse<List<ByDayStudyTimeResponse>> updateByDayStudyTimes(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId,
            @RequestBody List<LearningGoalRequest.ByDayStudyTimeEntry> entries) {
        return ApiResponse.success(learningGoalService.updateByDayStudyTimes(userDetails.getUsername(), lgId, entries));
    }

    @Operation(summary = "제외 날짜 수정")
    @PutMapping("/{lgId}/exclude-dates")
    public ApiResponse<List<LocalDate>> updateExcludeDates(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId,
            @RequestBody List<LocalDate> excludeDates) {
        return ApiResponse.success(learningGoalService.updateExcludeDates(userDetails.getUsername(), lgId, excludeDates));
    }

    @Operation(summary = "AI 학습 계획 생성")
    @PostMapping("/{lgId}/ai/study-plan")
    public ApiResponse<AiStudyPlanResponse> generateStudyPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId) {
        return ApiResponse.success(aiStudyPlanService.generateStudyPlan(userDetails.getUsername(), lgId));
    }

    @Operation(summary = "AI 학습 계획 저장")
    @PostMapping("/{lgId}/ai/study-plan/save")
    public ApiResponse<AiStudyPlanResponse> saveStudyPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId,
            @RequestBody AiStudyPlanResponse plan) {
        return ApiResponse.success(aiStudyPlanService.saveStudyPlan(userDetails.getUsername(), lgId, plan));
    }

    @Operation(summary = "AI 학습 계획 조회")
    @GetMapping("/{lgId}/ai/study-plan")
    public ApiResponse<AiStudyPlanResponse> getStudyPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId) {
        return ApiResponse.success(aiStudyPlanService.getStudyPlan(userDetails.getUsername(), lgId));
    }
}
