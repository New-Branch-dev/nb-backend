package com.newbranch.backend.domain.goal.controller;

import com.newbranch.backend.domain.goal.dto.RetrievalPlanRequest;
import com.newbranch.backend.domain.goal.dto.RetrievalPlanResponse;
import com.newbranch.backend.domain.goal.service.RetrievalPlanService;
import com.newbranch.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Retrieval Plan", description = "인출 계획 API")
@RestController
@RequestMapping("/api/v1/goals/{lgId}/retrieval")
@RequiredArgsConstructor
public class RetrievalPlanController {

    private final RetrievalPlanService retrievalPlanService;

    @Operation(summary = "인출 계획 수정")
    @PutMapping("/{rpId}")
    public ApiResponse<RetrievalPlanResponse> updatePlan(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long lgId,
            @PathVariable Long rpId,
            @RequestBody RetrievalPlanRequest request) {
        return ApiResponse.success(retrievalPlanService.updatePlan(userDetails.getUsername(), lgId, rpId, request));
    }
}
