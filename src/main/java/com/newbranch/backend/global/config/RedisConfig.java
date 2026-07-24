package com.newbranch.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정 클래스
 * Refresh Token 저장소로 사용
 */
@Configuration
public class RedisConfig {

    /**
     * RedisTemplate을 생성합니다 (Key와 Value 모두 String으로 직렬화)
     *
     * RedisConnectionFactory는 Spring Boot의 자동 설정에 위임한다 - 직접 LettuceConnectionFactory를
     * host/port로만 생성하면 spring.data.redis.password/database/timeout/lettuce.pool.* 설정이
     * 전부 무시되는 버그가 있었다.
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }
}
