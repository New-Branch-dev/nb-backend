package com.newbranch.backend.domain.goal.entity;

import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "learning_goals")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class LearningGoal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lg_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String title;

    private String purpose;

    @Column(name = "target_score")
    private Integer targetScore;

    @Column(name = "max_score")
    private Integer maxScore;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "ai_study_plan", columnDefinition = "TEXT")
    private String aiStudyPlan;

    public void updateAiStudyPlan(String aiStudyPlan) {
        this.aiStudyPlan = aiStudyPlan;
    }

    public void update(String title, String purpose, Integer targetScore, Integer maxScore,
                       LocalDate startDate, LocalDate endDate) {
        if (title != null) this.title = title;
        if (purpose != null) this.purpose = purpose;
        if (targetScore != null) this.targetScore = targetScore;
        if (maxScore != null) this.maxScore = maxScore;
        if (startDate != null) this.startDate = startDate;
        if (endDate != null) this.endDate = endDate;
    }
}
