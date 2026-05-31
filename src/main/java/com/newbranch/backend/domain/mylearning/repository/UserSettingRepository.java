package com.newbranch.backend.domain.mylearning.repository;

import com.newbranch.backend.domain.mylearning.entity.UserSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, Long> {
    Optional<UserSetting> findByUsersIdAndIsActiveTrue(Long usersId);
}