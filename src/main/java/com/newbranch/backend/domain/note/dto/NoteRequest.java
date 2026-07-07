package com.newbranch.backend.domain.note.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoteRequest {

    @NotBlank(message = "제목은 필수입니다")
    private String title;

    private String content;

    private String sourceType;
}
