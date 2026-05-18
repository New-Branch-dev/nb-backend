package com.newbranch.backend.domain.goal.repository;

import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LearningGoalRepository extends JpaRepository<LearningGoal, Long> {
    List<LearningGoal> findAllByUserAndIsActiveTrueOrderByCreatedAtDesc(User user);
    Optional<LearningGoal> findByIdAndIsActiveTrue(Long id);

    @Query("SELECT g FROM LearningGoal g WHERE g.user = :user AND g.isActive = true " +
           "AND (LOWER(g.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(g.purpose) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<LearningGoal> searchByKeyword(@Param("user") User user, @Param("keyword") String keyword);
}
