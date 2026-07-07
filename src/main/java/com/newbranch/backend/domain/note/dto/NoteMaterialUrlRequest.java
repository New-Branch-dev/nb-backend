package com.newbranch.backend.domain.note.dto;

import com.newbranch.backend.domain.note.entity.MaterialType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoteMaterialUrlRequest {

    @NotBlank(message = "URL은 필수입니다")
    private String url;

    @NotNull(message = "타입은 필수입니다 (VIDEO 또는 WEB)")
    private MaterialType type;
}
