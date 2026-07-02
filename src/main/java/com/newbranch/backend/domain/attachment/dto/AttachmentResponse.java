package com.newbranch.backend.domain.attachment.dto;

import com.newbranch.backend.domain.attachment.entity.Attachment;
import com.newbranch.backend.domain.attachment.entity.AttachmentType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class AttachmentResponse {

    private Long attachmentId;
    private Long lgId;
    private AttachmentType attachmentType;
    private String title;
    private String fileName;
    private String fileUrl;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AttachmentResponse from(Attachment attachment) {
        return AttachmentResponse.builder()
                .attachmentId(attachment.getId())
                .lgId(attachment.getLearningGoal() != null ? attachment.getLearningGoal().getId() : null)
                .attachmentType(attachment.getAttachmentType())
                .title(attachment.getTitle())
                .fileName(attachment.getFileName())
                .fileUrl(attachment.getFileUrl())
                .content(attachment.getContent())
                .createdAt(attachment.getCreatedAt())
                .updatedAt(attachment.getUpdatedAt())
                .build();
    }
}
