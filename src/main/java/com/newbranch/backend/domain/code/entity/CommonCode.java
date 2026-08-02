package com.newbranch.backend.domain.code.entity;

import com.newbranch.backend.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통코드 엔티티
 * common_codes 테이블과 매핑
 */
@Entity
@Table(name = "common_codes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCode extends BaseEntity {

    @Id
    @Column(name = "common_code_id", length = 50)
    private String commonCodeId;

    @Column(name = "common_code_name", nullable = false, length = 100)
    private String commonCodeName;

    @Builder
    public CommonCode(String commonCodeId, String commonCodeName) {
        this.commonCodeId = commonCodeId;
        this.commonCodeName = commonCodeName;
    }
}
