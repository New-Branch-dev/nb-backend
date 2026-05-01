package com.newbranch.backend.domain.user.controller;

import com.newbranch.backend.domain.user.dto.UpdateUserRequest;
import com.newbranch.backend.domain.user.dto.UserResponse;
import com.newbranch.backend.domain.user.service.UserService;
import com.newbranch.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 관리 API 컨트롤러
 */
@Tag(name = "User", description = "사용자 관리 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다")
    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserResponse response = userService.getCurrentUser(userDetails.getUsername());
        return ApiResponse.success(response);
    }

    /**
     * 현재 로그인한 사용자 정보 수정
     */
    @Operation(summary = "내 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다")
    @PutMapping("/me")
    public ApiResponse<UserResponse> updateCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateUserRequest request) {
        UserResponse response = userService.updateCurrentUser(
                userDetails.getUsername(), request);
        return ApiResponse.success(response);
    }

    /**
     * 회원 탈퇴
     */
    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자를 탈퇴 처리합니다 (소프트 삭제)")
    @DeleteMapping("/me")
    public ApiResponse<Void> deleteCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.deleteCurrentUser(userDetails.getUsername());
        return ApiResponse.success();
    }
}