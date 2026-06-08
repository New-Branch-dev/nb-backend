package com.newbranch.backend.domain.goal.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FormativeAssessmentResponse {

    private Long lgId;
    private Integer totalQuestions;
    private List<Question> questions;

    public static FormativeAssessmentResponse of(Long lgId, Integer totalQuestions, List<Question> questions) {
        FormativeAssessmentResponse response = new FormativeAssessmentResponse();
        response.lgId = lgId;
        response.totalQuestions = totalQuestions;
        response.questions = questions;
        return response;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Question {
        private Integer number;
        private String difficulty;
        private String type;
        private String question;
        private List<String> options;
        private String answer;
        private String explanation;
    }
}