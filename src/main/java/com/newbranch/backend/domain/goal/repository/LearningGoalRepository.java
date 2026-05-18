package com.newbranch.backend.domain.goal.repository;

import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LearningGoalRepository extends JpaRepository<LearningGoal, Long> {
    List<LearningGoal> findAllByUserAndIsActiveTrueOrderByCreatedAtDesc(User user);
    Optional<LearningGoal> findByIdAndIsActiveTrue(Long id);
    List<LearningGoal> findAllByUserAndIsActiveTrueAndTitleContainingIgnoreCase(User user, String keyword);
}
