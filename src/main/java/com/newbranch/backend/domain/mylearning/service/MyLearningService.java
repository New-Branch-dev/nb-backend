package com.newbranch.backend.domain.mylearning.service;

import com.newbranch.backend.domain.mylearning.dto.MyLearningRequest;
import com.newbranch.backend.domain.mylearning.dto.MyLearningResponse;
import com.newbranch.backend.domain.mylearning.entity.UserLearningStyle;
import com.newbranch.backend.domain.mylearning.entity.UserSetting;
import com.newbranch.backend.domain.mylearning.repository.UserLearningStyleRepository;
import com.newbranch.backend.domain.mylearning.repository.UserSettingRepository;
import com.newbranch.backend.domain.user.service.UserQueryService;
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
    private final UserQueryService userQueryService;

    public MyLearningResponse getMyLearningData(String email) {
        Long usersId = userQueryService.findByEmail(email).getId();

        UserSetting setting = userSettingRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        UserLearningStyle style = userLearningStyleRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        return MyLearningResponse.of(setting, style);
    }

    @Transactional
    public MyLearningResponse createMyLearningData(String email, MyLearningRequest dto) {
        Long usersId = userQueryService.findByEmail(email).getId();

        UserSetting setting = UserSetting.builder()
                .usersId(usersId)
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
                .usersId(usersId)
                .learningStyle(dto.getLearningStyle())
                .recommendedMethod(dto.getRecommendedMethod())
                .build();
        userLearningStyleRepository.save(style);

        return MyLearningResponse.of(setting, style);
    }

    @Transactional
    public MyLearningResponse updateMyLearningData(String email, MyLearningRequest dto) {
        Long usersId = userQueryService.findByEmail(email).getId();

        UserSetting setting = userSettingRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        UserLearningStyle style = userLearningStyleRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        setting.setPreferredStudyTime(dto.getPreferredStudyTime());
        setting.setOffDays(dto.getOffDays());
        setting.setPreferredMaterialFormat(dto.getPreferredMaterialFormat());
        setting.setPreferredClassStyle(dto.getPreferredClassStyle());
        setting.setPreferredStudyMethod(dto.getPreferredStudyMethod());
        setting.setPreferredTeacherStyle(dto.getPreferredTeacherStyle());
        setting.setPreferredFriendStyle(dto.getPreferredFriendStyle());
        setting.setExtraNotes(dto.getExtraNotes());
        style.setLearningStyle(dto.getLearningStyle());
        style.setRecommendedMethod(dto.getRecommendedMethod());

        return MyLearningResponse.of(setting, style);
    }

    @Transactional
    public void deleteMyLearningData(String email) {
        Long usersId = userQueryService.findByEmail(email).getId();

        UserSetting setting = userSettingRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
        UserLearningStyle style = userLearningStyleRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));

        setting.setIsActive(false);
        style.setIsActive(false);
    }
}
