package com.newbranch.backend.domain.goal.controller;

import com.newbranch.backend.domain.goal.dto.DailyStudyTimeRequest;
import com.newbranch.backend.domain.goal.dto.DailyStudyTimeResponse;
import com.newbranch.backend.domain.goal.dto.StudyPlanRequest;
import com.newbranch.backend.domain.goal.dto.StudyPlanResponse;
import com.newbranch.backend.domain.goal.service.StudyPlanService;
import com.newbranch.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Study Plan", description = "기타학습 계획 API")
@RestController
@RequestMapping("/api/v1/goals/{lgId}/study-plans")
@RequiredArgsConstructor
public class StudyPlanController {

    private final StudyPlanService studyPlanService;

    @Operation(summary = "기타학습 계획 수정")
    @PutMapping("/{spId}")
    public ApiResponse<StudyPlanResponse> updatePlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId,
            @PathVariable Long spId,
            @RequestBody StudyPlanRequest request) {
        return ApiResponse.success(studyPlanService.updatePlan(userDetails.getUsername(), lgId, spId, request));
    }

    @Operation(summary = "일별 공부시간 설정", description = "날짜별 가능한 공부 시간을 일괄 저장합니다")
    @PostMapping("/study-times")
    public ApiResponse<List<DailyStudyTimeResponse>> saveStudyTimes(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId,
            @Valid @RequestBody DailyStudyTimeRequest request) {
        return ApiResponse.success(studyPlanService.saveStudyTimes(userDetails.getUsername(), lgId, request));
    }
}
