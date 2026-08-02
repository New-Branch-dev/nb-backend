package com.newbranch.backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 사용자 정보 수정 요청 DTO
 */
@Getter
@Schema(description = "사용자 정보 수정 요청")
public class UpdateUserRequest {

    @Size(max = 100, message = "닉네임은 100자를 초과할 수 없습니다")
    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "연령대", example = "20대")
    private String ageGroup;

    @Schema(description = "학교명", example = "한국중학교")
    private String schoolName;

    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    @Schema(description = "새 비밀번호 (변경 시에만)", example = "newPassword123!")
    private String newPassword;

    // fix: 비밀번호 변경 시 현재 비밀번호 확인이 없어 탈취된 access token만으로 영구 계정 탈취가
    // 가능했던 문제라 현재 비밀번호 필드 추가 (siwon)
    @Schema(description = "현재 비밀번호 (비밀번호 변경 시 필수)", example = "currentPassword123!")
    private String currentPassword;
}
