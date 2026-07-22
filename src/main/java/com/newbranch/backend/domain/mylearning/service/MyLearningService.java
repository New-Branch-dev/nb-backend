package com.newbranch.backend.domain.mylearning.service;

import com.newbranch.backend.domain.mylearning.dto.MyLearningRequest;
import com.newbranch.backend.domain.mylearning.entity.UserLearningStyle;
import com.newbranch.backend.domain.mylearning.entity.UserSetting;
import com.newbranch.backend.domain.mylearning.repository.UserLearningStyleRepository;
import com.newbranch.backend.domain.mylearning.repository.UserSettingRepository;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyLearningService {

    private final UserSettingRepository userSettingRepository;
    private final UserLearningStyleRepository userLearningStyleRepository;

    /**
     * 회원 설정, 학습유형 조회
     */
    public MyLearningRequest getMyLearningData(Long usersId) {
        UserSetting setting = userSettingRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_SETTING_NOT_FOUND));

        UserLearningStyle style = userLearningStyleRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_LEARNING_STYLE_NOT_FOUND));

        return MyLearningRequest.builder()
                .usersId(setting.getUsersId())
                .preferredStudyTime(setting.getPreferredStudyTime())
                .offDays(setting.getOffDays())
                .preferredMaterialFormat(setting.getPreferredMaterialFormat())
                .preferredClassStyle(setting.getPreferredClassStyle())
                .preferredStudyMethod(setting.getPreferredStudyMethod())
                .preferredTeacherStyle(setting.getPreferredTeacherStyle())
                .preferredFriendStyle(setting.getPreferredFriendStyle())
                .extraNotes(setting.getExtraNotes())
                .learningStyle(style.getLearningStyle())
                .recommendedMethod(style.getRecommendedMethod())
                .build();
    }

    /**
     * 회원 설정, 학습유형 등록
     */
    @Transactional
    public MyLearningRequest createMyLearningData(MyLearningRequest dto) {
        if (userSettingRepository.existsByUsersIdAndIsActiveTrue(dto.getUsersId())) {
            throw new BusinessException(ErrorCode.MY_LEARNING_ALREADY_EXISTS);
        }

        UserSetting setting = UserSetting.builder()
                .usersId(dto.getUsersId())
                .preferredStudyTime(dto.getPreferredStudyTime())
                .offDays(dto.getOffDays())
                .preferredMaterialFormat(dto.getPreferredMaterialFormat())
                .preferredClassStyle(dto.getPreferredClassStyle())
                .preferredStudyMethod(dto.getPreferredStudyMethod())
                .preferredTeacherStyle(dto.getPreferredTeacherStyle())
                .preferredFriendStyle(dto.getPreferredFriendStyle())
                .extraNotes(dto.getExtraNotes())
                .build();
        userSettingRepository.save(setting);

        UserLearningStyle style = UserLearningStyle.builder()
                .usersId(dto.getUsersId())
                .learningStyle(dto.getLearningStyle())
                .recommendedMethod(dto.getRecommendedMethod())
                .build();
        userLearningStyleRepository.save(style);

        return dto;
    }

    /**
     * 회원 설정, 학습유형 수정
     */
    @Transactional
    public MyLearningRequest updateMyLearningData(Long usersId, MyLearningRequest dto) {

        UserSetting setting = userSettingRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_SETTING_NOT_FOUND));

        UserLearningStyle style = userLearningStyleRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_LEARNING_STYLE_NOT_FOUND));

        setting.update(dto.getPreferredStudyTime(), dto.getOffDays(), dto.getPreferredMaterialFormat(),
                dto.getPreferredClassStyle(), dto.getPreferredStudyMethod(), dto.getPreferredTeacherStyle(),
                dto.getPreferredFriendStyle(), dto.getExtraNotes());

        style.update(dto.getLearningStyle(), dto.getRecommendedMethod());

        return dto;
    }

    /**
     * 회원 설정, 학습유형 삭제
     */
    @Transactional
    public void deleteMyLearningData(Long usersId) {
        UserSetting setting = userSettingRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_SETTING_NOT_FOUND));

        UserLearningStyle style = userLearningStyleRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_LEARNING_STYLE_NOT_FOUND));

        setting.deactivate();
        style.deactivate();
    }
}