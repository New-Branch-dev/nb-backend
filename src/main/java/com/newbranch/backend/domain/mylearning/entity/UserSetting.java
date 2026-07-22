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

    @Column(name = "preferred_material_format", length = 100)
    private String preferredMaterialFormat;

    @Column(name = "preferred_class_style", length = 100)
    private String preferredClassStyle;

    @Column(name = "preferred_study_method", length = 100)
    private String preferredStudyMethod;

    @Column(name = "preferred_teacher_style", length = 50)
    private String preferredTeacherStyle;

    @Column(name = "preferred_friend_style", length = 50)
    private String preferredFriendStyle;

    @Column(name = "extra_notes", columnDefinition = "TEXT")
    private String extraNotes;

    @Builder
    public UserSetting(Long usersId, String preferredStudyTime, String offDays,
                       String preferredMaterialFormat, String preferredClassStyle,
                       String preferredStudyMethod, String preferredTeacherStyle,
                       String preferredFriendStyle, String extraNotes) {
        this.usersId = usersId;
        this.preferredStudyTime = preferredStudyTime;
        this.offDays = offDays;
        this.preferredMaterialFormat = preferredMaterialFormat;
        this.preferredClassStyle = preferredClassStyle;
        this.preferredStudyMethod = preferredStudyMethod;
        this.preferredTeacherStyle = preferredTeacherStyle;
        this.preferredFriendStyle = preferredFriendStyle;
        this.extraNotes = extraNotes;
    }

    public void update(String preferredStudyTime, String offDays,
                       String preferredMaterialFormat, String preferredClassStyle,
                       String preferredStudyMethod, String preferredTeacherStyle,
                       String preferredFriendStyle, String extraNotes) {
        this.preferredStudyTime = preferredStudyTime;
        this.offDays = offDays;
        this.preferredMaterialFormat = preferredMaterialFormat;
        this.preferredClassStyle = preferredClassStyle;
        this.preferredStudyMethod = preferredStudyMethod;
        this.preferredTeacherStyle = preferredTeacherStyle;
        this.preferredFriendStyle = preferredFriendStyle;
        this.extraNotes = extraNotes;
    }
}
