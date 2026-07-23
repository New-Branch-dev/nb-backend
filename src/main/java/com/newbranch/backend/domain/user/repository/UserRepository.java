package com.newbranch.backend.domain.user.repository;

import com.newbranch.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User 엔티티에 대한 Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자를 조회합니다
     */
    Optional<User> findByEmail(String email);

    /**
     * 이메일로 활성화된 사용자를 조회합니다
     */
    Optional<User> findByLoginIdAndIsActiveTrue(String loginId);

    /**
     * 이메일로 활성화된 사용자를 조회합니다
     */
    Optional<User> findByEmailAndIsActiveTrue(String email);

    /**
     * 이메일 중복 여부를 확인합니다
     */
    boolean existsByEmail(String email);

    /**
     * Google OAuth ID로 사용자를 조회합니다
     */
    Optional<User> findByGoogleOauthId(String googleOauthId);

    /**
     * 아이디 중복 여부를 확인합니다
     */
    boolean existsByLoginId(String loginId);

    /**
     * 사용자 로그인 ID를 조회합니다
     */
    Optional<User> findByLoginId(String loginId);
}
