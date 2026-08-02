package com.newbranch.backend.domain.consolidation.repository;

import com.newbranch.backend.domain.consolidation.entity.Consolidation;
import com.newbranch.backend.domain.consolidation.entity.ConsolidationAttachment;
import com.newbranch.backend.domain.consolidation.entity.ConsolidationAttachmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsolidationAttachmentRepository extends JpaRepository<ConsolidationAttachment, ConsolidationAttachmentId> {
    List<ConsolidationAttachment> findAllByConsolidationAndIsActiveTrue(Consolidation consolidation);
}
