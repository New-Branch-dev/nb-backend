package com.newbranch.backend.domain.note.repository;

import com.newbranch.backend.domain.note.entity.Note;
import com.newbranch.backend.domain.note.entity.NoteStatus;
import com.newbranch.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByUserAndIsActiveTrueAndStatus(User user, NoteStatus status);

    Optional<Note> findByIdAndIsActiveTrue(Long id);

    List<Note> findAllByIdInAndIsActiveTrue(List<Long> ids);

    List<Note> findAllByGoalAndIsActiveTrue(com.newbranch.backend.domain.goal.entity.LearningGoal goal);
}