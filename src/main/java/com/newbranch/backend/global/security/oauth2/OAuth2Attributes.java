package com.newbranch.backend.global.security.oauth2;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

/**
 * OAuth2 사용자 정보를 담는 DTO
 */
@Getter
@Builder
public class OAuth2Attributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String oauthId;
    private String name;
    private String email;

    /**
     * OAuth2User로부터 OAuth2Attributes를 생성합니다
     */
    public static OAuth2Attributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("google".equals(registrationId)) {
            return ofGoogle(userNameAttributeName, attributes);
        }

        throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId);
    }

    /**
     * Google OAuth2 사용자 정보를 변환합니다
     */
    private static OAuth2Attributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .oauthId((String) attributes.get("sub"))
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
}