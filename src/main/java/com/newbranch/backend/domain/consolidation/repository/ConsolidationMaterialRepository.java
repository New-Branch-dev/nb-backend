package com.newbranch.backend.domain.consolidation.repository;

import com.newbranch.backend.domain.consolidation.entity.Consolidation;
import com.newbranch.backend.domain.consolidation.entity.ConsolidationMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsolidationMaterialRepository extends JpaRepository<ConsolidationMaterial, Long> {
    List<ConsolidationMaterial> findAllByConsolidationAndIsActiveTrueOrderBySortOrderAsc(Consolidation consolidation);
    void deleteAllByConsolidation(Consolidation consolidation);
}
