package com.newbranch.backend.domain.note.dto;

import com.newbranch.backend.domain.note.entity.MaterialType;
import com.newbranch.backend.domain.note.entity.NoteMaterial;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NoteMaterialResponse {

    private Long id;
    private MaterialType materialType;
    private String fileName;
    private String fileUrl;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public static NoteMaterialResponse from(NoteMaterial noteMaterial) {
        return NoteMaterialResponse.builder()
                .id(noteMaterial.getId())
                .materialType(noteMaterial.getMaterialType())
                .fileName(noteMaterial.getFileName())
                .fileUrl(noteMaterial.getFileUrl())
                .title(noteMaterial.getTitle())
                .content(noteMaterial.getContent())
                .createdAt(noteMaterial.getCreatedAt())
                .build();
    }
}