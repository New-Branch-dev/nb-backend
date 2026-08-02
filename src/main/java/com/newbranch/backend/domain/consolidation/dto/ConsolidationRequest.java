package com.newbranch.backend.domain.consolidation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
@Schema(description = "단권화 등록/수정 요청")
public class ConsolidationRequest {

    @NotNull(message = "학습 목표 ID는 필수입니다")
    @Schema(description = "학습 목표 ID", example = "1")
    private Long lgId;

    @NotBlank(message = "제목은 필수입니다")
    @Schema(description = "제목", example = "1차 중간고사 영어 단권화")
    private String title;

    @Schema(description = "단권화 본문")
    private String article;

    @Schema(description = "생성 모드", example = "AI_AUTO")
    private String mode;

    @Schema(description = "중복 처리 방식", example = "SUMMARIZE")
    private String duplicateHandling;

    @Schema(description = "중요도", example = "HIGH")
    private String importanceLevel;

    @Schema(description = "요약 노트")
    private String summaryNote;

    @Schema(description = "매핑할 첨부파일 ID 목록 (등록 시 사용, 이미 업로드된 첨부파일 중 선택)")
    private List<Long> attachmentIds;

    @Valid
    @Schema(description = "단권화 하위 자료 목록")
    private List<MaterialEntry> materials;

    @Getter
    @Schema(description = "단권화 하위 자료 항목")
    public static class MaterialEntry {
        @NotBlank(message = "자료 내용은 필수입니다")
        @Schema(description = "자료 내용")
        private String field;

        @Schema(description = "정렬 순서", example = "0")
        private Integer sortOrder;
    }
}
