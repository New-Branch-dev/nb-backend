package com.newbranch.backend.domain.note.dto;

import com.newbranch.backend.domain.note.entity.Note;
import com.newbranch.backend.domain.note.entity.NoteStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoteResponse {

    private Long id;
    private String title;
    private String content;
    private String sourceType;
    private NoteStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static NoteResponse from(Note note) {
        return NoteResponse.builder()
                .id(note.getId())
                .title(note.getTitle())
                .content(note.getContent())
                .sourceType(note.getSourceType())
                .status(note.getStatus())
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .build();
    }
}