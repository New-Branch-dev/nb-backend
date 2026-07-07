package com.newbranch.backend.domain.goal.repository;

import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.goal.entity.StudyPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StudyPlanRepository extends JpaRepository<StudyPlan, Long> {
    List<StudyPlan> findAllByLearningGoalAndIsActiveTrue(LearningGoal learningGoal);
    Optional<StudyPlan> findByIdAndIsActiveTrue(Long id);
}