package com.newbranch.backend.domain.goal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.newbranch.backend.domain.goal.dto.FormativeAssessmentResponse;
import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.note.entity.Note;
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
public class AiFormativeAssessmentService {

    private static final String SYSTEM_PROMPT = """
            당신은 출제 전문가입니다. 제공된 기출 자료를 분석하여 형성평가 문제를 생성합니다.
            반드시 아래 JSON 형식으로만 응답하세요:
            {
              "totalQuestions": 10,
              "questions": [
                {
                  "number": 1,
                  "difficulty": "상|중|하",
                  "type": "객관식|주관식|서술형",
                  "question": "문제 내용",
                  "options": ["①...", "②...", "③...", "④...", "⑤..."],
                  "answer": "정답",
                  "explanation": "해설"
                }
              ]
            }
            규칙:
            - 전체 문제 수는 자료에서 파악한 한 회차 시험 문제 수의 1/5로 설정하세요.
            - 난이도 비율은 상:중:하 = 3:4:3으로 균등하게 출제하세요.
            - 기출에서 자주 나온 유형의 문제를 우선 출제하세요.
            - 객관식이 아닌 경우 options 필드는 빈 배열로 설정하세요.
            - 문제는 원본 기출과 동일하지 않게 변형하여 출제하세요.
            """;

    private final LearningGoalService learningGoalService;
    private final NoteRepository noteRepository;
    private final NoteMaterialRepository noteMaterialRepository;
    private final UserQueryService userQueryService;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    /**
     * AI 형성평가를 생성하고 저장합니다.
     *
     * @param email 사용자 이메일
     * @param lgId  학습 목표 ID
     * @return 생성된 형성평가
     */
    @Transactional
    public FormativeAssessmentResponse generate(String email, Long lgId) {
        User user = findUserByEmail(email);
        LearningGoal goal = learningGoalService.findGoalByIdAndUser(lgId, user);

        String userPrompt = buildUserPrompt(goal);
        String jsonResult = openAiClient.chat(SYSTEM_PROMPT, userPrompt);

        goal.updateFormativeAssessment(jsonResult);
        return parseResponse(jsonResult, lgId);
    }

    /**
     * 저장된 형성평가를 조회합니다.
     *
     * @param email 사용자 이메일
     * @param lgId  학습 목표 ID
     * @return 저장된 형성평가
     */
    public FormativeAssessmentResponse getAssessment(String email, Long lgId) {
        User user = findUserByEmail(email);
        LearningGoal goal = learningGoalService.findGoalByIdAndUser(lgId, user);

        if (goal.getFormativeAssessment() == null) {
            throw new BusinessException(ErrorCode.AI_PLAN_NOT_FOUND);
        }
        return parseResponse(goal.getFormativeAssessment(), lgId);
    }

    private String buildUserPrompt(LearningGoal goal) {
        String noteContents = collectNoteContents(goal);
        return String.format("""
                [학습 목표]
                제목: %s
                목적: %s

                [기출 자료]
                %s
                """,
                goal.getTitle(), goal.getPurpose(), noteContents);
    }

    private static final int MAX_CHARS_PER_MATERIAL = 8_000;
    private static final int MAX_CHARS_TOTAL = 24_000;

    private String collectNoteContents(LearningGoal goal) {
        List<Note> notes = noteRepository.findAllByGoalAndIsActiveTrue(goal);
        if (notes.isEmpty()) return "제공된 기출 자료 없음";

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

    private FormativeAssessmentResponse parseResponse(String json, Long lgId) {
        try {
            FormativeAssessmentResponse response = objectMapper.readValue(json, FormativeAssessmentResponse.class);
            response.setLgId(lgId);
            return response;
        } catch (Exception e) {
            log.error("형성평가 응답 파싱 실패: {}", e.getMessage());
            throw new BusinessException(ErrorCode.AI_SERVICE_ERROR);
        }
    }

    private User findUserByEmail(String email) {
        return userQueryService.findByEmail(email);
    }
}