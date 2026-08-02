package com.newbranch.backend.domain.user.repository;

import com.newbranch.backend.domain.user.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 이메일 인증 Repository
 */
@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    /**
     * 토큰으로 이메일 인증 정보를 조회합니다
     */
    Optional<EmailVerification> findByToken(String token);

    /**
     * 이메일로 가장 최근의 이메일 인증 정보를 조회합니다
     */
    Optional<EmailVerification> findTopByEmailOrderByCreatedAtDesc(String email);

    /**
     * 만료된 이메일 인증 정보를 삭제합니다
     */
    void deleteByExpiresAtBefore(LocalDateTime expiresAt);

    /**
     * 이메일로 인증되지 않은 이메일 인증 정보를 조회합니다
     */
    Optional<EmailVerification> findByEmailAndVerifiedFalse(String email);
}