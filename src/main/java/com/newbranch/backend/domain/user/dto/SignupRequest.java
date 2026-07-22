package com.newbranch.backend.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Schema(description = "회원가입 요청")
public class SignupRequest {

    @NotBlank(message = "아이디는 필수입니다")
    @Size(min=5, max=15, message = "아이디는 최소 5자 이상, 15자 이하여야 합니다.")
    @Schema(description = "아이디", example = "user")
    private String loginId;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Schema(description = "이메일", example = "user@example.com")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    @Schema(description = "비밀번호", example = "password123!")
    private String password;

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(max = 100, message = "닉네임은 100자를 초과할 수 없습니다")
    @Schema(description = "닉네임", example = "홍길동")
    private String nickname;

    @Schema(description = "연령대", example = "20대")
    private String ageGroup;

    @Schema(description = "학교명", example = "한국중학교")
    private String schoolName;
}
