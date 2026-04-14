package com.newbranch.backend.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 권한 Enum
 * Spring Security의 GrantedAuthority와 호환
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    ROLE_USER("ROLE_USER", "일반 사용자"),
    ROLE_ADMIN("ROLE_ADMIN", "관리자"),
    ROLE_GUEST("ROLE_GUEST", "게스트");

    private final String key;
    private final String description;
}
