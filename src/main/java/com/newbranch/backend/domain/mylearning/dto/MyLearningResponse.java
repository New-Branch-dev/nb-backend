package com.newbranch.backend.domain.mylearning.dto;

import com.newbranch.backend.domain.mylearning.entity.UserLearningStyle;
import com.newbranch.backend.domain.mylearning.entity.UserSetting;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyLearningResponse {

    private Long usersId;

    // 회원설정
    private String preferredStudyTime;
    private String offDays;
    private String preferredMaterialFormat;
    private String preferredClassStyle;
    private String preferredStudyMethod;
    private String preferredTeacherStyle;
    private String preferredFriendStyle;
    private String extraNotes;

    // 학습 유형
    private String learningStyle;
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
