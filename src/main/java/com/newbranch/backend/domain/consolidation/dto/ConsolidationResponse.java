package com.newbranch.backend.domain.consolidation.dto;

import com.newbranch.backend.domain.attachment.dto.AttachmentResponse;
import com.newbranch.backend.domain.consolidation.entity.Consolidation;
import com.newbranch.backend.domain.consolidation.entity.ConsolidationMaterial;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@Schema(description = "단권화 응답")
public class ConsolidationResponse {

    private Long coId;
    private Long lgId;
    private String title;
    private String article;
    private String mode;
    private String duplicateHandling;
    private String importanceLevel;
    private String summaryNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private List<MaterialResponse> materials;
    private List<AttachmentResponse> attachments;

    /**
     * 목록 조회용 응답 생성 (하위 자료, 첨부파일 제외)
     */
    public static ConsolidationResponse from(Consolidation consolidation) {
        return ConsolidationResponse.builder()
                .coId(consolidation.getId())
                .lgId(consolidation.getLearningGoal().getId())
                .title(consolidation.getTitle())
                .article(consolidation.getArticle())
                .mode(consolidation.getMode())
                .duplicateHandling(consolidation.getDuplicateHandling())
                .importanceLevel(consolidation.getImportanceLevel())
                .summaryNote(consolidation.getSummaryNote())
                .createdAt(consolidation.getCreatedAt())
                .updatedAt(consolidation.getUpdatedAt())
                .build();
    }

    /**
     * 상세 조회용 응답 생성 (하위 자료, 매핑된 첨부파일 포함)
     */
    public static ConsolidationResponse fromDetail(Consolidation consolidation,
                                                     List<ConsolidationMaterial> materials,
                                                     List<AttachmentResponse> attachments) {
        return from(consolidation).toBuilder()
                .materials(materials.stream().map(MaterialResponse::from).toList())
                .attachments(attachments)
                .build();
    }

    @Getter
    @Builder
    @Schema(description = "단권화 하위 자료")
    public static class MaterialResponse {
        private Long cmId;
        private String field;
        private Integer sortOrder;

        public static MaterialResponse from(ConsolidationMaterial material) {
            return MaterialResponse.builder()
                    .cmId(material.getId())
                    .field(material.getField())
                    .sortOrder(material.getSortOrder())
                    .build();
        }
    }
}
