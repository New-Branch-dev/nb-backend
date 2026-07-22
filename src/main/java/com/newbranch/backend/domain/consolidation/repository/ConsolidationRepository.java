package com.newbranch.backend.domain.consolidation.repository;

import com.newbranch.backend.domain.consolidation.entity.Consolidation;
import com.newbranch.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsolidationRepository extends JpaRepository<Consolidation, Long> {
    List<Consolidation> findAllByUserAndIsActiveTrueOrderByCreatedAtDesc(User user);
    Optional<Consolidation> findByIdAndIsActiveTrue(Long id);
}
