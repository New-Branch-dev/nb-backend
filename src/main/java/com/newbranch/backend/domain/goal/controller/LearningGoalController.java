package com.newbranch.backend.domain.goal.controller;

import com.newbranch.backend.domain.goal.dto.LearningGoalRequest;
import com.newbranch.backend.domain.goal.dto.LearningGoalResponse;
import com.newbranch.backend.domain.goal.service.LearningGoalService;
import com.newbranch.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Learning Goal", description = "학습 목표 API")
@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class LearningGoalController {

    private final LearningGoalService learningGoalService;

    // F-GO-001: 학습 목표 목록 조회
    @Operation(summary = "학습 목표 목록 조회")
    @GetMapping
    public ApiResponse<List<LearningGoalResponse>> getGoals(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(learningGoalService.getGoals(userDetails.getUsername()));
    }

    // F-GO-002: 학습 목표 생성
    @Operation(summary = "학습 목표 생성")
    @PostMapping
    public ApiResponse<LearningGoalResponse> createGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody LearningGoalRequest request) {
        return ApiResponse.success(learningGoalService.createGoal(userDetails.getUsername(), request));
    }

    // F-GO-003: 학습 목표 상세 조회
    @Operation(summary = "학습 목표 상세 조회")
    @GetMapping("/{lgId}")
    public ApiResponse<LearningGoalResponse> getGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId) {
        return ApiResponse.success(learningGoalService.getGoal(userDetails.getUsername(), lgId));
    }

    // F-GO-004: 학습 목표 검색
    @Operation(summary = "학습 목표 검색", description = "제목 기반 검색")
    @GetMapping("/search")
    public ApiResponse<List<LearningGoalResponse>> searchGoals(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String keyword) {
        return ApiResponse.success(learningGoalService.searchGoals(userDetails.getUsername(), keyword));
    }

    // F-GO-005: 학습 목표 삭제
    @Operation(summary = "학습 목표 삭제")
    @DeleteMapping("/{lgId}")
    public ApiResponse<Void> deleteGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId) {
        learningGoalService.deleteGoal(userDetails.getUsername(), lgId);
        return ApiResponse.success();
    }

    // F-GO-006: 학습 목표 수정
    @Operation(summary = "학습 목표 수정")
    @PutMapping("/{lgId}")
    public ApiResponse<LearningGoalResponse> updateGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId,
            @Valid @RequestBody LearningGoalRequest request) {
        return ApiResponse.success(learningGoalService.updateGoal(userDetails.getUsername(), lgId, request));
    }
}
