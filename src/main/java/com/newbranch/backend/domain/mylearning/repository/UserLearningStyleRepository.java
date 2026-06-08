package com.newbranch.backend.domain.mylearning.repository;

import com.newbranch.backend.domain.mylearning.entity.UserLearningStyle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserLearningStyleRepository extends JpaRepository<UserLearningStyle, Long> {
    Optional<UserLearningStyle> findByUsersIdAndIsActiveTrue(Long usersId);
}