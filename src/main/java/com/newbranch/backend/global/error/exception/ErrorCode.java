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

    // User
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "User not found"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "U002", "User already exists"),

    // Goal
    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "G001", "Learning goal not found"),
    STUDY_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "G002", "Study plan not found"),
    RETRIEVAL_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "G003", "Retrieval plan not found"),
    MEMORIZATION_PLAN_NOT_FOUND(HttpStatus.NOT_FOUND, "G004", "Memorization plan not found"),

    // Note
    NOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "N001", "Note not found"),
    NOTE_MATERIAL_NOT_FOUND(HttpStatus.NOT_FOUND, "N002", "Note material not found"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "N003", "Invalid file type. Allowed: PDF, PPT, Word, Audio"),
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "N004", "File upload failed"),

    // Authentication & Authorization
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "Authentication is required"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "Invalid or expired token"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A003", "Refresh token not found"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A004", "Refresh token expired"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A005", "Invalid email or password"),

    // Email Verification
    INVALID_VERIFICATION_TOKEN(HttpStatus.BAD_REQUEST, "E001", "Invalid verification token"),
    EXPIRED_VERIFICATION_TOKEN(HttpStatus.BAD_REQUEST, "E002", "Verification token expired"),
    ALREADY_VERIFIED_EMAIL(HttpStatus.BAD_REQUEST, "E003", "Email already verified"),
    EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "E004", "Email not verified"),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "E005", "Failed to send email");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
