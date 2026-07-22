package com.newbranch.backend.domain.mylearning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "나만의 학습 등록/수정 요청")
public class MyLearningRequest {

    @Schema(description = "사용자 ID")
    private Long usersId;

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

    @Schema(description = "선호 자료 형식", example = "VIDEO")
    private String preferredMaterialFormat;

    @Schema(description = "선호 수업 방식", example = "LECTURE")
    private String preferredClassStyle;

    @Schema(description = "선호 학습 방법", example = "SELF_STUDY")
    private String preferredStudyMethod;

    @Schema(description = "선호 선생님 스타일", example = "STRICT")
    private String preferredTeacherStyle;

    @Schema(description = "선호 친구 스타일", example = "COOPERATIVE")
    private String preferredFriendStyle;

    @Schema(description = "기타 특성 (흥미/적성/자유입력)", example = "그림 그리기를 좋아함")
    private String extraNotes;

    // 학습 유형 (user_learning_styles)
    @Schema(description = "학습 유형", example = "AUDITORY")
    private String learningStyle;

    @Schema(description = "추천 학습법", example = "PODCAST")
    private String recommendedMethod;
}
