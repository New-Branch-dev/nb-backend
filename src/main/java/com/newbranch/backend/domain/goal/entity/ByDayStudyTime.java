package com.newbranch.backend.domain.goal.entity;

import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "byday_study_times")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ByDayStudyTime extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bst_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lg_id", nullable = false)
    private LearningGoal learningGoal;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;  // 1=월 ~ 7=일

    @Column(name = "available_minutes")
    private Integer availableMinutes;
}
