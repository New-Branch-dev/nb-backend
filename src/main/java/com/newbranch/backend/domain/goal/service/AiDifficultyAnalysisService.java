package com.newbranch.backend.domain.goal.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbranch.backend.domain.goal.dto.DifficultyAnalysisResponse;
import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.mylearning.entity.UserLearningStyle;
import com.newbranch.backend.domain.mylearning.entity.UserSetting;
import com.newbranch.backend.domain.mylearning.repository.UserLearningStyleRepository;
import com.newbranch.backend.domain.mylearning.repository.UserSettingRepository;
import com.newbranch.backend.domain.note.entity.Note;
import com.newbranch.backend.domain.note.entity.NoteMaterial;
import com.newbranch.backend.domain.note.repository.NoteMaterialRepository;
import com.newbranch.backend.domain.note.repository.NoteRepository;
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
public class AiDifficultyAnalysisService {

    private static final String SYSTEM_PROMPT = """
            당신은 시험 분석 전문가입니다. 제공된 학습 자료와 목표를 바탕으로 시험의 특징과 기출 경향을 분석합니다.
            반드시 아래 JSON 형식으로만 응답하세요:
            {
              "difficultyAnalysis": "학습 난이도 및 시험 특징 분석 내용",
              "trendAnalysis": "기출 경향 및 출제 패턴 분석 내용",
              "recommendedMethod": "분석 결과를 바탕으로 추천하는 학습 방법"
            }
            분석 항목:
            - 시험 특징 (출제 범위, 문제 유형, 배점 분포)
            - 문제 출제 경향 (자주 나오는 유형, 핵심 개념)
            - 시험 추후 변화 예측
            - 학습 목적 달성을 위한 핵심 학습 포인트
            """;

    private final LearningGoalService learningGoalService;
    private final NoteRepository noteRepository;
    private final NoteMaterialRepository noteMaterialRepository;
    private final UserQueryService userQueryService;
    private final UserSettingRepository userSettingRepository;
    private final UserLearningStyleRepository userLearningStyleRepository;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    /**
     * AI 학습 난이도 분석을 수행하고 저장합니다.
     *
     * @param email 사용자 이메일
     * @param lgId  학습 목표 ID
     * @return 난이도 분석 결과
     */
    @Transactional
    public DifficultyAnalysisResponse analyze(String email, Long lgId) {
        User user = findUserByEmail(email);
        LearningGoal goal = learningGoalService.findGoalByIdAndUser(lgId, user);

        String userPrompt = buildUserPrompt(goal, user);
        String jsonResult = openAiClient.chat(SYSTEM_PROMPT, userPrompt);

        saveAnalysisResult(goal, jsonResult);
        return DifficultyAnalysisResponse.from(goal);
    }

    /**
     * 저장된 난이도 분석 결과를 조회합니다.
     *
     * @param email 사용자 이메일
     * @param lgId  학습 목표 ID
     * @return 난이도 분석 결과
     */
    public DifficultyAnalysisResponse getAnalysis(String email, Long lgId) {
        User user = findUserByEmail(email);
        LearningGoal goal = learningGoalService.findGoalByIdAndUser(lgId, user);

        if (goal.getDifficultyAnalysis() == null) {
            throw new BusinessException(ErrorCode.AI_PLAN_NOT_FOUND);
        }
        return DifficultyAnalysisResponse.from(goal);
    }

    private String buildUserPrompt(LearningGoal goal, User user) {
        String noteContents = collectNoteContents(goal);
        UserSetting setting = userSettingRepository.findByUsersIdAndIsActiveTrue(user.getId()).orElse(null);
        UserLearningStyle style = userLearningStyleRepository.findByUsersIdAndIsActiveTrue(user.getId()).orElse(null);
        String userContext = buildUserContext(setting, style);
        return String.format("""
                [학습 목표]
                제목: %s
                목적: %s
                목표 점수: %s / %s

                %s
                [학습 자료]
                %s
                """,
                goal.getTitle(), goal.getPurpose(),
                goal.getTargetScore(), goal.getMaxScore(),
                userContext, noteContents);
    }

    private String buildUserContext(UserSetting setting, UserLearningStyle style) {
        if (setting == null && style == null) return "";
        StringBuilder sb = new StringBuilder("[사용자 학습 특성]\n");
        if (setting != null) {
            if (setting.getPreferredStudyTime() != null)
                sb.append("선호 학습 시간대: ").append(setting.getPreferredStudyTime()).append("\n");
            if (setting.getPreferredStudyMethod() != null)
                sb.append("선호 학습 방법: ").append(setting.getPreferredStudyMethod()).append("\n");
            if (setting.getPreferredFriendStyle() != null)
                sb.append("선호 학습 파트너 스타일: ").append(setting.getPreferredFriendStyle()).append("\n");
            if (setting.getExtraNotes() != null)
                sb.append("흥미/적성: ").append(setting.getExtraNotes()).append("\n");
        }
        if (style != null && style.getLearningStyle() != null)
            sb.append("학습 유형: ").append(style.getLearningStyle()).append("\n");
        return sb.append("\n").toString();
    }

    private static final int MAX_CHARS_PER_MATERIAL = 8_000;
    private static final int MAX_CHARS_TOTAL = 24_000;

    private String collectNoteContents(LearningGoal goal) {
        List<Note> notes = noteRepository.findAllByGoalAndIsActiveTrue(goal);
        if (notes.isEmpty()) return "제공된 학습 자료 없음";

        StringBuilder sb = new StringBuilder();
        for (Note note : notes) {
            if (sb.length() >= MAX_CHARS_TOTAL) break;
            if (note.getContent() != null) {
                sb.append("[노트: ").append(note.getTitle()).append("]\n");
                sb.append(truncate(note.getContent())).append("\n\n");
            }
            appendTextMaterials(sb, note);
        }
        return sb.toString();
    }

    private void appendTextMaterials(StringBuilder sb, Note note) {
        noteMaterialRepository.findAllByNoteAndIsActiveTrue(note)
                .forEach(m -> {
                    if (sb.length() >= MAX_CHARS_TOTAL) return;
                    String text = resolveText(m);
                    if (text != null && !text.isBlank()) {
                        String title = m.getTitle() != null ? m.getTitle() : m.getFileName();
                        int remaining = MAX_CHARS_TOTAL - sb.length();
                        String truncated = truncate(text, Math.min(MAX_CHARS_PER_MATERIAL, remaining));
                        sb.append("[자료: ").append(title).append("]\n")
                          .append(truncated).append("\n\n");
                    }
                });
    }

    private String resolveText(com.newbranch.backend.domain.note.entity.NoteMaterial m) {
        return switch (m.getMaterialType()) {
            case TEXT -> m.getContent();
            case FILE, LECTURE -> m.getExtractedText();
            default -> null;
        };
    }

    private String truncate(String text) {
        return truncate(text, MAX_CHARS_PER_MATERIAL);
    }

    private String truncate(String text, int limit) {
        if (text == null || text.length() <= limit) return text;
        return text.substring(0, limit) + "\n...(이하 생략)";
    }

    private void saveAnalysisResult(LearningGoal goal, String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            goal.updateDifficultyAnalysis(
                    root.path("difficultyAnalysis").asText(),
                    root.path("trendAnalysis").asText(),
                    root.path("recommendedMethod").asText()
            );
        } catch (Exception e) {
            log.error("난이도 분석 응답 파싱 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.AI_SERVICE_ERROR);
        }
    }

    private User findUserByEmail(String email) {
        return userQueryService.findByEmail(email);
    }
}