package com.newbranch.backend.global.security.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.oauth2.client.jackson2.OAuth2ClientJackson2Module;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CookieUtils {

    // fix: 서명 없는 쿠키값에 Java 네이티브 역직렬화(ObjectInputStream)를 쓰면 가젯체인을 통한
    // RCE로 이어질 수 있어(CWE-502) Jackson JSON 직렬화로 교체 (siwon)
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new OAuth2ClientJackson2Module());

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();
        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAgeSeconds) {
        // fix: jakarta.servlet.http.Cookie는 Secure/SameSite를 설정할 수 없어 HTTPS가 아닌 경로로
        // 쿠키가 새거나 CSRF에 취약할 수 있었음. ResponseCookie로 교체해 Secure+SameSite=Lax 적용 (siwon)
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .maxAge(Duration.ofSeconds(maxAgeSeconds))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return;
        Arrays.stream(cookies)
                .filter(c -> c.getName().equals(name))
                .forEach(c -> {
                    c.setValue("");
                    c.setPath("/");
                    c.setMaxAge(0);
                    response.addCookie(c);
                });
    }

    public static String serialize(Object object) {
        try {
            byte[] json = OBJECT_MAPPER.writeValueAsBytes(object);
            return Base64.getUrlEncoder().encodeToString(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object", e);
        }
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(cookie.getValue());
            return OBJECT_MAPPER.readValue(new String(bytes, StandardCharsets.UTF_8), cls);
        } catch (Exception e) {
            log.warn("Failed to deserialize cookie: {}", e.getMessage());
            return null;
        }
    }
}
