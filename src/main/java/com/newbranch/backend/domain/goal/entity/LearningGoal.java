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

    @Column(name = "study_method")
    private String studyMethod;

    @Column(name = "difficulty_analysis", columnDefinition = "TEXT")
    private String difficultyAnalysis;

    @Column(name = "trend_analysis", columnDefinition = "TEXT")
    private String trendAnalysis;

    @Column(name = "recommended_method", columnDefinition = "TEXT")
    private String recommendedMethod;

    public void update(String title, String purpose, Integer targetScore, Integer maxScore,
                       LocalDate startDate, LocalDate endDate, String studyMethod) {
        this.title = title;
        this.purpose = purpose;
        this.targetScore = targetScore;
        this.maxScore = maxScore;
        this.startDate = startDate;
        this.endDate = endDate;
        this.studyMethod = studyMethod;
    }
}
