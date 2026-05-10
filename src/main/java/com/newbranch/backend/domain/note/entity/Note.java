package com.newbranch.backend.domain.note.entity;

import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Note extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notes_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "source_type", length = 50)
    private String sourceType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lg_id")
    private LearningGoal goal;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NoteStatus status = NoteStatus.DRAFT;

    @Builder
    public Note(User user) {
        this.user = user;
        this.status = NoteStatus.DRAFT;
    }

    public void linkToGoal(LearningGoal goal) {
        this.goal = goal;
    }

    public void publish(String title, String content, String sourceType) {
        this.title = title;
        this.content = content;
        this.sourceType = sourceType;
        this.status = NoteStatus.ACTIVE;
    }
}
