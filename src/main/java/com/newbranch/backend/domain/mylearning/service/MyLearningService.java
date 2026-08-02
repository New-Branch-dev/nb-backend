package com.newbranch.backend.domain.mylearning.service;

import com.newbranch.backend.domain.mylearning.dto.MyLearningRequest;
import com.newbranch.backend.domain.mylearning.entity.UserLearningStyle;
import com.newbranch.backend.domain.mylearning.entity.UserSetting;
import com.newbranch.backend.domain.mylearning.repository.UserLearningStyleRepository;
import com.newbranch.backend.domain.mylearning.repository.UserSettingRepository;
import com.newbranch.backend.domain.user.entity.User;
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

    /**
     * 회원 설정, 학습유형 조회
     */
    public MyLearningRequest getMyLearningData(String email, Long usersId) {
        UserSetting setting = userSettingRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_SETTING_NOT_FOUND));

        UserLearningStyle style = userLearningStyleRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_LEARNING_STYLE_NOT_FOUND));

        // fix: usersId를 그대로 신뢰하면 타 사용자 데이터에 접근 가능(IDOR)해서 소유권 검증 추가.
        // 존재 여부(404)를 먼저 확인한 뒤 소유권(403)을 확인해야 없는 리소스 조회가 403으로 잘못
        // 보고되지 않는다 (siwon)
        validateOwner(email, usersId);

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
    public MyLearningRequest createMyLearningData(String email, MyLearningRequest dto) {
        // fix: 요청 바디의 usersId를 그대로 쓰면 타 사용자 명의로 데이터 생성이 가능해서 토큰 소유자 ID로 대체 (siwon)
        Long usersId = userQueryService.findByLoginId(email).getId();
        // fix: 활성 설정이 이미 있는 상태에서 재생성하면 findByUsersIdAndIsActiveTrue가 복수 row로 500 나서 사전 차단 (siwon)
        if (userSettingRepository.existsByUsersIdAndIsActiveTrue(usersId)) {
            throw new BusinessException(ErrorCode.MY_LEARNING_ALREADY_EXISTS);
        }

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

        return dto;
    }

    /**
     * 회원 설정, 학습유형 수정
     */
    @Transactional
    public MyLearningRequest updateMyLearningData(String email, Long usersId, MyLearningRequest dto) {
        UserSetting setting = userSettingRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_SETTING_NOT_FOUND));

        UserLearningStyle style = userLearningStyleRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_LEARNING_STYLE_NOT_FOUND));

        // fix: 경로의 usersId가 본인 것인지 확인 없이 수정 가능했던 IDOR 차단 (존재 확인 후 소유권 확인) (siwon)
        validateOwner(email, usersId);

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
    public void deleteMyLearningData(String email, Long usersId) {
        UserSetting setting = userSettingRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_SETTING_NOT_FOUND));

        UserLearningStyle style = userLearningStyleRepository.findByUsersIdAndIsActiveTrue(usersId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_LEARNING_STYLE_NOT_FOUND));

        // fix: 경로의 usersId가 본인 것인지 확인 없이 삭제 가능했던 IDOR 차단 (존재 확인 후 소유권 확인) (siwon)
        validateOwner(email, usersId);

        setting.deactivate();
        style.deactivate();
    }

    private void validateOwner(String email, Long usersId) {
        User user = userQueryService.findByLoginId(email);
        if (!user.getId().equals(usersId)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }
}