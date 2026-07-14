package com.newbranch.backend.domain.consolidation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ConsolidationAttachmentId implements Serializable {

    @Column(name = "co_id")
    private Long consolidationId;

    @Column(name = "attachment_id")
    private Long attachmentId;
}
