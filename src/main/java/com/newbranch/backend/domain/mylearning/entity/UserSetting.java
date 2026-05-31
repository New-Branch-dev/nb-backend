package com.newbranch.backend.domain.mylearning.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSetting {

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

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}