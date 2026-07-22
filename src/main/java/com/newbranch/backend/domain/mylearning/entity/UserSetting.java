package com.newbranch.backend.domain.mylearning.entity;

import com.newbranch.backend.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_settings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "us_id")
    private Long usId;

    @Column(name = "users_id", nullable = false)
    private Long usersId;

    @Column(name = "preferred_study_time", length = 50)
    private String preferredStudyTime;

    @Column(name = "off_days", length = 100)
    private String offDays;

    @Column(name = "interest_cd", length = 100)
    private String interestCd;

    @Column(name = "aptitude_cd", length = 100)
    private String aptitudeCd;

    @Column(name = "personality_cd", length = 100)
    private String personalityCd;

    @Column(name = "learning_disposition", length = 255)
    private String learningDisposition;

    @Column(name = "preferred_data_format", length = 100)
    private String preferredDataFormat;

    @Column(name = "preferred_teaching_style", length = 100)
    private String preferredTeachingStyle;

    @Column(name = "preferred_learning_method", length = 100)
    private String preferredLearningMethod;

    @Column(name = "teacher_style", length = 100)
    private String teacherStyle;

    @Column(name = "teammate_style", length = 100)
    private String teammateStyle;

    @Builder
    public UserSetting(Long usersId, String preferredStudyTime, String offDays,
                       String interestCd, String aptitudeCd, String personalityCd,
                       String learningDisposition, String preferredDataFormat,
                       String preferredTeachingStyle, String preferredLearningMethod,
                       String teacherStyle, String teammateStyle) {
        this.usersId = usersId;
        this.preferredStudyTime = preferredStudyTime;
        this.offDays = offDays;
        this.interestCd = interestCd;
        this.aptitudeCd = aptitudeCd;
        this.personalityCd = personalityCd;
        this.learningDisposition = learningDisposition;
        this.preferredDataFormat = preferredDataFormat;
        this.preferredTeachingStyle = preferredTeachingStyle;
        this.preferredLearningMethod = preferredLearningMethod;
        this.teacherStyle = teacherStyle;
        this.teammateStyle = teammateStyle;
    }

    public void update(String preferredStudyTime, String offDays,
                       String interestCd, String aptitudeCd, String personalityCd,
                       String learningDisposition, String preferredDataFormat,
                       String preferredTeachingStyle, String preferredLearningMethod,
                       String teacherStyle, String teammateStyle) {
        this.preferredStudyTime = preferredStudyTime;
        this.offDays = offDays;
        this.interestCd = interestCd;
        this.aptitudeCd = aptitudeCd;
        this.personalityCd = personalityCd;
        this.learningDisposition = learningDisposition;
        this.preferredDataFormat = preferredDataFormat;
        this.preferredTeachingStyle = preferredTeachingStyle;
        this.preferredLearningMethod = preferredLearningMethod;
        this.teacherStyle = teacherStyle;
        this.teammateStyle = teammateStyle;
    }
}
