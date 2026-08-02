package com.newbranch.backend.global.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * JWT 설정 속성
 * application.properties의 jwt.* 속성을 바인딩
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    // fix: JWT_SECRET_KEY 미설정 시 이 파일에 그대로 커밋된 placeholder 값으로 조용히
    // 기동돼서, 서명 키가 공개된 채 서비스가 뜨는 걸 막기 위해 기동 시점에 검증 (siwon)
    private static final String PLACEHOLDER_SECRET =
            "your-secret-key-must-be-at-least-256-bits-long-for-HS256-algorithm-change-this-in-production";
    private static final int MIN_SECRET_BYTES = 32; // HS256 최소 256비트

    /**
     * JWT 서명에 사용할 비밀 키 (최소 256비트)
     */
    private String secretKey;

    /**
     * Access Token 만료 시간 (밀리초)
     * 기본값: 15분 (900000ms)
     */
    private Long accessTokenExpiration;

    /**
     * Refresh Token 만료 시간 (밀리초)
     * 기본값: 7일 (604800000ms)
     */
    private Long refreshTokenExpiration;

    /**
     * JWT 발행자
     */
    private String issuer;

    @PostConstruct
    public void validate() {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("JWT_SECRET_KEY가 설정되지 않았습니다.");
        }
        if (PLACEHOLDER_SECRET.equals(secretKey)) {
            throw new IllegalStateException("JWT_SECRET_KEY가 기본 placeholder 값입니다. 환경변수로 반드시 재설정하세요.");
        }
        if (secretKey.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
            throw new IllegalStateException("JWT_SECRET_KEY는 최소 256비트(32바이트) 이상이어야 합니다.");
        }
    }
}
