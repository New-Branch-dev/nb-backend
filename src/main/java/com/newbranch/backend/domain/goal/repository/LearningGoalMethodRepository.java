package com.newbranch.backend.domain.goal.repository;

import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.goal.entity.LearningGoalMethod;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LearningGoalMethodRepository extends JpaRepository<LearningGoalMethod, Long> {
    List<LearningGoalMethod> findAllByLearningGoalAndIsActiveTrue(LearningGoal learningGoal);
    // fix: 목표 목록/검색에서 목표당 1쿼리씩 나가는 N+1을 없애기 위한 배치 조회 (siwon)
    List<LearningGoalMethod> findAllByLearningGoalInAndIsActiveTrue(List<LearningGoal> learningGoals);
    void deleteAllByLearningGoal(LearningGoal learningGoal);
}
