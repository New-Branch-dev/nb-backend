package com.newbranch.backend.domain.attachment.repository;

import com.newbranch.backend.domain.attachment.entity.Attachment;
import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findAllByUserAndIsActiveTrueOrderByCreatedAtDesc(User user);
    List<Attachment> findAllByIdInAndIsActiveTrue(List<Long> ids);
    Optional<Attachment> findByIdAndIsActiveTrue(Long id);
    List<Attachment> findAllByLearningGoalAndIsActiveTrue(LearningGoal learningGoal);
}
