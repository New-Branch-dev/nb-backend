package com.newbranch.backend.domain.consolidation.controller;

import com.newbranch.backend.domain.consolidation.dto.ConsolidationRequest;
import com.newbranch.backend.domain.consolidation.dto.ConsolidationResponse;
import com.newbranch.backend.domain.consolidation.service.ConsolidationService;
import com.newbranch.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Consolidation", description = "단권화 API")
@RestController
@RequestMapping("/api/v1/consolidations")
@RequiredArgsConstructor
public class ConsolidationController {

    private final ConsolidationService consolidationService;

    @Operation(summary = "단권화 목록 조회", description = "사용자의 활성화된 단권화 목록을 조회합니다")
    @GetMapping
    public ApiResponse<List<ConsolidationResponse>> getConsolidations(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(consolidationService.getConsolidations(userDetails.getUsername()));
    }

    @Operation(summary = "단권화 상세 조회", description = "단권화 상세 정보와 하위 자료, 매핑된 첨부파일을 조회합니다")
    @GetMapping("/{coId}")
    public ApiResponse<ConsolidationResponse> getConsolidation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long coId) {
        return ApiResponse.success(consolidationService.getConsolidation(userDetails.getUsername(), coId));
    }

    @Operation(summary = "단권화 등록", description = "단권화 메인 정보, 첨부파일 매핑, 하위 자료를 함께 등록합니다")
    @PostMapping
    public ApiResponse<ConsolidationResponse> createConsolidation(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ConsolidationRequest request) {
        return ApiResponse.success(consolidationService.createConsolidation(userDetails.getUsername(), request));
    }

    @Operation(summary = "단권화 수정", description = "단권화 텍스트 정보 및 하위 자료를 수정합니다")
    @PutMapping("/{coId}")
    public ApiResponse<ConsolidationResponse> updateConsolidation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long coId,
            @Valid @RequestBody ConsolidationRequest request) {
        return ApiResponse.success(consolidationService.updateConsolidation(userDetails.getUsername(), coId, request));
    }

    @Operation(summary = "단권화 삭제", description = "단권화를 소프트 삭제합니다 (is_active = false)")
    @DeleteMapping("/{coId}")
    public ApiResponse<Void> deleteConsolidation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long coId) {
        consolidationService.deleteConsolidation(userDetails.getUsername(), coId);
        return ApiResponse.success();
    }
}
