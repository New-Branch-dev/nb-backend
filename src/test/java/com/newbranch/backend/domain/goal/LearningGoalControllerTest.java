package com.newbranch.backend.domain.goal;

import com.fasterxml.jackson.databind.JsonNode;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import com.newbranch.backend.support.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * QA 테스트케이스 표 20~41번 (Learning Goal 도메인) 검증
 */
class LearningGoalControllerTest extends IntegrationTestSupport {

    private String goalBody(String title) {
        return """
                {"attachmentIds":[],"title":"%s","purpose":"목적","targetScore":90,"maxScore":100,
                 "startDate":"2026-08-01","endDate":"2026-08-31","methods":["요약"]}
                """.formatted(title);
    }

    private long createGoal(IntegrationTestSupport.TestUser user, String title) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/goals")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(goalBody(title)))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.path("data").path("lgId").asLong();
    }

    // 20. 학습 목표 생성 정상
    @Test
    @DisplayName("로그인 상태에서 유효한 데이터로 학습 목표를 생성하면 201을 반환한다")
    void createGoal_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(post("/api/v1/goals")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(goalBody("토익 900")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.title").value("토익 900"));
    }

    // 21. 필수값 누락 (title 없음)
    @Test
    @DisplayName("title 없이 학습 목표를 생성하면 400 validation 오류를 반환한다")
    void createGoal_missingRequired() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        String body = """
                {"attachmentIds":[]}
                """;

        mockMvc.perform(post("/api/v1/goals")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C002"));
    }

    // 22. 목표 목록 조회 정상 (1건 이상)
    @Test
    @DisplayName("목표가 1건 이상 있으면 목록 조회 시 200과 목록을 반환한다")
    void getGoals_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        createGoal(user, "목표1");

        mockMvc.perform(get("/api/v1/goals").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    // 23. 목표 없는 경우
    @Test
    @DisplayName("목표가 없는 신규 계정은 목록 조회 시 200과 빈 배열을 반환한다")
    void getGoals_empty() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(get("/api/v1/goals").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // 24. 목표 상세 조회 정상
    @Test
    @DisplayName("본인 목표를 상세 조회하면 200과 상세 정보를 반환한다")
    void getGoal_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user, "상세조회목표");

        mockMvc.perform(get("/api/v1/goals/" + lgId).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("상세조회목표"));
    }

    // 25. 존재하지 않는 목표 조회
    @Test
    @DisplayName("존재하지 않는 lgId를 조회하면 404 GOAL_NOT_FOUND를 반환한다")
    void getGoal_notFound() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(get("/api/v1/goals/999999999").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("G001"));
    }

    // 26. 타인 목표 접근
    @Test
    @DisplayName("타인의 목표를 조회하면 403 ACCESS_DENIED를 반환한다")
    void getGoal_otherUsersGoal() throws Exception {
        IntegrationTestSupport.TestUser owner = signupAndLogin();
        IntegrationTestSupport.TestUser intruder = signupAndLogin();
        long lgId = createGoal(owner, "타인목표");

        mockMvc.perform(get("/api/v1/goals/" + lgId).header("Authorization", bearer(intruder.accessToken())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("C006"));
    }

    // 27. 목표 수정 정상
    @Test
    @DisplayName("본인 목표를 유효한 데이터로 수정하면 200과 변경사항이 반영된다")
    void updateGoal_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user, "수정전");

        mockMvc.perform(put("/api/v1/goals/" + lgId)
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(goalBody("수정후")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("수정후"));
    }

    // 28. 존재하지 않는 목표 수정
    @Test
    @DisplayName("존재하지 않는 lgId를 수정하면 404 GOAL_NOT_FOUND를 반환한다")
    void updateGoal_notFound() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(put("/api/v1/goals/999999999")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(goalBody("수정")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("G001"));
    }

    // 29. 목표 삭제 정상
    @Test
    @DisplayName("본인 목표를 삭제하면 200을 반환하고 이후 조회 시 404가 된다")
    void deleteGoal_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user, "삭제될목표");

        mockMvc.perform(delete("/api/v1/goals/" + lgId).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/goals/" + lgId).header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("G001"));
    }

    // 30. 존재하지 않는 목표 삭제
    @Test
    @DisplayName("존재하지 않는 lgId를 삭제하면 404 GOAL_NOT_FOUND를 반환한다")
    void deleteGoal_notFound() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();

        mockMvc.perform(delete("/api/v1/goals/999999999").header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("G001"));
    }

    // 31. 목표 검색 정상
    @Test
    @DisplayName("검색어와 일치하는 목표가 있으면 200과 검색 결과를 반환한다")
    void searchGoals_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        createGoal(user, "토익 스피킹 마스터");

        mockMvc.perform(get("/api/v1/goals/search")
                        .header("Authorization", bearer(user.accessToken()))
                        .param("keyword", "스피킹"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    // 32. 검색 결과 없음
    @Test
    @DisplayName("매칭되지 않는 키워드로 검색하면 200과 빈 배열을 반환한다")
    void searchGoals_noMatch() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        createGoal(user, "토익 900");

        mockMvc.perform(get("/api/v1/goals/search")
                        .header("Authorization", bearer(user.accessToken()))
                        .param("keyword", "존재하지않는키워드"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    // 33. 제외 날짜 설정
    @Test
    @DisplayName("본인 목표에 유효한 제외 날짜 배열을 설정하면 200과 변경사항이 반영된다")
    void updateExcludeDates_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user, "제외날짜목표");

        mockMvc.perform(put("/api/v1/goals/" + lgId + "/exclude-dates")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content("[\"2026-08-15\"]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0]").value("2026-08-15"));
    }

    // 34. 잘못된 날짜 형식
    @Test
    @DisplayName("형식이 잘못된 날짜값을 입력하면 400 validation 오류를 반환한다")
    void updateExcludeDates_invalidFormat() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user, "제외날짜목표2");

        mockMvc.perform(put("/api/v1/goals/" + lgId + "/exclude-dates")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content("[\"not-a-date\"]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C002"));
    }

    // 35. 요일별 학습시간 설정
    @Test
    @DisplayName("본인 목표에 유효한 요일별 학습시간을 설정하면 200과 변경사항이 반영된다")
    void updateByDayStudyTimes_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user, "요일별시간목표");

        mockMvc.perform(put("/api/v1/goals/" + lgId + "/byday-study-times")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content("[{\"dayOfWeek\":1,\"availableMinutes\":120}]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].availableMinutes").value(120));
    }

    // 36. 범위 초과 값 입력 (음수/24시간 초과) - LearningGoalRequest.ByDayStudyTimeEntry에 @Min/@Max 추가로 검증됨
    @Test
    @DisplayName("음수이거나 24시간을 초과하는 학습시간을 입력하면 400 validation 오류를 반환한다")
    void updateByDayStudyTimes_outOfRange() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user, "요일별시간목표2");

        mockMvc.perform(put("/api/v1/goals/" + lgId + "/byday-study-times")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content("[{\"dayOfWeek\":1,\"availableMinutes\":-10}]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C002"));

        mockMvc.perform(put("/api/v1/goals/" + lgId + "/byday-study-times")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content("[{\"dayOfWeek\":1,\"availableMinutes\":1500}]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("C002"));
    }

    private static final String SAMPLE_PLAN_JSON = """
            {"dailyPlans":[{"date":"2026-08-01","tasks":[{"startTime":"09:00","durationMinutes":60,"activity":"1~3장 핵심 개념 읽기 후 빈칸 채우기 연습","method":"요약 노트 작성"}]}]}
            """;

    // 37. AI 학습 계획 생성
    @Test
    @DisplayName("목표가 존재하면 AI 학습 계획 생성 요청 시 200과 생성된 계획을 반환한다")
    void generateStudyPlan_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user, "AI계획목표");
        Mockito.when(openAiClient.chat(Mockito.anyString(), Mockito.anyString())).thenReturn(SAMPLE_PLAN_JSON);

        mockMvc.perform(post("/api/v1/goals/" + lgId + "/ai/study-plan")
                        .header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dailyPlans[0].date").value("2026-08-01"));
    }

    // 38. AI 외부 API 오류/timeout
    @Test
    @DisplayName("AI 서버 응답이 실패하면 500 AI_SERVICE_ERROR를 반환한다")
    void generateStudyPlan_aiServiceError() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user, "AI실패목표");
        Mockito.when(openAiClient.chat(Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new BusinessException(ErrorCode.AI_SERVICE_ERROR));

        mockMvc.perform(post("/api/v1/goals/" + lgId + "/ai/study-plan")
                        .header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("AI001"));
    }

    // 39. AI 학습 계획 조회
    @Test
    @DisplayName("생성 후 저장된 AI 학습 계획을 조회하면 200과 계획 데이터를 반환한다")
    void getStudyPlan_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user, "AI조회목표");

        mockMvc.perform(post("/api/v1/goals/" + lgId + "/ai/study-plan/save")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(SAMPLE_PLAN_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/goals/" + lgId + "/ai/study-plan")
                        .header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dailyPlans[0].date").value("2026-08-01"));
    }

    // 40. 계획 미생성 상태 조회
    @Test
    @DisplayName("AI 학습 계획이 없는 상태에서 조회하면 404 AI_PLAN_NOT_FOUND를 반환한다")
    void getStudyPlan_notGenerated() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user, "AI미생성목표");

        mockMvc.perform(get("/api/v1/goals/" + lgId + "/ai/study-plan")
                        .header("Authorization", bearer(user.accessToken())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("AI002"));
    }

    // 41. AI 학습 계획 저장
    @Test
    @DisplayName("생성된 AI 학습 계획을 저장하면 200과 저장 완료 결과를 반환한다")
    void saveStudyPlan_success() throws Exception {
        IntegrationTestSupport.TestUser user = signupAndLogin();
        long lgId = createGoal(user, "AI저장목표");

        mockMvc.perform(post("/api/v1/goals/" + lgId + "/ai/study-plan/save")
                        .header("Authorization", bearer(user.accessToken()))
                        .contentType("application/json")
                        .content(SAMPLE_PLAN_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.lgId").value(lgId));
    }
}
