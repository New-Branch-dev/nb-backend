package com.newbranch.backend.domain.note.entity;

import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "note_materials")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NoteMaterial extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nm_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "users_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notes_id", nullable = false)
    private Note note;

    @Enumerated(EnumType.STRING)
    @Column(name = "material_type", nullable = false, length = 20)
    private MaterialType materialType;

    // FILE, LECTURE 타입
    @Column(name = "file_name", length = 255)
    private String fileName;

    @Column(name = "file_url", length = 500)
    private String fileUrl;

    // TEXT 타입
    @Column(name = "title", length = 255)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Builder
    public NoteMaterial(User user, Note note, MaterialType materialType,
                        String fileName, String fileUrl, String title, String content) {
        this.user = user;
        this.note = note;
        this.materialType = materialType;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.title = title;
        this.content = content;
    }
}