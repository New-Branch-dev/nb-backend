package com.newbranch.backend.domain.mylearning.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyLearningRequest {
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
}