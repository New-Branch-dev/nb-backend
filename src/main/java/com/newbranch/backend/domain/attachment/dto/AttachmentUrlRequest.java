package com.newbranch.backend.domain.attachment.dto;

import com.newbranch.backend.domain.attachment.entity.AttachmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class AttachmentUrlRequest {

    @NotNull
    private AttachmentType type;

    @NotBlank
    @Pattern(regexp = "^https?://.+", message = "올바른 URL 형식이 아닙니다")
    private String url;

    private String title;
}
