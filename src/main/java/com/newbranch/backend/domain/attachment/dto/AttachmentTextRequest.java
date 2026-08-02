package com.newbranch.backend.domain.attachment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AttachmentTextRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;
}
