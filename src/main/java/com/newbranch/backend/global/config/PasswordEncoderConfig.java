package com.newbranch.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 비밀번호 암호화 Encoder 설정
 *
 * fix: SecurityConfig 안에 있던 이 빈을 CustomOAuth2UserService가 필요로 하게 되면서
 * SecurityConfig(CustomOAuth2UserService 필요) -> CustomOAuth2UserService(PasswordEncoder 필요)
 * -> PasswordEncoder(SecurityConfig의 @Bean 메서드)로 순환참조가 생겨 별도 설정 클래스로 분리 (siwon)
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
