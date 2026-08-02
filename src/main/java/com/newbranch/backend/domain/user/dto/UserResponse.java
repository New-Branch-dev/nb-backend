package com.newbranch.backend.domain.user.dto;

import com.newbranch.backend.domain.user.entity.Role;
import com.newbranch.backend.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * 사용자 정보 응답 DTO
 */
@Getter
@Builder
@Schema(description = "사용자 정보 응답")
public class UserResponse {

    @Schema(description = "사용자 ID", example = "1")
    private Long id;

    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "연령대", example = "20대")
    private String ageGroup;

    @Schema(description = "학교명", example = "서울대학교")
    private String schoolName;

    @Schema(description = "관리자 여부", example = "false")
    private Boolean isAdmin;

    @Schema(description = "게스트 여부", example = "false")
    private Boolean isGuest;

    @Schema(description = "권한", example = "ROLE_USER")
    private Role role;

    @Schema(description = "생성 시간", example = "2024-03-28T10:15:30+09:00")
    private LocalDateTime createdAt;

    /**
     * User 엔티티로부터 DTO를 생성합니다
     */
    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .ageGroup(user.getAgeGroup())
                .schoolName(user.getSchoolName())
                .isAdmin(user.getIsAdmin())
                .isGuest(user.getIsGuest())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
