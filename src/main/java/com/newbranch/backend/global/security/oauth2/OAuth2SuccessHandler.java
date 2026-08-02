package com.newbranch.backend.global.security.oauth2;

import com.newbranch.backend.global.config.JwtProperties;
import com.newbranch.backend.global.security.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

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
        String loginId = principal.getUser().getLoginId();
        String nickname = URLEncoder.encode(principal.getUser().getNickname(), StandardCharsets.UTF_8);

        // JWT subject / Redis 키는 시스템 전반에서 loginId를 기준으로 삼으므로(일반 로그인·refresh·
        // JwtAuthenticationFilter 전부 loginId 기준), OAuth2User의 getName()(Google sub 값)을 그대로
        // 쓰지 않고 loginId로 새 Authentication을 만들어 토큰을 발급한다.
        Authentication tokenAuthentication = new UsernamePasswordAuthenticationToken(
                loginId, null, Collections.singletonList(
                        new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                principal.getUser().getRole().getKey())));

        String accessToken = jwtTokenProvider.createAccessToken(tokenAuthentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(tokenAuthentication);

        storeRefreshToken(loginId, refreshToken);

        // fix: 토큰을 쿼리스트링으로 보내면 브라우저 히스토리/서버·프록시 접근로그/Referer 헤더로
        // 유출될 수 있어(특히 refresh token) 서버로 전송되지 않는 URL fragment로 이동 (siwon)
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUrl)
                .build()
                .toUriString()
                + "#accessToken=" + URLEncoder.encode(accessToken, StandardCharsets.UTF_8)
                + "&refreshToken=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8)
                + "&nickname=" + nickname;

        log.info("OAuth2 login success for user: {}", loginId);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    /**
     * Refresh Token을 Redis에 저장합니다
     */
    private void storeRefreshToken(String loginId, String token) {
        String key = REFRESH_TOKEN_PREFIX + loginId;
        redisTemplate.opsForValue().set(key, token,
                jwtProperties.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);
    }
}