package com.newbranch.backend.domain.code.entity;

import com.newbranch.backend.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통코드값 엔티티
 * common_code_values 테이블과 매핑
 */
@Entity
@Table(name = "common_code_values",
        uniqueConstraints = @UniqueConstraint(columnNames = {"common_code_id", "common_code_value"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommonCodeValue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "common_code_value_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "common_code_id", nullable = false)
    private CommonCode commonCode;

    @Column(name = "common_code_value", nullable = false, length = 20)
    private String commonCodeValue;

    @Column(name = "common_code_value_name", nullable = false, length = 100)
    private String commonCodeValueName;

    @Builder
    public CommonCodeValue(CommonCode commonCode, String commonCodeValue, String commonCodeValueName) {
        this.commonCode = commonCode;
        this.commonCodeValue = commonCodeValue;
        this.commonCodeValueName = commonCodeValueName;
    }
}
