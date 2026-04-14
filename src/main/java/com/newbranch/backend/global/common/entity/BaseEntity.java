package com.newbranch.backend.global.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * 모든 엔티티의 공통 필드를 정의하는 Base Entity
 * - created_at: 생성 시간 (자동 설정)
 * - updated_at: 수정 시간 (자동 갱신)
 * - is_active: 활성 상태 (soft delete 지원)
 */
@Getter
@MappedSuperclass
@EnableJpaAuditing
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    /**
     * 엔티티를 비활성화합니다 (Soft Delete)
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 엔티티를 활성화합니다
     */
    public void activate() {
        this.isActive = true;
    }
}
