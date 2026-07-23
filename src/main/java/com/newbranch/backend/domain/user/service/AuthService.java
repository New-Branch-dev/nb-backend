package com.newbranch.backend.domain.user.service;

import com.newbranch.backend.domain.user.dto.*;
import com.newbranch.backend.domain.user.entity.Role;
import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.domain.user.repository.UserRepository;
import com.newbranch.backend.global.config.JwtProperties;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import com.newbranch.backend.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 인증 관련 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;

    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    /**
     * 회원가입을 처리합니다
     */
    @Transactional
    public UserResponse signup(SignupRequest request) {
        validateDuplicateEmail(request.getEmail());

        User user = User.builder()
                .loginId(request.getLoginId())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .ageGroup(request.getAgeGroup())
                .schoolName(request.getSchoolName())
                .emailVerified(false)
                .role(Role.ROLE_USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered: {}", savedUser.getEmail());

        emailService.sendVerificationEmail(savedUser.getEmail());

        return UserResponse.from(savedUser);
    }

    /**
     * 로그인을 처리하고 JWT 토큰을 발급합니다
     */
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        Authentication authentication = authenticate(request);
        return generateTokens(authentication);
    }

    /**
     * Refresh Token으로 새로운 Access Token을 발급합니다
     */
    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        validateRefreshToken(refreshToken);

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        String storedToken = getStoredRefreshToken(email);

        if (!refreshToken.equals(storedToken)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        User user = getUserByEmail(email);
        Authentication authentication = createAuthentication(user);

        return generateTokens(authentication);
    }

    /**
     * 로그아웃을 처리합니다 (Refresh Token 삭제)
     */
    public void logout(String email) {
        deleteRefreshToken(email);
        log.info("User logged out: {}", email);
    }

    /**
     * 이메일 인증을 처리합니다
     */
    @Transactional
    public void verifyEmail(String token) {
        emailService.verifyEmail(token);

        String email = emailService.getEmailFromToken(token);
        User user = getUserByEmail(email);
        user.verifyEmail();

        log.info("User email verified: {}", email);
    }

    /**
     * 인증을 수행합니다
     */
    private Authentication authenticate(LoginRequest request) {
        try {
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getLoginId(), request.getPassword())
            );
        } catch (Exception e) {
            log.error("Authentication failed for user: {}", request.getLoginId());
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    /**
     * JWT 토큰을 생성하고 Refresh Token을 Redis에 저장합니다
     */
    private TokenResponse generateTokens(Authentication authentication) {
        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);
        String loginId = authentication.getName();

        storeRefreshToken(loginId, refreshToken);

        User user = getUserByLoginId(loginId);

        return TokenResponse.of(accessToken, refreshToken, jwtProperties.getAccessTokenExpiration(), user.getNickname());
    }

    /**
     * 사용자 로그인 ID를 조회합니다
     */
    private User getUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 이메일 중복을 검증합니다
     */
    public void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }
    }

    /**
     * 아이디 중복을 검증합니다
     */
    @Transactional(readOnly = true)
    public boolean validateDuplicateUserId(String loginId) {
        return userRepository.existsByLoginId(loginId);
    }

    /**
     * Refresh Token을 검증합니다
     */
    private void validateRefreshToken(String token) {
        if (!jwtTokenProvider.validateToken(token)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * 이메일로 사용자를 조회합니다
     */
    private User getUserByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 사용자 인증 객체를 생성합니다
     */
    private Authentication createAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(user.getLoginId(), null,
                Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getKey())));
    }

    /**
     * Refresh Token을 Redis에 저장합니다
     */
    private void storeRefreshToken(String email, String token) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(key, token,
                jwtProperties.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);
    }

    /**
     * Redis에서 Refresh Token을 조회합니다
     */
    private String getStoredRefreshToken(String email) {
        String key = REFRESH_TOKEN_PREFIX + email;
        String token = redisTemplate.opsForValue().get(key);

        if (token == null) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        return token;
    }

    /**
     * Redis에서 Refresh Token을 삭제합니다
     */
    private void deleteRefreshToken(String email) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.delete(key);
    }
}
