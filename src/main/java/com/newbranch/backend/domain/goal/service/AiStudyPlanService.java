package com.newbranch.backend.domain.goal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbranch.backend.domain.goal.dto.AiStudyPlanResponse;
import com.newbranch.backend.domain.goal.entity.ByDayStudyTime;
import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.goal.entity.LearningGoalExclude;
import com.newbranch.backend.domain.goal.entity.LearningGoalMethod;
import com.newbranch.backend.domain.goal.repository.ByDayStudyTimeRepository;
import com.newbranch.backend.domain.goal.repository.LearningGoalExcludeRepository;
import com.newbranch.backend.domain.goal.repository.LearningGoalMethodRepository;
import com.newbranch.backend.domain.mylearning.entity.UserLearningStyle;
import com.newbranch.backend.domain.mylearning.entity.UserSetting;
import com.newbranch.backend.domain.mylearning.repository.UserLearningStyleRepository;
import com.newbranch.backend.domain.mylearning.repository.UserSettingRepository;
import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.domain.user.service.UserQueryService;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import com.newbranch.backend.infra.openai.OpenAiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiStudyPlanService {

    private static final String SYSTEM_PROMPT = """
            당신은 전문 학습 플래너입니다. 학생의 시험 준비를 위한 일별 상세 학습 계획을 생성합니다.
            반드시 아래 JSON 형식으로만 응답하세요:
            {
              "dailyPlans": [
                {
                  "date": "YYYY-MM-DD",
                  "tasks": [
                    {
                      "startTime": "HH:MM",
                      "durationMinutes": 60,
                      "activity": "구체적인 활동 내용 (예: 1~3장 핵심 개념 읽기 후 빈칸 채우기)",
                      "method": "학습 방법 (예: 마인드맵, 문제 풀기, 요약 노트 작성)"
                    }
                  ]
                }
              ]
            }
            규칙:
            1. [학습 기간] 내 날짜 중 [요일별 가능 공부 시간]에 해당하는 요일의 날짜에만 계획을 생성하세요.
            2. 각 날짜의 tasks 총 durationMinutes는 해당 요일의 availableMinutes를 초과하지 않아야 합니다.
            3. [나만의 학습 설정]의 선호 학습 시간대를 반영하여 startTime을 설정하세요.
            4. [제외 날짜]에 해당하는 날짜는 계획에서 완전히 제외하세요.
            5. [학습 방법]을 반영하여 activity와 method를 구체적으로 작성하세요.
            6. 사용자의 학습 유형을 참고하여 activity를 개인화하세요 (예: 시각형이면 도식화·마인드맵 위주).
            7. activity는 30자 이상의 구체적인 내용으로 작성하고, 단순히 "복습" 또는 "공부"라고만 쓰지 마세요.
            8. 하루 계획은 집중도를 고려해 60~90분 단위로 나누고, 긴 세션 사이에는 짧은 휴식 task를 포함하세요.
            """;

    private final LearningGoalService learningGoalService;
    private final ByDayStudyTimeRepository byDayStudyTimeRepository;
    private final LearningGoalExcludeRepository learningGoalExcludeRepository;
    private final LearningGoalMethodRepository learningGoalMethodRepository;
    private final UserQueryService userQueryService;
    private final UserSettingRepository userSettingRepository;
    private final UserLearningStyleRepository userLearningStyleRepository;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    @Transactional
    public AiStudyPlanResponse generateStudyPlan(String email, Long lgId) {
        User user = findUserByEmail(email);
        LearningGoal goal = learningGoalService.findGoalByIdAndUser(lgId, user);

        String userPrompt = buildUserPrompt(goal, user);
        String jsonResult = openAiClient.chat(SYSTEM_PROMPT, userPrompt);

        return parseAiResponse(jsonResult, lgId);
    }

    @Transactional
    public AiStudyPlanResponse saveStudyPlan(String email, Long lgId, AiStudyPlanResponse plan) {
        User user = findUserByEmail(email);
        LearningGoal goal = learningGoalService.findGoalByIdAndUser(lgId, user);

        try {
            String jsonResult = objectMapper.writeValueAsString(plan);
            goal.updateAiStudyPlan(jsonResult);
        } catch (Exception e) {
            log.error("AI 학습 계획 직렬화 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.AI_SERVICE_ERROR);
        }
        plan.setLgId(lgId);
        return plan;
    }

    public AiStudyPlanResponse getStudyPlan(String email, Long lgId) {
        User user = findUserByEmail(email);
        LearningGoal goal = learningGoalService.findGoalByIdAndUser(lgId, user);

        if (goal.getAiStudyPlan() == null) {
            throw new BusinessException(ErrorCode.AI_PLAN_NOT_FOUND);
        }
        return parseAiResponse(goal.getAiStudyPlan(), lgId);
    }

    private String buildUserPrompt(LearningGoal goal, User user) {
        List<ByDayStudyTime> bstList = byDayStudyTimeRepository
                .findAllByLearningGoalAndIsActiveTrueOrderByDayOfWeekAsc(goal);
        List<LearningGoalExclude> excludes = learningGoalExcludeRepository
                .findAllByLearningGoalAndIsActiveTrueOrderByExcludeDateAsc(goal);
        List<LearningGoalMethod> methods = learningGoalMethodRepository
                .findAllByLearningGoalAndIsActiveTrue(goal);
        UserSetting setting = userSettingRepository.findByUsersIdAndIsActiveTrue(user.getId()).orElse(null);
        UserLearningStyle style = userLearningStyleRepository.findByUsersIdAndIsActiveTrue(user.getId()).orElse(null);

        StringBuilder sb = new StringBuilder();
        sb.append(buildGoalSection(goal, user, methods));
        sb.append(buildUserSettingSection(setting, style));
        sb.append(buildByDaySection(bstList));
        sb.append(buildExcludeDatesSection(excludes));
        return sb.toString();
    }

    private String buildGoalSection(LearningGoal goal, User user, List<LearningGoalMethod> methods) {
        String methodStr = methods.stream()
                .map(LearningGoalMethod::getMethodName)
                .collect(Collectors.joining(", "));
        return String.format("""
                [학습 목표]
                제목: %s
                목적: %s
                목표 점수: %s / %s
                학습 기간: %s ~ %s
                학습 방법: %s

                [사용자 정보]
                닉네임: %s
                연령대: %s

                """,
                goal.getTitle(), goal.getPurpose(),
                goal.getTargetScore(), goal.getMaxScore(),
                goal.getStartDate(), goal.getEndDate(),
                methodStr,
                user.getNickname(), user.getAgeGroup());
    }

    private String buildUserSettingSection(UserSetting setting, UserLearningStyle style) {
        if (setting == null && style == null) return "";
        StringBuilder sb = new StringBuilder("[나만의 학습 설정]\n");
        if (setting != null) {
            if (setting.getPreferredStudyTime() != null)
                sb.append("선호 학습 시간대: ").append(setting.getPreferredStudyTime()).append("\n");
            if (setting.getPreferredStudyMethod() != null)
                sb.append("선호 학습 방법: ").append(setting.getPreferredStudyMethod()).append("\n");
            if (setting.getPreferredMaterialFormat() != null)
                sb.append("선호 자료 형식: ").append(setting.getPreferredMaterialFormat()).append("\n");
            if (setting.getExtraNotes() != null)
                sb.append("기타 특성: ").append(setting.getExtraNotes()).append("\n");
        }
        if (style != null && style.getLearningStyle() != null)
            sb.append("학습 유형: ").append(style.getLearningStyle()).append("\n");
        return sb.append("\n").toString();
    }

    private String buildByDaySection(List<ByDayStudyTime> bstList) {
        if (bstList.isEmpty()) return "";
        String[] dayNames = {"", "월", "화", "수", "목", "금", "토", "일"};
        StringBuilder sb = new StringBuilder("[요일별 가능 공부 시간]\n");
        for (ByDayStudyTime bst : bstList) {
            String dayName = (bst.getDayOfWeek() >= 1 && bst.getDayOfWeek() <= 7)
                    ? dayNames[bst.getDayOfWeek()] : bst.getDayOfWeek().toString();
            sb.append(String.format("%s요일: %d분\n", dayName, bst.getAvailableMinutes()));
        }
        return sb.append("\n").toString();
    }

    private String buildExcludeDatesSection(List<LearningGoalExclude> excludes) {
        if (excludes.isEmpty()) return "";
        String dates = excludes.stream()
                .map(e -> e.getExcludeDate().toString())
                .collect(Collectors.joining(", "));
        return "[제외 날짜 (절대 계획 생성 금지)]\n" + dates + "\n\n";
    }

    private AiStudyPlanResponse parseAiResponse(String json, Long lgId) {
        try {
            AiStudyPlanResponse response = objectMapper.readValue(json, AiStudyPlanResponse.class);
            response.setLgId(lgId);
            return response;
        } catch (Exception e) {
            log.error("AI 응답 파싱 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.AI_SERVICE_ERROR);
        }
    }

    private User findUserByEmail(String email) {
        return userQueryService.findByEmail(email);
    }
}
