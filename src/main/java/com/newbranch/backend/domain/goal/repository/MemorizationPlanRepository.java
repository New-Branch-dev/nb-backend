package com.newbranch.backend.domain.goal.repository;

import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.goal.entity.MemorizationPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemorizationPlanRepository extends JpaRepository<MemorizationPlan, Long> {
    List<MemorizationPlan> findAllByLearningGoalAndIsActiveTrue(LearningGoal learningGoal);
    Optional<MemorizationPlan> findByIdAndIsActiveTrue(Long id);
}
