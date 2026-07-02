package com.newbranch.backend.domain.goal.repository;

import com.newbranch.backend.domain.goal.entity.ByDayStudyTime;
import com.newbranch.backend.domain.goal.entity.LearningGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ByDayStudyTimeRepository extends JpaRepository<ByDayStudyTime, Long> {
    List<ByDayStudyTime> findAllByLearningGoalAndIsActiveTrueOrderByDayOfWeekAsc(LearningGoal learningGoal);
    void deleteAllByLearningGoal(LearningGoal learningGoal);
}
