package com.newbranch.backend.domain.user.service;

import com.newbranch.backend.domain.user.entity.EmailVerification;
import com.newbranch.backend.domain.user.repository.EmailVerificationRepository;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 이메일 발송 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;

    @Value("${email.from}")
    private String fromEmail;

    @Value("${email.verification.base-url}")
    private String baseUrl;

    @Value("${email.verification.expiration}")
    private Long verificationExpiration;

    /**
     * 이메일 인증 메일을 발송합니다
     *//*
    @Async
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

        String verificationUrl = baseUrl + "/api/v1/auth/verify-email?token=" + token;
        String subject = "[NewBranch] 이메일 인증을 완료해주세요";
        String content = buildVerificationEmailContent(verificationUrl);

        sendEmail(email, subject, content);
        log.info("Verification email sent to: {}", email);
    }*/

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

   /* *//**
     * 이메일을 발송합니다
     *//*
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
    }*/
/*
    *//**
     * 이메일 인증 토큰을 생성합니다
     *//*
    private String generateVerificationToken() {
        return UUID.randomUUID().toString();
    }

    *//**
     * 이메일 인증 메일 내용을 생성합니다
     *//*
    private String buildVerificationEmailContent(String verificationUrl) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
                        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; }
                        .content { padding: 20px; background-color: #f9f9f9; }
                        .button { display: inline-block; padding: 12px 24px; background-color: #4CAF50; color: white; text-decoration: none; border-radius: 4px; margin: 20px 0; }
                        .footer { text-align: center; padding: 20px; color: #777; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>NewBranch 이메일 인증</h1>
                        </div>
                        <div class="content">
                            <p>안녕하세요,</p>
                            <p>NewBranch에 가입해 주셔서 감사합니다.</p>
                            <p>아래 버튼을 클릭하여 이메일 인증을 완료해주세요.</p>
                            <div style="text-align: center;">
                                <a href="%s" class="button">이메일 인증하기</a>
                            </div>
                            <p>또는 아래 링크를 복사하여 브라우저에 붙여넣으세요:</p>
                            <p style="word-break: break-all; color: #666;">%s</p>
                            <p style="color: #999; font-size: 14px;">이 링크는 24시간 동안 유효합니다.</p>
                        </div>
                        <div class="footer">
                            <p>본 메일은 발신 전용입니다.</p>
                            <p>&copy; 2024 NewBranch. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(verificationUrl, verificationUrl);
    }*/

    /**
     * 6자리 인증번호 메일을 발송합니다
     */
    @Async
    @Transactional
    public void sendVerificationEmail(String email) {
        String token = generateVerificationToken(); // 6자리 숫자로 변경됨
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
     * 프론트엔드 화면에서 입력한 이메일과 인증번호를 실시간 검증합니다
     */
    @Transactional
    public void verifyEmailCode(String email, String token) {
        EmailVerification emailVerification = emailVerificationRepository.findTopByEmailOrderByCreatedAtDesc(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_VERIFICATION_TOKEN));

        if (!emailVerification.getToken().equals(token)) {
            throw new BusinessException(ErrorCode.INVALID_VERIFICATION_TOKEN); // 인증번호 불일치
        }

        if (emailVerification.isVerified()) {
            throw new BusinessException(ErrorCode.ALREADY_VERIFIED_EMAIL);
        }

        if (emailVerification.isExpired()) {
            throw new BusinessException(ErrorCode.EXPIRED_VERIFICATION_TOKEN);
        }

        emailVerification.verify();
        log.info("Email successfully verified via code: {}", email);
    }

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
     * 6자리 숫자 인증번호 생성
     */
    private String generateVerificationToken() {
        return String.valueOf((int)(Math.random() * 899999) + 100000);
    }

    /**
     * HTML 메일 템플릿
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