package com.newbranch.backend.domain.attachment.dto;

import com.newbranch.backend.domain.attachment.entity.AttachmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class AttachmentUrlRequest {

    @NotNull
    private AttachmentType type;

    @NotBlank
    private String url;

    private String title;
}
