package com.newbranch.backend.global.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API Response wrapper")
public class ApiResponse<T> {

    @Schema(description = "Response status", example = "success")
    private final String status;

    @Schema(description = "Response data")
    private final T data;

    @Schema(description = "Error code", example = "U001")
    private final String code;

    @Schema(description = "Error message", example = "User not found")
    private final String message;

    private ApiResponse(String status, T data, String code, String message) {
        this.status = status;
        this.data = data;
        this.code = code;
        this.message = message;
    }

    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>("success", null, null, null);
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data, null, null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>("error", null, code, message);
    }
}
