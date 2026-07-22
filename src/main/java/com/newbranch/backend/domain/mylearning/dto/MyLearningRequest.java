package com.newbranch.backend.domain.mylearning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "나만의 학습 등록/수정 요청")
public class MyLearningRequest {

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(max = 100, message = "닉네임은 100자를 초과할 수 없습니다")
    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;

    @NotBlank(message = "학교 및 소속은 필수입니다")
    @Size(max = 100, message = "학교 및 소속은 100자를 초과할 수 없습니다")
    @Schema(description = "학교 및 소속", example = "서울고등학교")
    private String schoolName;

    // 회원 설정 (user_settings)
    @Schema(description = "선호 학습 시간대", example = "MORNING")
    private String preferredStudyTime;

    @Schema(description = "쉬는 날", example = "SAT,SUN")
    private String offDays;

    @NotBlank(message = "흥미는 필수입니다")
    @Size(max = 100, message = "흥미는 100자를 초과할 수 없습니다")
    @Schema(description = "흥미 코드", example = "ART")
    private String interestCd;

    @NotBlank(message = "적성은 필수입니다")
    @Size(max = 100, message = "적성은 100자를 초과할 수 없습니다")
    @Schema(description = "적성 코드", example = "LOGICAL")
    private String aptitudeCd;

    @NotBlank(message = "성격은 필수입니다")
    @Size(max = 100, message = "성격은 100자를 초과할 수 없습니다")
    @Schema(description = "성격 코드", example = "INTROVERT")
    private String personalityCd;

    @NotBlank(message = "학습성향은 필수입니다")
    @Size(max = 255, message = "학습성향은 255자를 초과할 수 없습니다")
    @Schema(description = "학습성향", example = "VISUAL")
    private String learningDisposition;

    @NotBlank(message = "선호 자료형식은 필수입니다")
    @Size(max = 100, message = "선호 자료형식은 100자를 초과할 수 없습니다")
    @Schema(description = "선호 자료형식", example = "VIDEO")
    private String preferredDataFormat;

    @NotBlank(message = "선호 수업방식은 필수입니다")
    @Size(max = 100, message = "선호 수업방식은 100자를 초과할 수 없습니다")
    @Schema(description = "선호 수업방식", example = "LECTURE")
    private String preferredTeachingStyle;

    @NotBlank(message = "선호 학습방법은 필수입니다")
    @Size(max = 100, message = "선호 학습방법은 100자를 초과할 수 없습니다")
    @Schema(description = "선호 학습방법", example = "SELF_STUDY")
    private String preferredLearningMethod;

    @Schema(description = "선호 선생님 스타일", example = "STRICT")
    private String teacherStyle;

    @Schema(description = "선호 팀원 스타일", example = "COOPERATIVE")
    private String teammateStyle;

    // 학습 유형 (user_learning_styles)
    @Schema(description = "학습 유형", example = "AUDITORY")
    private String learningStyle;

    @Schema(description = "추천 학습법", example = "PODCAST")
    private String recommendedMethod;
}
