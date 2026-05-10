package com.newbranch.backend.global.security.oauth2;

import com.newbranch.backend.global.config.JwtProperties;
import com.newbranch.backend.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * OAuth2 로그인 성공 핸들러
 * JWT 토큰을 생성하고 클라이언트로 리다이렉트합니다
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "RT:";

    @Value("${oauth2.redirect-url}")
    private String redirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2UserPrincipal principal = (OAuth2UserPrincipal) authentication.getPrincipal();
        String email = principal.getEmail();
        String nickname = URLEncoder.encode(principal.getUser().getNickname(), StandardCharsets.UTF_8);

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        storeRefreshToken(email, refreshToken);

        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .queryParam("nickname", nickname)
                .build()
                .toUriString();

        log.info("OAuth2 login success for user: {}", email);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * Refresh Token을 Redis에 저장합니다
     */
    private void storeRefreshToken(String email, String token) {
        String key = REFRESH_TOKEN_PREFIX + email;
        redisTemplate.opsForValue().set(key, token,
                jwtProperties.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);
    }
}