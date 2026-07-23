package com.newbranch.backend.global.security.oauth2;

import com.newbranch.backend.domain.user.entity.Role;
import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * OAuth2 사용자 정보를 처리하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        OAuth2Attributes attributes = OAuth2Attributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        return new OAuth2UserPrincipal(user, attributes.getAttributes(), attributes.getNameAttributeKey());
    }

    /**
     * OAuth2 사용자 정보를 저장하거나 업데이트합니다
     */
    private User saveOrUpdate(OAuth2Attributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> updateExistingUser(entity, attributes))
                .orElseGet(() -> createNewUser(attributes));

        return userRepository.save(user);
    }

    /**
     * 기존 사용자 정보를 업데이트합니다
     */
    private User updateExistingUser(User user, OAuth2Attributes attributes) {
        log.info("Updating existing user from OAuth2: {}", attributes.getEmail());
        return user;
    }

    /**
     * 새로운 사용자를 생성합니다
     */
    private User createNewUser(OAuth2Attributes attributes) {
        log.info("Creating new user from OAuth2: {}", attributes.getEmail());

        return User.builder()
                .loginId(attributes.getEmail())
                .email(attributes.getEmail())
                .password("")
                .nickname(attributes.getName())
                .googleOauthId(attributes.getOauthId())
                .emailVerified(true)
                .role(Role.ROLE_USER)
                .build();
    }
}