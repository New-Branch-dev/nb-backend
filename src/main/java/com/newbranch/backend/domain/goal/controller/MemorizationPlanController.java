package com.newbranch.backend.domain.goal.controller;

import com.newbranch.backend.domain.goal.dto.MemorizationPlanRequest;
import com.newbranch.backend.domain.goal.dto.MemorizationPlanResponse;
import com.newbranch.backend.domain.goal.service.MemorizationPlanService;
import com.newbranch.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Memorization Plan", description = "암기 계획 API")
@RestController
@RequestMapping("/api/v1/goals/{lgId}/memorization")
@RequiredArgsConstructor
public class MemorizationPlanController {

    private final MemorizationPlanService memorizationPlanService;

    @Operation(summary = "암기 계획 수정")
    @PutMapping("/{mpId}")
    public ApiResponse<MemorizationPlanResponse> updatePlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId,
            @PathVariable Long mpId,
            @RequestBody MemorizationPlanRequest request) {
        return ApiResponse.success(memorizationPlanService.updatePlan(userDetails.getUsername(), lgId, mpId, request));
    }
}
