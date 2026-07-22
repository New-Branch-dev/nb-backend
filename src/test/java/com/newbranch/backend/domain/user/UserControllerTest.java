package com.newbranch.backend.domain.user;

import com.newbranch.backend.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * QA 테스트케이스 표 14~19번 (User 도메인) 검증
 */
class UserControllerTest extends IntegrationTestSupport {

    // 14. 내 정보 조회 정상
    @Test
    @DisplayName("로그인 상태에서 내 정보를 조회하면 200과 사용자 정보를 반환한다")
    void getCurrentUser_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(get("/api/v1/users/me").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.email").value(user.email()))
                .andExpect(jsonPath("$.data.nickname").value(user.nickname()));
    }

    // 15. 인증 없이 조회
    @Test
    @DisplayName("토큰 없이 내 정보를 조회하면 401을 반환한다")
    void getCurrentUser_withoutToken() throws Exception {
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("A001"));
    }

    // 16. 내 정보 수정 정상
    @Test
    @DisplayName("로그인 상태에서 유효한 데이터로 내 정보를 수정하면 200과 변경사항이 반영된다")
    void updateCurrentUser_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        String body = """
                {"nickname":"newNick","ageGroup":"20대","schoolName":"새학교"}
                """;

        mockMvc.perform(put("/api/v1/users/me")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("newNick"))
                .andExpect(jsonPath("$.data.schoolName").value("새학교"));
    }

    // 17. 잘못된 형식 값 (newPassword 8자 미만 - UpdateUserRequest에서 형식 검증이 걸리는 실제 필드)
    @Test
    @DisplayName("형식에 맞지 않는 값으로 내 정보를 수정하면 400 validation 오류를 반환한다")
    void updateCurrentUser_invalidFormat() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        String body = """
                {"newPassword":"short1"}
                """;

        mockMvc.perform(put("/api/v1/users/me")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C002"));
    }

    // 18. 회원 탈퇴 정상
    @Test
    @DisplayName("로그인 상태에서 회원 탈퇴를 요청하면 200을 반환하고 계정이 비활성화된다")
    void deleteCurrentUser_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(delete("/api/v1/users/me").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk());

        // 탈퇴(비활성화) 후에는 JwtAuthenticationFilter가 CustomUserDetailsService에서
        // 활성 사용자를 찾지 못해 인증 자체를 구성하지 못하므로, 서비스 계층 404가 아니라
        // 401(A001)로 필터 단에서 걸러진다.
        mockMvc.perform(get("/api/v1/users/me").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("A001"));
    }

    // 19. 인증 없이 탈퇴 요청
    @Test
    @DisplayName("토큰 없이 회원 탈퇴를 요청하면 401을 반환한다")
    void deleteCurrentUser_withoutToken() throws Exception {
        mockMvc.perform(delete("/api/v1/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("A001"));
    }
}
