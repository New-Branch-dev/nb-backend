package com.newbranch.backend.domain.goal.repository;

import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.goal.entity.RetrievalPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RetrievalPlanRepository extends JpaRepository<RetrievalPlan, Long> {
    List<RetrievalPlan> findAllByLearningGoalAndIsActiveTrue(LearningGoal learningGoal);
    Optional<RetrievalPlan> findByIdAndIsActiveTrue(Long id);
}
