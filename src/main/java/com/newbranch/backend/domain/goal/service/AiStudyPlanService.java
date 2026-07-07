package com.newbranch.backend.domain.goal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbranch.backend.domain.goal.dto.AiStudyPlanResponse;
import com.newbranch.backend.domain.goal.entity.DailyStudyTime;
import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.goal.entity.MemorizationPlan;
import com.newbranch.backend.domain.goal.entity.RetrievalPlan;
import com.newbranch.backend.domain.goal.entity.StudyPlan;
import com.newbranch.backend.domain.goal.repository.DailyStudyTimeRepository;
import com.newbranch.backend.domain.goal.repository.MemorizationPlanRepository;
import com.newbranch.backend.domain.goal.repository.RetrievalPlanRepository;
import com.newbranch.backend.domain.goal.repository.StudyPlanRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AiStudyPlanService {

    private static final String SYSTEM_PROMPT = """
            당신은 전문 학습 컨설턴트입니다. 사용자의 학습 정보를 분석하여 일별 구체적인 학습 계획을 생성합니다.
            반드시 아래 JSON 형식으로만 응답하세요:
            {
              "dailyPlans": [
                {
                  "date": "YYYY-MM-DD",
                  "tasks": [
                    {
                      "startTime": "HH:MM",
                      "durationMinutes": 60,
                      "category": "암기|인출|기타학습|복습",
                      "activity": "구체적인 활동 내용",
                      "method": "사용할 학습 방법"
                    }
                  ]
                }
              ]
            }
            규칙:
            - 각 날짜의 tasks 총 durationMinutes는 해당 날의 availableMinutes를 초과하지 않아야 합니다.
            - [일별 가능 공부 시간]에 나열된 날짜에 대해서만 계획을 생성하세요.
            - 암기 기간에는 사용자가 선택한 암기 방법을 활용하세요.
            - 인출 기간에는 사용자가 선택한 인출 방법을 활용하세요.
            - 기타 학습 기간에는 해당 방법을 활용하세요.
            """;

    private final LearningGoalService learningGoalService;
    private final MemorizationPlanRepository memorizationPlanRepository;
    private final RetrievalPlanRepository retrievalPlanRepository;
    private final StudyPlanRepository studyPlanRepository;
    private final DailyStudyTimeRepository dailyStudyTimeRepository;
    private final UserQueryService userQueryService;
    private final UserSettingRepository userSettingRepository;
    private final UserLearningStyleRepository userLearningStyleRepository;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    /**
     * AI 학습 계획을 생성하고 저장합니다.
     *
     * @param email 사용자 이메일
     * @param lgId  학습 목표 ID
     * @return 생성된 AI 학습 계획
     */
    @Transactional
    public AiStudyPlanResponse generateStudyPlan(String email, Long lgId) {
        User user = findUserByEmail(email);
        LearningGoal goal = learningGoalService.findGoalByIdAndUser(lgId, user);

        String userPrompt = buildUserPrompt(goal, user);
        String jsonResult = openAiClient.chat(SYSTEM_PROMPT, userPrompt);

        goal.updateAiStudyPlan(jsonResult);
        return parseAiResponse(jsonResult, lgId);
    }

    /**
     * 저장된 AI 학습 계획을 조회합니다.
     *
     * @param email 사용자 이메일
     * @param lgId  학습 목표 ID
     * @return 저장된 AI 학습 계획
     */
    public AiStudyPlanResponse getStudyPlan(String email, Long lgId) {
        User user = findUserByEmail(email);
        LearningGoal goal = learningGoalService.findGoalByIdAndUser(lgId, user);

        if (goal.getAiStudyPlan() == null) {
            throw new BusinessException(ErrorCode.AI_PLAN_NOT_FOUND);
        }
        return parseAiResponse(goal.getAiStudyPlan(), lgId);
    }

    private String buildUserPrompt(LearningGoal goal, User user) {
        List<MemorizationPlan> mpList = memorizationPlanRepository.findAllByLearningGoalAndIsActiveTrue(goal);
        List<RetrievalPlan> rpList = retrievalPlanRepository.findAllByLearningGoalAndIsActiveTrue(goal);
        List<StudyPlan> spList = studyPlanRepository.findAllByLearningGoalAndIsActiveTrue(goal);
        List<DailyStudyTime> dstList = dailyStudyTimeRepository.findAllByLearningGoalAndIsActiveTrueOrderByStudyDateAsc(goal);
        UserSetting setting = userSettingRepository.findByUsersIdAndIsActiveTrue(user.getId()).orElse(null);
        UserLearningStyle style = userLearningStyleRepository.findByUsersIdAndIsActiveTrue(user.getId()).orElse(null);

        StringBuilder sb = new StringBuilder();
        sb.append(buildGoalSection(goal, user));
        sb.append(buildUserSettingSection(setting, style));
        sb.append(buildMemorizationSection(mpList));
        sb.append(buildRetrievalSection(rpList));
        sb.append(buildStudySection(spList));
        sb.append(buildDailyTimeSection(dstList));
        return sb.toString();
    }

    private String buildGoalSection(LearningGoal goal, User user) {
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
                goal.getStudyMethod(),
                user.getNickname(), user.getAgeGroup());
    }

    private String buildUserSettingSection(UserSetting setting, UserLearningStyle style) {
        if (setting == null && style == null) return "";
        StringBuilder sb = new StringBuilder("[나만의 학습 설정]\n");
        if (setting != null) {
            if (setting.getPreferredStudyTime() != null)
                sb.append("선호 학습 시간대: ").append(setting.getPreferredStudyTime()).append("\n");
            if (setting.getOffDays() != null)
                sb.append("쉬는 날: ").append(setting.getOffDays()).append(" (해당 날짜는 계획에서 제외)\n");
            if (setting.getPreferredStudyMethod() != null)
                sb.append("선호 학습 방법: ").append(setting.getPreferredStudyMethod()).append("\n");
            if (setting.getPreferredMaterialFormat() != null)
                sb.append("선호 자료 형식: ").append(setting.getPreferredMaterialFormat()).append("\n");
            if (setting.getPreferredFriendStyle() != null)
                sb.append("선호 학습 파트너 스타일: ").append(setting.getPreferredFriendStyle()).append("\n");
            if (setting.getExtraNotes() != null)
                sb.append("기타 특성: ").append(setting.getExtraNotes()).append("\n");
        }
        if (style != null && style.getLearningStyle() != null)
            sb.append("학습 유형: ").append(style.getLearningStyle()).append("\n");
        return sb.append("\n").toString();
    }

    private String buildMemorizationSection(List<MemorizationPlan> mpList) {
        if (mpList.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("[암기 계획]\n");
        for (MemorizationPlan mp : mpList) {
            sb.append(String.format("기간: %s ~ %s, 회독수: %d, 방법: %s\n",
                    mp.getStartDate(), mp.getEndDate(), mp.getRepetitionCount(), mp.getMethod()));
        }
        return sb.append("\n").toString();
    }

    private String buildRetrievalSection(List<RetrievalPlan> rpList) {
        if (rpList.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("[인출 계획]\n");
        for (RetrievalPlan rp : rpList) {
            sb.append(String.format("기간: %s ~ %s, 회독수: %d, 방법: %s\n",
                    rp.getStartDate(), rp.getEndDate(), rp.getRepetitionCount(), rp.getMethod()));
        }
        return sb.append("\n").toString();
    }

    private String buildStudySection(List<StudyPlan> spList) {
        if (spList.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("[기타 학습 계획]\n");
        for (StudyPlan sp : spList) {
            sb.append(String.format("기간: %s ~ %s, 회독수: %d, 방법: %s\n",
                    sp.getStartDate(), sp.getEndDate(), sp.getRepetitionCount(), sp.getMethod()));
        }
        return sb.append("\n").toString();
    }

    private String buildDailyTimeSection(List<DailyStudyTime> dstList) {
        if (dstList.isEmpty()) return "";
        StringBuilder sb = new StringBuilder("[일별 가능 공부 시간]\n");
        for (DailyStudyTime dst : dstList) {
            sb.append(String.format("%s: %d분\n", dst.getStudyDate(), dst.getAvailableMinutes()));
        }
        return sb.toString();
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