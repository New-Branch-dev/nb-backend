package com.newbranch.backend.domain.user.service;

import com.newbranch.backend.domain.user.entity.EmailVerification;
import com.newbranch.backend.domain.user.repository.EmailVerificationRepository;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 이메일 발송 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private final RedisTemplate<String, String> redisTemplate;

    // fix: Math.random()은 예측 가능해 6자리 코드를 통계적으로 더 쉽게 맞출 수 있어 CSPRNG로 교체 (siwon)
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    // fix: 시도 횟수 제한이 없어 90만 경우의 수인 6자리 코드를 브루트포스로 뚫을 수 있었음 (siwon)
    private static final String VERIFY_ATTEMPT_PREFIX = "EV_ATTEMPT:";
    private static final int MAX_VERIFY_ATTEMPTS = 5;
    private static final Duration VERIFY_ATTEMPT_WINDOW = Duration.ofMinutes(10);

    @Value("${email.from}")
    private String fromEmail;

    @Value("${email.verification.base-url}")
    private String baseUrl;

    @Value("${email.verification.expiration}")
    private Long verificationExpiration;

    /**
     * 이메일 인증 메일을 발송합니다
     */
    @Transactional
    public void sendVerificationEmail(String email) {
        String token = generateVerificationToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(verificationExpiration / 1000);

        EmailVerification emailVerification = EmailVerification.builder()
                .email(email)
                .token(token)
                .expiresAt(expiresAt)
                .build();

        emailVerificationRepository.save(emailVerification);

        String subject = "[NewBranch] 회원가입 이메일 인증번호입니다.";
        String content = buildVerificationEmailContent(token);

        sendEmail(email, subject, content);
        log.info("Verification code [{}] sent to: {}", token, email);
    }

    /**
     * 이메일 인증 토큰을 검증하고 인증을 완료합니다
     */
    @Transactional
    public void verifyEmail(String token) {
        EmailVerification emailVerification = emailVerificationRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_VERIFICATION_TOKEN));

        if (emailVerification.isVerified()) {
            throw new BusinessException(ErrorCode.ALREADY_VERIFIED_EMAIL);
        }

        if (emailVerification.isExpired()) {
            throw new BusinessException(ErrorCode.EXPIRED_VERIFICATION_TOKEN);
        }

        emailVerification.verify();
        log.info("Email verified: {}", emailVerification.getEmail());
    }

    /**
     * 프론트엔드 화면에서 입력한 이메일과 인증번호를 실시간 검증합니다
     */
    @Transactional
    public void verifyEmailCode(String email, String token) {
        // fix: 검증 시도마다 카운트를 올리고 한도를 넘으면 차단해서 브루트포스 방지 (siwon)
        checkAndIncrementAttempts(email);

        EmailVerification emailVerification = emailVerificationRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_VERIFICATION_TOKEN));

        if (!emailVerification.getToken().equals(token)) {
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_TOKEN);
        }

        if (emailVerification.isVerified()) {
            throw new BusinessException(ErrorCode.ALREADY_VERIFIED_EMAIL);
        }

        if (emailVerification.isExpired()) {
            throw new BusinessException(ErrorCode.EXPIRED_VERIFICATION_TOKEN);
        }

        emailVerification.verify();
        redisTemplate.delete(VERIFY_ATTEMPT_PREFIX + email);
        log.info("Email successfully verified via code: {}", email);
    }

    /**
     * 이메일별 검증 시도 횟수를 증가시키고 한도 초과 시 예외를 던집니다
     */
    private void checkAndIncrementAttempts(String email) {
        String key = VERIFY_ATTEMPT_PREFIX + email;
        Long attempts = redisTemplate.opsForValue().increment(key);
        if (attempts != null && attempts == 1L) {
            redisTemplate.expire(key, VERIFY_ATTEMPT_WINDOW);
        }
        if (attempts != null && attempts > MAX_VERIFY_ATTEMPTS) {
            throw new BusinessException(ErrorCode.TOO_MANY_VERIFICATION_ATTEMPTS);
        }
    }

    /**
     * 이메일이 인증되었는지 확인합니다
     */
    @Transactional(readOnly = true)
    public boolean isEmailVerified(String email) {
        return emailVerificationRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .map(EmailVerification::isVerified)
                .orElse(false);
    }

    /**
     * 토큰으로 이메일 주소를 가져옵니다
     */
    @Transactional(readOnly = true)
    public String getEmailFromToken(String token) {
        EmailVerification emailVerification = emailVerificationRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_VERIFICATION_TOKEN));
        return emailVerification.getEmail();
    }

    /**
     * 이메일을 발송합니다
     */
    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }

    /**
     * 6자리 숫자 인증번호를 생성합니다
     */
    private String generateVerificationToken() {
        return String.valueOf(SECURE_RANDOM.nextInt(900000) + 100000);
    }

    /**
     * 이메일 인증 메일 내용을 생성합니다
     */
    private String buildVerificationEmailContent(String token) {
        return """
                <!DOCTYPE html>
                <html>
                    <head><meta charset="UTF-8"></head>
                    <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                        <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #eee; border-radius: 8px;">
                            <h2 style="color: #6641DF; text-align: center;">NewBranch 이메일 인증</h2>
                            <p>안녕하세요, NewBranch에 가입해 주셔서 감사합니다.</p>
                            <p>화면의 인증번호 입력란에 아래 6자리 코드를 입력해주세요.</p>
                            <div style="background-color: #f3efff; padding: 20px; text-align: center; margin: 20px 0; border-radius: 4px;">
                                <span style="font-size: 32px; font-weight: bold; color: #6641DF; letter-spacing: 5px;">%s</span>
                        </div>
                         <p style="color: #999; font-size: 13px;">본 인증번호는 3분 동안 유효합니다.</p>
                    </div>
                </body>
                </html>
                """.formatted(token);
    }
}