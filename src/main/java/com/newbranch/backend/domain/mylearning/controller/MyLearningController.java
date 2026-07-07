package com.newbranch.backend.domain.mylearning.controller;

import com.newbranch.backend.domain.mylearning.dto.MyLearningRequest;
import com.newbranch.backend.domain.mylearning.dto.MyLearningResponse;
import com.newbranch.backend.domain.mylearning.service.MyLearningService;
import com.newbranch.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "My Learning", description = "나만의 학습 API")
@RestController
@RequestMapping("/api/v1/my-learning")
@RequiredArgsConstructor
public class MyLearningController {

    private final MyLearningService myLearningService;

    @Operation(summary = "나만의 학습 조회", description = "등록된 회원 설정과 학습 유형을 조회합니다.")
    @GetMapping
    public ApiResponse<MyLearningResponse> getMyLearningData(
            @AuthenticationPrincipal UserDetails userDetails) {
        MyLearningResponse response = myLearningService.getMyLearningData(userDetails.getUsername());
        return ApiResponse.success(response);
    }

    @Operation(summary = "나만의 학습 등록", description = "회원 설정과 학습 유형을 등록합니다. 최초 1회만 가능합니다.")
    @PostMapping
    public ApiResponse<MyLearningResponse> createMyLearningData(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MyLearningRequest request) {
        MyLearningResponse response = myLearningService.createMyLearningData(userDetails.getUsername(), request);
        return ApiResponse.success(response);
    }

    @Operation(summary = "나만의 학습 수정", description = "등록된 회원 설정과 학습 유형을 수정합니다.")
    @PutMapping
    public ApiResponse<MyLearningResponse> updateMyLearningData(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MyLearningRequest request) {
        MyLearningResponse response = myLearningService.updateMyLearningData(userDetails.getUsername(), request);
        return ApiResponse.success(response);
    }

    @Operation(summary = "나만의 학습 삭제", description = "등록된 회원 설정과 학습 유형을 비활성화합니다.")
    @DeleteMapping
    public ApiResponse<Void> deleteMyLearningData(
            @AuthenticationPrincipal UserDetails userDetails) {
        myLearningService.deleteMyLearningData(userDetails.getUsername());
        return ApiResponse.success();
    }
}
