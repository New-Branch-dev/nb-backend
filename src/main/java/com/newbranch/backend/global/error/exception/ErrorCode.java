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

    // Authentication & Authorization
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "A001", "Authentication is required"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "Invalid or expired token"),
    REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "A003", "Refresh token not found"),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A004", "Refresh token expired"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "A005", "Invalid email or password");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
