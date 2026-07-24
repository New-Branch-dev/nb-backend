package com.newbranch.backend.domain.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.newbranch.backend.domain.user.entity.EmailVerification;
import com.newbranch.backend.domain.user.repository.EmailVerificationRepository;
import com.newbranch.backend.global.config.JwtProperties;
import com.newbranch.backend.support.IntegrationTestSupport;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * QA 테스트케이스 표 1~13번 (Auth 도메인) 검증
 */
class AuthControllerTest extends IntegrationTestSupport {

    @Autowired
    private EmailVerificationRepository emailVerificationRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private EntityManager entityManager;

    // 1. 정상 회원가입
    @Test
    @DisplayName("미가입 이메일로 정상 값을 입력하면 회원가입에 성공한다")
    void signup_success() throws Exception {
        String suffix = uniqueSuffix();
        String body = """
                {"loginId":"user%s","email":"user%s@test.com","password":"password123!","nickname":"nick%s"}
                """.formatted(suffix, suffix, suffix);

        mockMvc.perform(post("/api/v1/auth/signup").contentType("application/json").content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.email").value("user" + suffix + "@test.com"));
    }

    // 2. 중복 이메일 회원가입
    @Test
    @DisplayName("이미 가입된 이메일로 회원가입하면 409 USER_ALREADY_EXISTS를 반환한다")
    void signup_duplicateEmail() throws Exception {
        IntegrationTestSupport.TestUser existing = signupAndLogin();
        String suffix = uniqueSuffix();
        String body = """
                {"loginId":"user%s","email":"%s","password":"password123!","nickname":"nick%s"}
                """.formatted(suffix, existing.email(), suffix);

        mockMvc.perform(post("/api/v1/auth/signup").contentType("application/json").content(body))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("error"))
                .andExpect(jsonPath("$.code").value("U002"));
    }

    // 3. 필수값 누락 (password 누락)
    @Test
    @DisplayName("password 없이 회원가입하면 400 validation 오류를 반환한다")
    void signup_missingPassword() throws Exception {
        String suffix = uniqueSuffix();
        String body = """
                {"loginId":"user%s","email":"user%s@test.com","nickname":"nick%s"}
                """.formatted(suffix, suffix, suffix);

        mockMvc.perform(post("/api/v1/auth/signup").contentType("application/json").content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C002"));
    }

    // 4. 비밀번호 형식 오류 (8자 미만)
    @Test
    @DisplayName("8자 미만 비밀번호로 회원가입하면 400 validation 오류를 반환한다")
    void signup_invalidPasswordFormat() throws Exception {
        String suffix = uniqueSuffix();
        String body = """
                {"loginId":"user%s","email":"user%s@test.com","password":"short1","nickname":"nick%s"}
                """.formatted(suffix, suffix, suffix);

        mockMvc.perform(post("/api/v1/auth/signup").contentType("application/json").content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C002"));
    }

    // 5. 정상 로그인
    @Test
    @DisplayName("가입된 계정으로 올바른 아이디/비밀번호를 입력하면 로그인에 성공하고 토큰이 발급된다")
    void login_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        assertThat(user.accessToken()).isNotBlank();
        assertThat(user.refreshToken()).isNotBlank();
    }

    // 6. 비밀번호 오류
    @Test
    @DisplayName("가입된 계정에 잘못된 비밀번호로 로그인하면 401 INVALID_CREDENTIALS를 반환한다")
    void login_wrongPassword() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        String body = """
                {"loginId":"%s","password":"wrongPassword1!"}
                """.formatted(user.loginId());

        mockMvc.perform(post("/api/v1/auth/login").contentType("application/json").content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("A005"));
    }

    // 7. 존재하지 않는 계정으로 로그인
    @Test
    @DisplayName("존재하지 않는 아이디로 로그인하면 401 INVALID_CREDENTIALS를 반환한다")
    void login_userNotFound() throws Exception {
        String body = """
                {"loginId":"no-such-user-%s","password":"password123!"}
                """.formatted(uniqueSuffix());

        mockMvc.perform(post("/api/v1/auth/login").contentType("application/json").content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("A005"));
    }

    // 8. 정상 로그아웃
    @Test
    @DisplayName("유효한 accessToken으로 로그아웃하면 200을 반환한다")
    void logout_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(post("/api/v1/auth/logout").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    // 9. 토큰 없이 로그아웃 요청
    @Test
    @DisplayName("Authorization 헤더 없이 로그아웃을 요청하면 401을 반환한다")
    void logout_withoutToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("A001"));
    }

    // 10. 정상 토큰 재발급 (refresh() email/loginId 버그 수정 검증)
    @Test
    @DisplayName("유효한 refreshToken으로 재발급하면 200과 함께 새 accessToken이 발급된다")
    void refresh_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        String body = """
                {"refreshToken":"%s"}
                """.formatted(user.refreshToken());

        MvcResult result = mockMvc.perform(post("/api/v1/auth/refresh").contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.nickname").value(user.nickname()))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        String newAccessToken = json.path("data").path("accessToken").asText();

        // 재발급된 accessToken이 실제로 인증에 사용 가능한지까지 검증한다 (createAuthentication 버그 회귀 테스트)
        mockMvc.perform(post("/api/v1/auth/logout").header("Authorization", bearer(newAccessToken)))
                .andExpect(status().isOk());
    }

    // 11-a. 위조된 refreshToken
    @Test
    @DisplayName("위조된 refreshToken으로 재발급 요청하면 401 INVALID_TOKEN을 반환한다")
    void refresh_forgedToken() throws Exception {
        String body = """
                {"refreshToken":"this.is.not-a-valid-jwt"}
                """;

        mockMvc.perform(post("/api/v1/auth/refresh").contentType("application/json").content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("A002"));
    }

    // 11-b. 만료된 refreshToken (REFRESH_TOKEN_EXPIRED 구분 처리 회귀 테스트)
    @Test
    @DisplayName("만료된 refreshToken으로 재발급 요청하면 401 REFRESH_TOKEN_EXPIRED를 반환한다")
    void refresh_expiredToken() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
        Date past = new Date(System.currentTimeMillis() - 10_000);
        String expiredToken = Jwts.builder()
                .subject(user.loginId())
                .claim("auth", "ROLE_USER")
                .issuer(jwtProperties.getIssuer())
                .issuedAt(new Date(past.getTime() - 1000))
                .expiration(past)
                .signWith(key)
                .compact();

        String body = """
                {"refreshToken":"%s"}
                """.formatted(expiredToken);

        mockMvc.perform(post("/api/v1/auth/refresh").contentType("application/json").content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("A004"));
    }

    // 12. 정상 이메일 인증 (verifyEmail() getActiveUserByEmail 버그 수정 검증)
    @Test
    @DisplayName("발송된 인증 코드로 이메일 인증을 완료하면 200을 반환한다")
    void verifyEmail_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        EmailVerification verification = emailVerificationRepository
                .findTopByEmailOrderByCreatedAtDesc(user.email())
                .orElseThrow();

        mockMvc.perform(get("/api/v1/auth/verify-email").param("token", verification.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    // 13. 만료된 인증 링크
    @Test
    @Transactional
    @DisplayName("만료된 인증 코드로 인증을 요청하면 400 EXPIRED_VERIFICATION_TOKEN을 반환한다")
    void verifyEmail_expiredToken() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        EmailVerification verification = emailVerificationRepository
                .findTopByEmailOrderByCreatedAtDesc(user.email())
                .orElseThrow();

        entityManager.createQuery(
                        "UPDATE EmailVerification e SET e.expiresAt = :expiresAt WHERE e.id = :id")
                .setParameter("expiresAt", LocalDateTime.now().minusMinutes(1))
                .setParameter("id", verification.getId())
                .executeUpdate();
        entityManager.flush();
        entityManager.clear();

        mockMvc.perform(get("/api/v1/auth/verify-email").param("token", verification.getToken()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("E002"));
    }
}
