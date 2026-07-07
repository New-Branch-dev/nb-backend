package com.newbranch.backend.domain.mylearning.service;

import com.newbranch.backend.domain.mylearning.dto.MyLearningRequest;
import com.newbranch.backend.domain.mylearning.dto.MyLearningResponse;
import com.newbranch.backend.domain.mylearning.entity.UserLearningStyle;
import com.newbranch.backend.domain.mylearning.entity.UserSetting;
import com.newbranch.backend.domain.mylearning.repository.UserLearningStyleRepository;
import com.newbranch.backend.domain.mylearning.repository.UserSettingRepository;
import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.domain.user.repository.UserRepository;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyLearningService {

    private final UserRepository userRepository;
    private final UserSettingRepository userSettingRepository;
    private final UserLearningStyleRepository userLearningStyleRepository;

    /**
     * 나만의 학습 조회
     */
    public MyLearningResponse getMyLearningData(String email) {
        User user = findActiveUser(email);

        UserSetting setting = findActiveSetting(user.getId());
        UserLearningStyle style = findActiveStyle(user.getId());

        return MyLearningResponse.of(user, setting, style);
    }

    /**
     * 나만의 학습 등록 (최초 1회)
     */
    @Transactional
    public MyLearningResponse createMyLearningData(String email, MyLearningRequest request) {
        User user = findActiveUser(email);

        if (userSettingRepository.existsByUsersIdAndIsActiveTrue(user.getId())) {
            throw new BusinessException(ErrorCode.MY_LEARNING_ALREADY_EXISTS);
        }

        user.updateNickname(request.getNickname());
        user.updateSchoolName(request.getSchoolName());

        UserSetting setting = UserSetting.builder()
                .usersId(user.getId())
                .preferredStudyTime(request.getPreferredStudyTime())
                .offDays(request.getOffDays())
                .interestCd(request.getInterestCd())
                .aptitudeCd(request.getAptitudeCd())
                .personalityCd(request.getPersonalityCd())
                .learningDisposition(request.getLearningDisposition())
                .preferredDataFormat(request.getPreferredDataFormat())
                .preferredTeachingStyle(request.getPreferredTeachingStyle())
                .preferredLearningMethod(request.getPreferredLearningMethod())
                .teacherStyle(request.getTeacherStyle())
                .teammateStyle(request.getTeammateStyle())
                .build();
        userSettingRepository.save(setting);

        UserLearningStyle style = UserLearningStyle.builder()
                .usersId(user.getId())
                .learningStyle(request.getLearningStyle())
                .recommendedMethod(request.getRecommendedMethod())
                .build();
        userLearningStyleRepository.save(style);

        log.info("MyLearning created: {}", email);
        return MyLearningResponse.of(user, setting, style);
    }

    /**
     * 나만의 학습 수정
     */
    @Transactional
    public MyLearningResponse updateMyLearningData(String email, MyLearningRequest request) {
        User user = findActiveUser(email);

        UserSetting setting = findActiveSetting(user.getId());
        UserLearningStyle style = findActiveStyle(user.getId());

        user.updateNickname(request.getNickname());
        user.updateSchoolName(request.getSchoolName());

        setting.update(
                request.getPreferredStudyTime(),
                request.getOffDays(),
                request.getInterestCd(),
                request.getAptitudeCd(),
                request.getPersonalityCd(),
                request.getLearningDisposition(),
                request.getPreferredDataFormat(),
                request.getPreferredTeachingStyle(),
                request.getPreferredLearningMethod(),
                request.getTeacherStyle(),
                request.getTeammateStyle()
        );
        style.update(request.getLearningStyle(), request.getRecommendedMethod());

        log.info("MyLearning updated: {}", email);
        return MyLearningResponse.of(user, setting, style);
    }

    /**
     * 나만의 학습 삭제 (소프트 삭제 - is_active = false)
     */
    @Transactional
    public void deleteMyLearningData(String email) {
        User user = findActiveUser(email);

        UserSetting setting = findActiveSetting(user.getId());
        UserLearningStyle style = findActiveStyle(user.getId());

        setting.deactivate();
        style.deactivate();

        log.info("MyLearning deactivated: {}", email);
    }

    private User findActiveUser(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private UserSetting findActiveSetting(Long usersId) {
        return userSettingRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_SETTING_NOT_FOUND));
    }

    private UserLearningStyle findActiveStyle(Long usersId) {
        return userLearningStyleRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_LEARNING_STYLE_NOT_FOUND));
    }
}
