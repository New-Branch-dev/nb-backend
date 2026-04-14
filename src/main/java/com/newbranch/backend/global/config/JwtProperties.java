package com.newbranch.backend.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 설정 속성
 * application.properties의 jwt.* 속성을 바인딩
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

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
}
