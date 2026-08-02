package com.newbranch.backend.global.error.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C001", "Internal server error"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C002", "Invalid input value"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "Method not allowed"),
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C004", "Entity not found"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "Invalid type value"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "Access denied"),
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "C007", "Duplicate resource"),

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User not found"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "User already exists"),

    // MyLearning
    MY_LEARNING_ALREADY_EXISTS(HttpStatus.CONFLICT, "M001", "My learning data already registered"),
    USER_SETTING_NOT_FOUND(HttpStatus.NOT_FOUND, "M002", "User setting not found"),
    USER_LEARNING_STYLE_NOT_FOUND(HttpStatus.NOT_FOUND, "M003", "User learning style not found"),

    // Goal
    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "G001", "Learning goal not found"),

    // Consolidation
    CONSOLIDATION_NOT_FOUND(HttpStatus.NOT_FOUND, "CO001", "Consolidation not found"),

    // Attachment
    ATTACHMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "AT001", "Attachment not found"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "AT002", "Invalid file type. Allowed: PDF, PPT, Word, Audio"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AT003", "File upload failed"),

    // Authentication & Authorization
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "Authentication is required"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "Invalid or expired token"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A003", "Refresh token not found"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A004", "Refresh token expired"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A005", "Invalid email or password"),
    INVALID_CURRENT_PASSWORD(HttpStatus.BAD_REQUEST, "A006", "Current password is incorrect"),

    // AI
    AI_SERVICE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AI001", "AI service error"),
    AI_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "AI002", "AI study plan not found"),

    // Email Verification
    INVALID_VERIFICATION_TOKEN(HttpStatus.BAD_REQUEST, "E001", "Invalid verification token"),
    EXPIRED_VERIFICATION_TOKEN(HttpStatus.BAD_REQUEST, "E002", "Verification token expired"),
    ALREADY_VERIFIED_EMAIL(HttpStatus.BAD_REQUEST, "E003", "Email already verified"),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "E004", "Email not verified"),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E005", "Failed to send email"),
    TOO_MANY_VERIFICATION_ATTEMPTS(HttpStatus.TOO_MANY_REQUESTS, "E006", "Too many verification attempts");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
