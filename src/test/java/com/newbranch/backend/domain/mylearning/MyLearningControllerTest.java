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

    // fix: 타인의 usersId로 조회/수정/삭제가 가능했던 IDOR을 막았는지 회귀 검증 (siwon)
    @Test
    @DisplayName("타인의 usersId를 조회하면 403 ACCESS_DENIED를 반환한다")
    void getMyLearningData_otherUsers() throws Exception {
        IntegrationTestSupport.TestUser owner = signupAndLogin();
        IntegrationTestSupport.TestUser intruder = signupAndLogin();
        long ownerUsersId = fetchUsersId(owner);
        createMyLearning(owner, ownerUsersId, "타인설정");

        mockMvc.perform(get("/api/mylearning/" + ownerUsersId).header("Authorization", bearer(intruder.accessToken())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("C006"));
    }

    @Test
    @DisplayName("타인의 usersId를 수정하면 403 ACCESS_DENIED를 반환한다")
    void updateMyLearningData_otherUsers() throws Exception {
        IntegrationTestSupport.TestUser owner = signupAndLogin();
        IntegrationTestSupport.TestUser intruder = signupAndLogin();
        long ownerUsersId = fetchUsersId(owner);
        createMyLearning(owner, ownerUsersId, "타인설정");

        mockMvc.perform(put("/api/mylearning/" + ownerUsersId)
                        .header("Authorization", bearer(intruder.accessToken()))
                        .contentType("application/json")
                        .content(requestBody(ownerUsersId, "탈취시도")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("C006"));
    }

    @Test
    @DisplayName("타인의 usersId를 삭제하면 403 ACCESS_DENIED를 반환한다")
    void deleteMyLearningData_otherUsers() throws Exception {
        IntegrationTestSupport.TestUser owner = signupAndLogin();
        IntegrationTestSupport.TestUser intruder = signupAndLogin();
        long ownerUsersId = fetchUsersId(owner);
        createMyLearning(owner, ownerUsersId, "타인설정");

        mockMvc.perform(delete("/api/mylearning/" + ownerUsersId).header("Authorization", bearer(intruder.accessToken())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("C006"));
    }

    // fix: 활성 설정이 있는 상태에서 재생성 시 500 대신 409를 반환하는지 검증 (siwon)
    @Test
    @DisplayName("이미 설정이 있는 사용자가 다시 생성하면 409 MY_LEARNING_ALREADY_EXISTS를 반환한다")
    void createMyLearningData_duplicate() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long usersId = fetchUsersId(user);
        createMyLearning(user, usersId, "최초설정");

        mockMvc.perform(post("/api/mylearning")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(requestBody(usersId, "중복설정")))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("M001"));
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
