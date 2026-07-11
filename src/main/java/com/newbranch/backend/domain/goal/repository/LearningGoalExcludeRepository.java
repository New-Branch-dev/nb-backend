package com.newbranch.backend.domain.goal.repository;

import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.goal.entity.LearningGoalExclude;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningGoalExcludeRepository extends JpaRepository<LearningGoalExclude, Long> {
    List<LearningGoalExclude> findAllByLearningGoalAndIsActiveTrueOrderByExcludeDateAsc(LearningGoal learningGoal);
    void deleteAllByLearningGoal(LearningGoal learningGoal);
}
