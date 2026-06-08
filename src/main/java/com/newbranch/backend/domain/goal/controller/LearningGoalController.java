package com.newbranch.backend.domain.goal.controller;

import com.newbranch.backend.domain.goal.dto.AiStudyPlanResponse;
import com.newbranch.backend.domain.goal.dto.DailyStudyTimeRequest;
import com.newbranch.backend.domain.goal.dto.DailyStudyTimeResponse;
import com.newbranch.backend.domain.goal.dto.DifficultyAnalysisResponse;
import com.newbranch.backend.domain.goal.dto.FormativeAssessmentResponse;
import com.newbranch.backend.domain.goal.dto.LearningGoalRequest;
import com.newbranch.backend.domain.goal.dto.LearningGoalResponse;
import com.newbranch.backend.domain.goal.service.AiDifficultyAnalysisService;
import com.newbranch.backend.domain.goal.service.AiFormativeAssessmentService;
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

import java.util.List;

@Tag(name = "Learning Goal", description = "학습 목표 API")
@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
public class LearningGoalController {

    private final LearningGoalService learningGoalService;
    private final AiStudyPlanService aiStudyPlanService;
    private final AiDifficultyAnalysisService aiDifficultyAnalysisService;
    private final AiFormativeAssessmentService aiFormativeAssessmentService;

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

    // 일별 공부 시간 수정
    @Operation(summary = "일별 공부 시간 수정", description = "학습 목표의 일별 공부 가능 시간을 수정합니다. 기존 데이터는 모두 교체됩니다.")
    @PutMapping("/{lgId}/daily-study-times")
    public ApiResponse<List<DailyStudyTimeResponse>> updateDailyStudyTimes(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId,
            @Valid @RequestBody DailyStudyTimeRequest request) {
        return ApiResponse.success(learningGoalService.updateDailyStudyTimes(userDetails.getUsername(), lgId, request));
    }

    // F-GO-002: AI 학습 계획 생성
    @Operation(summary = "AI 학습 계획 생성", description = "학습 목표 정보를 바탕으로 AI가 일별 학습 계획을 생성합니다.")
    @PostMapping("/{lgId}/ai/study-plan")
    public ApiResponse<AiStudyPlanResponse> generateStudyPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId) {
        return ApiResponse.success(aiStudyPlanService.generateStudyPlan(userDetails.getUsername(), lgId));
    }

    // F-GO-002: AI 학습 계획 조회
    @Operation(summary = "AI 학습 계획 조회", description = "저장된 AI 학습 계획을 조회합니다.")
    @GetMapping("/{lgId}/ai/study-plan")
    public ApiResponse<AiStudyPlanResponse> getStudyPlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId) {
        return ApiResponse.success(aiStudyPlanService.getStudyPlan(userDetails.getUsername(), lgId));
    }

    // F-GO-002: AI 학습 난이도 분석
    @Operation(summary = "AI 학습 난이도 분석", description = "노트 자료를 바탕으로 시험 특징과 기출 경향을 분석합니다.")
    @PostMapping("/{lgId}/ai/difficulty-analysis")
    public ApiResponse<DifficultyAnalysisResponse> analyzeDifficulty(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId) {
        return ApiResponse.success(aiDifficultyAnalysisService.analyze(userDetails.getUsername(), lgId));
    }

    // F-GO-002: AI 학습 난이도 분석 조회
    @Operation(summary = "AI 학습 난이도 분석 조회", description = "저장된 난이도 분석 결과를 조회합니다.")
    @GetMapping("/{lgId}/ai/difficulty-analysis")
    public ApiResponse<DifficultyAnalysisResponse> getDifficultyAnalysis(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId) {
        return ApiResponse.success(aiDifficultyAnalysisService.getAnalysis(userDetails.getUsername(), lgId));
    }

    // F-GO-002: AI 형성평가 생성
    @Operation(summary = "AI 형성평가 생성", description = "기출 자료를 바탕으로 난이도별 형성평가 문제를 생성합니다.")
    @PostMapping("/{lgId}/ai/formative-assessment")
    public ApiResponse<FormativeAssessmentResponse> generateFormativeAssessment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId) {
        return ApiResponse.success(aiFormativeAssessmentService.generate(userDetails.getUsername(), lgId));
    }

    // F-GO-002: AI 형성평가 조회
    @Operation(summary = "AI 형성평가 조회", description = "저장된 형성평가를 조회합니다.")
    @GetMapping("/{lgId}/ai/formative-assessment")
    public ApiResponse<FormativeAssessmentResponse> getFormativeAssessment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId) {
        return ApiResponse.success(aiFormativeAssessmentService.getAssessment(userDetails.getUsername(), lgId));
    }
}
