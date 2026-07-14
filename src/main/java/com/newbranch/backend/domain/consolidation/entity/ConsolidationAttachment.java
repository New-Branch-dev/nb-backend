package com.newbranch.backend.domain.consolidation.entity;

import com.newbranch.backend.domain.attachment.entity.Attachment;
import com.newbranch.backend.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 단권화 ↔ 첨부파일 매핑 (복합키: co_id + attachment_id)
 */
@Entity
@Table(name = "consolidation_attachments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsolidationAttachment extends BaseEntity {

    @EmbeddedId
    private ConsolidationAttachmentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("consolidationId")
    @JoinColumn(name = "co_id")
    private Consolidation consolidation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("attachmentId")
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;

    public ConsolidationAttachment(Consolidation consolidation, Attachment attachment) {
        this.consolidation = consolidation;
        this.attachment = attachment;
        this.id = new ConsolidationAttachmentId(consolidation.getId(), attachment.getId());
    }
}
