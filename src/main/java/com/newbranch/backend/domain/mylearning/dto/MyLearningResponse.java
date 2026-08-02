package com.newbranch.backend.domain.mylearning.dto;

import com.newbranch.backend.domain.mylearning.entity.UserLearningStyle;
import com.newbranch.backend.domain.mylearning.entity.UserSetting;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "나만의 학습 응답")
public class MyLearningResponse {

    @Schema(description = "사용자 ID")
    private Long usersId;

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "학교 및 소속")
    private String schoolName;

    // 회원 설정
    @Schema(description = "선호 학습 시간대")
    private String preferredStudyTime;

    @Schema(description = "쉬는 날")
    private String offDays;
    private String preferredMaterialFormat;
    private String preferredClassStyle;
    private String preferredStudyMethod;
    private String preferredTeacherStyle;
    private String preferredFriendStyle;
    private String extraNotes;

    @Schema(description = "흥미 코드")
    private String interestCd;

    @Schema(description = "적성 코드")
    private String aptitudeCd;

    @Schema(description = "성격 코드")
    private String personalityCd;

    @Schema(description = "학습성향")
    private String learningDisposition;

    @Schema(description = "선호 자료형식")
    private String preferredDataFormat;

    @Schema(description = "선호 수업방식")
    private String preferredTeachingStyle;

    @Schema(description = "선호 학습방법")
    private String preferredLearningMethod;

    @Schema(description = "선호 선생님 스타일")
    private String teacherStyle;

    @Schema(description = "선호 팀원 스타일")
    private String teammateStyle;

    // 학습 유형
    @Schema(description = "학습 유형")
    private String learningStyle;

    @Schema(description = "추천 학습법")
    private String recommendedMethod;

    public static MyLearningResponse of(UserSetting setting, UserLearningStyle style) {
        return MyLearningResponse.builder()
                .usersId(setting.getUsersId())
                .preferredStudyTime(setting.getPreferredStudyTime())
                .offDays(setting.getOffDays())
                .preferredMaterialFormat(setting.getPreferredMaterialFormat())
                .preferredClassStyle(setting.getPreferredClassStyle())
                .preferredStudyMethod(setting.getPreferredStudyMethod())
                .preferredTeacherStyle(setting.getPreferredTeacherStyle())
                .preferredFriendStyle(setting.getPreferredFriendStyle())
                .extraNotes(setting.getExtraNotes())
                .learningStyle(style.getLearningStyle())
                .recommendedMethod(style.getRecommendedMethod())
                .build();
    }
}
