package com.newbranch.backend.domain.consolidation.entity;

import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "consolidations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Consolidation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "co_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lg_id", nullable = false)
    private LearningGoal learningGoal;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String article;

    @Column(length = 100)
    private String mode;

    @Column(name = "duplicate_handling", length = 50)
    private String duplicateHandling;

    @Column(name = "importance_level", length = 50)
    private String importanceLevel;

    @Column(name = "summary_note", columnDefinition = "TEXT")
    private String summaryNote;

    public void update(String title, String article, String mode, String duplicateHandling,
                        String importanceLevel, String summaryNote) {
        if (title != null) this.title = title;
        if (article != null) this.article = article;
        if (mode != null) this.mode = mode;
        if (duplicateHandling != null) this.duplicateHandling = duplicateHandling;
        if (importanceLevel != null) this.importanceLevel = importanceLevel;
        if (summaryNote != null) this.summaryNote = summaryNote;
    }
}
