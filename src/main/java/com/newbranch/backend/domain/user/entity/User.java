package com.newbranch.backend.domain.user.entity;

import com.newbranch.backend.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 엔티티
 * users 테이블과 매핑
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "users_id")
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true, length = 50)
    private String loginId;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "nickname", length = 100)
    private String nickname;

    @Column(name = "age_group", length = 20)
    private String ageGroup;

    @Column(name = "school_name", length = 100)
    private String schoolName;

    @Column(name = "google_oauth_id", length = 255)
    private String googleOauthId;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = false;

    @Column(name = "is_guest", nullable = false)
    private Boolean isGuest = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role = Role.ROLE_USER;

    @Builder
    public User(String email, String loginId, String password, String nickname, String ageGroup,
                String schoolName, String googleOauthId, Boolean emailVerified, Boolean isAdmin, Boolean isGuest, Role role) {
        this.email = email;
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.ageGroup = ageGroup;
        this.schoolName = schoolName;
        this.googleOauthId = googleOauthId;
        this.emailVerified = emailVerified != null ? emailVerified : false;
        this.isAdmin = isAdmin != null ? isAdmin : false;
        this.isGuest = isGuest != null ? isGuest : false;
        this.role = role != null ? role : Role.ROLE_USER;
    }

    /**
     * 비밀번호를 업데이트합니다 (암호화된 비밀번호 사용)
     */
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    /**
     * 닉네임을 업데이트합니다
     */
    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 연령대를 업데이트합니다
     */
    public void updateAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    /**
     * 학교명을 업데이트합니다
     */
    public void updateSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    /**
     * 사용자를 비활성화합니다 (소프트 삭제)
     */
    public void deactivate() {
        super.deactivate();
    }

    /**
     * 관리자 권한을 부여합니다
     */
    public void grantAdminRole() {
        this.isAdmin = true;
        this.role = Role.ROLE_ADMIN;
    }

    /**
     * 이메일 인증을 완료합니다
     */
    public void verifyEmail() {
        this.emailVerified = true;
    }

    /**
     * 구글 계정을 연동합니다
     */
    public void linkGoogleAccount(String googleOauthId) {
        this.googleOauthId = googleOauthId;
    }
}
