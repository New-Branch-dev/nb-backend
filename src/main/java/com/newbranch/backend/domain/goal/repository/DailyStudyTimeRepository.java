package com.newbranch.backend.domain.goal.repository;

import com.newbranch.backend.domain.goal.entity.DailyStudyTime;
import com.newbranch.backend.domain.goal.entity.LearningGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyStudyTimeRepository extends JpaRepository<DailyStudyTime, Long> {
    List<DailyStudyTime> findAllByLearningGoalAndIsActiveTrueOrderByStudyDateAsc(LearningGoal learningGoal);
    void deleteAllByLearningGoal(LearningGoal learningGoal);
}