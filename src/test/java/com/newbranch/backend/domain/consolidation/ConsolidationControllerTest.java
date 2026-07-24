package com.newbranch.backend.domain.consolidation;

import com.fasterxml.jackson.databind.JsonNode;
import com.newbranch.backend.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * QA 테스트케이스 표 42~50번 (Consolidation 도메인) 검증
 */
class ConsolidationControllerTest extends IntegrationTestSupport {

    private long createGoal(IntegrationTestSupport.TestUser user) throws Exception {
        String body = """
                {"attachmentIds":[],"title":"단권화용 목표"}
                """;
        MvcResult result = mockMvc.perform(post("/api/v1/goals")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).path("data").path("lgId").asLong();
    }

    private String consolidationBody(long lgId, String title) {
        return """
                {"lgId":%d,"title":"%s","article":"본문 내용","mode":"AI_AUTO"}
                """.formatted(lgId, title);
    }

    private long createConsolidation(IntegrationTestSupport.TestUser user, long lgId, String title) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/consolidations")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(consolidationBody(lgId, title)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.path("data").path("coId").asLong();
    }

    // 42. 단권화 등록 정상
    @Test
    @DisplayName("로그인 상태에서 유효한 데이터로 단권화를 등록하면 201을 반환한다")
    void createConsolidation_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user);

        mockMvc.perform(post("/api/v1/consolidations")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(consolidationBody(lgId, "1차 중간고사 단권화")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("1차 중간고사 단권화"));
    }

    // 43. 필수값 누락
    @Test
    @DisplayName("title 없이 단권화를 등록하면 400 validation 오류를 반환한다")
    void createConsolidation_missingRequired() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user);
        String body = """
                {"lgId":%d}
                """.formatted(lgId);

        mockMvc.perform(post("/api/v1/consolidations")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C002"));
    }

    // materials 내부 항목의 @NotBlank 검증(materials 필드에 @Valid 추가로 수정됨) 회귀 테스트
    @Test
    @DisplayName("하위 자료 항목의 내용이 비어있으면 400 validation 오류를 반환한다")
    void createConsolidation_blankMaterialField() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user);
        String body = """
                {"lgId":%d,"title":"제목","materials":[{"field":"   ","sortOrder":0}]}
                """.formatted(lgId);

        mockMvc.perform(post("/api/v1/consolidations")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C002"));
    }

    // 44. 단권화 목록 조회 정상
    @Test
    @DisplayName("데이터가 1건 이상 있으면 목록 조회 시 200과 목록을 반환한다")
    void getConsolidations_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user);
        createConsolidation(user, lgId, "목록조회단권화");

        mockMvc.perform(get("/api/v1/consolidations").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    // 45. 단권화 상세 조회
    @Test
    @DisplayName("본인 데이터를 상세 조회하면 200과 상세 정보를 반환한다")
    void getConsolidation_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user);
        long coId = createConsolidation(user, lgId, "상세조회단권화");

        mockMvc.perform(get("/api/v1/consolidations/" + coId).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("상세조회단권화"));
    }

    // 46. 존재하지 않는 데이터 조회
    @Test
    @DisplayName("존재하지 않는 coId를 조회하면 404 CONSOLIDATION_NOT_FOUND를 반환한다")
    void getConsolidation_notFound() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(get("/api/v1/consolidations/999999999").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CO001"));
    }

    // 47. 단권화 수정
    @Test
    @DisplayName("본인 데이터를 유효한 데이터로 수정하면 200과 변경사항이 반영된다")
    void updateConsolidation_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user);
        long coId = createConsolidation(user, lgId, "수정전단권화");

        mockMvc.perform(put("/api/v1/consolidations/" + coId)
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(consolidationBody(lgId, "수정후단권화")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("수정후단권화"));
    }

    // 48. 존재하지 않는 데이터 수정
    @Test
    @DisplayName("존재하지 않는 coId를 수정하면 404 CONSOLIDATION_NOT_FOUND를 반환한다")
    void updateConsolidation_notFound() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user);

        mockMvc.perform(put("/api/v1/consolidations/999999999")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(consolidationBody(lgId, "수정")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CO001"));
    }

    // 49. 단권화 삭제 정상
    @Test
    @DisplayName("본인 데이터를 삭제하면 200을 반환하고 이후 조회 시 404가 된다")
    void deleteConsolidation_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user);
        long coId = createConsolidation(user, lgId, "삭제될단권화");

        mockMvc.perform(delete("/api/v1/consolidations/" + coId).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/consolidations/" + coId).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CO001"));
    }

    // 50. 존재하지 않는 데이터 삭제
    @Test
    @DisplayName("존재하지 않는 coId를 삭제하면 404 CONSOLIDATION_NOT_FOUND를 반환한다")
    void deleteConsolidation_notFound() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(delete("/api/v1/consolidations/999999999").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CO001"));
    }
}
