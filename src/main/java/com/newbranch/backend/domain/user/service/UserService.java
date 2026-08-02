package com.newbranch.backend.domain.user.service;

import com.newbranch.backend.domain.user.dto.UpdateUserRequest;
import com.newbranch.backend.domain.user.dto.UserResponse;
import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.domain.user.repository.UserRepository;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관리 비즈니스 로직을 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 현재 로그인한 사용자의 정보를 조회합니다.
     *
     * @param email 사용자 이메일
     * @return 사용자 정보
     * @throws BusinessException 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = findActiveUserByEmail(email);
        return UserResponse.from(user);
    }

    /**
     * 현재 로그인한 사용자의 정보를 수정합니다.
     *
     * @param email 사용자 이메일
     * @param request 수정할 정보
     * @return 수정된 사용자 정보
     * @throws BusinessException 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public UserResponse updateCurrentUser(String email, UpdateUserRequest request) {
        User user = findActiveUserByEmail(email);
        updateUserInfo(user, request);
        log.info("User updated: {}", email);
        return UserResponse.from(user);
    }

    /**
     * 현재 로그인한 사용자를 탈퇴 처리합니다 (소프트 삭제).
     *
     * @param email 사용자 이메일
     * @throws BusinessException 사용자를 찾을 수 없는 경우
     */
    @Transactional
    public void deleteCurrentUser(String email) {
        User user = findActiveUserByEmail(email);
        user.deactivate();
        log.info("User deactivated: {}", email);
    }

    /**
     * 이메일로 활성화된 사용자를 조회합니다.
     */
    private User findActiveUserByEmail(String email) {
        return userRepository.findByLoginIdAndIsActiveTrue(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    /**
     * 사용자 정보를 업데이트합니다.
     */
    private void updateUserInfo(User user, UpdateUserRequest request) {
        updateNicknameIfPresent(user, request.getNickname());
        updateAgeGroupIfPresent(user, request.getAgeGroup());
        updateSchoolNameIfPresent(user, request.getSchoolName());
        updatePasswordIfPresent(user, request);
    }

    private void updateNicknameIfPresent(User user, String nickname) {
        if (nickname != null) {
            user.updateNickname(nickname);
        }
    }

    private void updateAgeGroupIfPresent(User user, String ageGroup) {
        if (ageGroup != null) {
            user.updateAgeGroup(ageGroup);
        }
    }

    private void updateSchoolNameIfPresent(User user, String schoolName) {
        if (schoolName != null) {
            user.updateSchoolName(schoolName);
        }
    }

    private void updatePasswordIfPresent(User user, UpdateUserRequest request) {
        String newPassword = request.getNewPassword();
        if (newPassword == null) {
            return;
        }
        // fix: 현재 비밀번호 확인 없이 변경 가능해서 탈취된 access token만으로 영구 계정 탈취가
        // 가능했던 문제 차단 (siwon)
        if (request.getCurrentPassword() == null
                || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CURRENT_PASSWORD);
        }
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.updatePassword(encodedPassword);
    }
}