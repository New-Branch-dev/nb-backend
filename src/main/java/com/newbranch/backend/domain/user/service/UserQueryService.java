package com.newbranch.backend.domain.user.service;

import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.domain.user.repository.UserRepository;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserRepository userRepository;

    public User findByLoginId(String loginId) {
        return userRepository.findByLoginIdAndIsActiveTrue(loginId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}
