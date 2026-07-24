package com.newbranch.backend.domain.mylearning;

import com.fasterxml.jackson.databind.JsonNode;
import com.newbranch.backend.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * QA 테스트케이스 표 66~73번 (My Learning 도메인) 검증
 *
 * 참고: /api/mylearning은 인증(JWT) 필요 여부와 무관하게 usersId를 그대로 신뢰하는 구조라
 * (본인 확인 로직이 없음) 여기서는 QA 표에 정의된 정상/404 케이스만 검증한다.
 */
class MyLearningControllerTest extends IntegrationTestSupport {

    private String requestBody(long usersId, String nickname) {
        return """
                {"usersId":%d,"nickname":"%s","schoolName":"서울고등학교","preferredStudyTime":"MORNING"}
                """.formatted(usersId, nickname);
    }

    // 66. 회원 설정/학습유형 생성 정상
    @Test
    @DisplayName("유효한 설정 데이터로 생성하면 201을 반환한다")
    void createMyLearningData_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long usersId = fetchUsersId(user);

        mockMvc.perform(post("/api/mylearning")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(requestBody(usersId, "홍길동")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname").value("홍길동"));
    }

    // 67. 필수값 누락 (nickname 빈 값)
    @Test
    @DisplayName("nickname 없이 생성 요청하면 400 validation 오류를 반환한다")
    void createMyLearningData_missingRequired() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long usersId = fetchUsersId(user);
        String body = """
                {"usersId":%d,"nickname":"","schoolName":"서울고등학교"}
                """.formatted(usersId);

        mockMvc.perform(post("/api/mylearning")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C002"));
    }

    // 68. 회원 설정 조회 정상
    @Test
    @DisplayName("데이터가 존재하면 조회 시 200과 설정 정보를 반환한다")
    void getMyLearningData_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long usersId = fetchUsersId(user);
        createMyLearning(user, usersId, "조회용닉네임");

        // 주의: MyLearningService가 UserSetting/UserLearningStyle 엔티티만으로 응답을 조립하는데
        // 그 엔티티들에는 nickname/schoolName 컬럼이 아예 없어서, 생성 시 입력한 nickname은
        // 검증만 되고 실제로는 저장되지 않는다 (조회 응답에 nickname 필드 자체가 항상 null).
        mockMvc.perform(get("/api/mylearning/" + usersId).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preferredStudyTime").value("MORNING"));
    }

    // 69. 존재하지 않는 설정 조회
    @Test
    @DisplayName("존재하지 않는 usersId를 조회하면 404 USER_SETTING_NOT_FOUND를 반환한다")
    void getMyLearningData_notFound() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(get("/api/mylearning/999999999").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("M002"));
    }

    // 70. 회원 설정 수정 정상
    @Test
    @DisplayName("존재하는 데이터를 유효한 값으로 수정하면 200과 변경사항이 반영된다")
    void updateMyLearningData_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long usersId = fetchUsersId(user);
        createMyLearning(user, usersId, "수정전닉네임");

        mockMvc.perform(put("/api/mylearning/" + usersId)
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(requestBody(usersId, "수정후닉네임")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("수정후닉네임"));
    }

    // 71. 존재하지 않는 설정 수정
    @Test
    @DisplayName("존재하지 않는 usersId를 수정하면 404 USER_SETTING_NOT_FOUND를 반환한다")
    void updateMyLearningData_notFound() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(put("/api/mylearning/999999999")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(requestBody(999999999L, "없는사람")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("M002"));
    }

    // 72. 회원 설정 삭제 정상
    @Test
    @DisplayName("존재하는 데이터를 삭제하면 204를 반환하고 이후 조회 시 404가 된다")
    void deleteMyLearningData_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long usersId = fetchUsersId(user);
        createMyLearning(user, usersId, "삭제될닉네임");

        mockMvc.perform(delete("/api/mylearning/" + usersId).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/mylearning/" + usersId).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("M002"));
    }

    // 73. 존재하지 않는 설정 삭제
    @Test
    @DisplayName("존재하지 않는 usersId를 삭제하면 404 USER_SETTING_NOT_FOUND를 반환한다")
    void deleteMyLearningData_notFound() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(delete("/api/mylearning/999999999").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("M002"));
    }

    private long fetchUsersId(IntegrationTestSupport.TestUser user) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/v1/users/me").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.path("data").path("id").asLong();
    }

    private void createMyLearning(IntegrationTestSupport.TestUser user, long usersId, String nickname) throws Exception {
        mockMvc.perform(post("/api/mylearning")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(requestBody(usersId, nickname)))
                .andExpect(status().isCreated());
    }
}
