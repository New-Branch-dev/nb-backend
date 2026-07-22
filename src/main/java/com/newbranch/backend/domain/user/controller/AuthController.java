package com.newbranch.backend.domain.user.controller;

import com.newbranch.backend.domain.user.dto.*;
import com.newbranch.backend.domain.user.service.AuthService;
import com.newbranch.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API 컨트롤러
 */
@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     * [F-L-001] 회원/로그인 > 로그인 > 회원가입
     */
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    @PostMapping("/signup")
    public ApiResponse<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
        UserResponse response = authService.signup(request);
        return ApiResponse.success(response);
    }

    /**
     * 로그인 및 토큰 발급
     * [F-L-002] 회원/로그인 > 로그인 > 로그인 및 토큰 발급
     */
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다")
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authService.login(request);
        return ApiResponse.success(response);
    }

    /**
     * Access Token 갱신
     * [F-L-003] 회원/로그인 > 회원가입 > Access Token 갱신
     */
    @Operation(summary = "토큰 재발급", description = "Refresh Token으로 새로운 Access Token을 발급받습니다")
    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        TokenResponse response = authService.refresh(request.getRefreshToken());
        return ApiResponse.success(response);
    }

    /**
     * 로그아웃
     * [F-L-004] 회원/로그인 > 로그아웃 > 로그아웃
     */
    @Operation(summary = "로그아웃", description = "Refresh Token을 무효화합니다")
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@AuthenticationPrincipal UserDetails userDetails) {
        authService.logout(userDetails.getUsername());
        return ApiResponse.success();
    }

    /**
     * 이메일 인증
     */
    @Operation(summary = "이메일 인증", description = "이메일 인증 토큰으로 이메일을 인증합니다")
    @GetMapping("/verify-email")
    public ApiResponse<Void> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ApiResponse.success();
    }

    /**
     * 이메일 인증번호 발송 요청
     */
    @Operation(summary = "이메일 인증번호 발송", description = "입력한 이메일로 6자리 인증번호를 발송합니다")
    @PostMapping("/email/request")
    public ApiResponse<Void> requestEmailVerification(@RequestParam String email) {
        authService.validateDuplicateEmail(email);
        emailService.sendVerificationEmail(email);
        return ApiResponse.success();
    }

    /**
     * 이메일 인증번호 검증 확인
     */
    @Operation(summary = "이메일 인증번호 검증", description = "사용자가 입력한 6자리 인증번호를 검증합니다")
    @PostMapping("/email/verify")
    public ApiResponse<Void> verifyEmailCode(@RequestParam String email, @RequestParam String code) {
        emailService.verifyEmailCode(email, code);
        return ApiResponse.success();
    }

    /**
     * 아이디 중복 확인
     */
    @Operation(summary = "아이디 중복 확인", description = "입력한 아이디가 이미 존재하는지 확인합니다")
    @GetMapping("/check-userid")
    public ApiResponse<Boolean> checkUserid(@RequestParam String loginId) {
        boolean isDuplicate = authService.validateDuplicateUserId(loginId);
        return ApiResponse.success(isDuplicate);
    }
}
