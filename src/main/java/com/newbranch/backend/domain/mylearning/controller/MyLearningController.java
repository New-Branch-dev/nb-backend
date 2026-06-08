package com.newbranch.backend.domain.mylearning.controller;

import com.newbranch.backend.domain.mylearning.dto.MyLearningRequest;
import com.newbranch.backend.domain.mylearning.dto.MyLearningResponse;
import com.newbranch.backend.domain.mylearning.service.MyLearningService;
import com.newbranch.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "My Learning", description = "나만의 학습 API")
@RestController
@RequestMapping("/api/v1/mylearning")
@RequiredArgsConstructor
public class MyLearningController {

    private final MyLearningService myLearningService;

    @Operation(summary = "회원 설정, 학습유형 조회")
    @GetMapping
    public ApiResponse<MyLearningResponse> getMyLearningData(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(myLearningService.getMyLearningData(userDetails.getUsername()));
    }

    @Operation(summary = "회원 설정, 학습유형 생성")
    @PostMapping
    public ApiResponse<MyLearningResponse> createMyLearningData(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MyLearningRequest dto) {
        return ApiResponse.success(myLearningService.createMyLearningData(userDetails.getUsername(), dto));
    }

    @Operation(summary = "회원 설정, 학습유형 수정")
    @PutMapping
    public ApiResponse<MyLearningResponse> updateMyLearningData(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody MyLearningRequest dto) {
        return ApiResponse.success(myLearningService.updateMyLearningData(userDetails.getUsername(), dto));
    }

    @Operation(summary = "회원 설정, 학습유형 삭제")
    @DeleteMapping
    public ApiResponse<Void> deleteMyLearningData(@AuthenticationPrincipal UserDetails userDetails) {
        myLearningService.deleteMyLearningData(userDetails.getUsername());
        return ApiResponse.success(null);
    }
}
