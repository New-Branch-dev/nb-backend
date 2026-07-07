package com.newbranch.backend.domain.goal.repository;

import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.goal.entity.LearningGoalMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningGoalMethodRepository extends JpaRepository<LearningGoalMethod, Long> {
    List<LearningGoalMethod> findAllByLearningGoalAndIsActiveTrue(LearningGoal learningGoal);
    void deleteAllByLearningGoal(LearningGoal learningGoal);
}
