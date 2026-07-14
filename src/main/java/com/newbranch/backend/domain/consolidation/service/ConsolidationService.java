package com.newbranch.backend.domain.consolidation.service;

import com.newbranch.backend.domain.attachment.dto.AttachmentResponse;
import com.newbranch.backend.domain.attachment.entity.Attachment;
import com.newbranch.backend.domain.attachment.repository.AttachmentRepository;
import com.newbranch.backend.domain.consolidation.dto.ConsolidationRequest;
import com.newbranch.backend.domain.consolidation.dto.ConsolidationResponse;
import com.newbranch.backend.domain.consolidation.entity.Consolidation;
import com.newbranch.backend.domain.consolidation.entity.ConsolidationAttachment;
import com.newbranch.backend.domain.consolidation.entity.ConsolidationMaterial;
import com.newbranch.backend.domain.consolidation.repository.ConsolidationAttachmentRepository;
import com.newbranch.backend.domain.consolidation.repository.ConsolidationMaterialRepository;
import com.newbranch.backend.domain.consolidation.repository.ConsolidationRepository;
import com.newbranch.backend.domain.goal.entity.LearningGoal;
import com.newbranch.backend.domain.goal.service.LearningGoalService;
import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.domain.user.service.UserQueryService;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConsolidationService {

    private final ConsolidationRepository consolidationRepository;
    private final ConsolidationMaterialRepository consolidationMaterialRepository;
    private final ConsolidationAttachmentRepository consolidationAttachmentRepository;
    private final AttachmentRepository attachmentRepository;
    private final UserQueryService userQueryService;
    private final LearningGoalService learningGoalService;

    /**
     * 사용자의 활성 단권화 목록을 최신순으로 조회합니다.
     */
    public List<ConsolidationResponse> getConsolidations(String email) {
        User user = userQueryService.findByEmail(email);
        return consolidationRepository.findAllByUserAndIsActiveTrueOrderByCreatedAtDesc(user)
                .stream()
                .map(ConsolidationResponse::from)
                .toList();
    }

    /**
     * 단권화 상세 정보를 조회합니다 (하위 자료, 매핑된 첨부파일 포함).
     */
    public ConsolidationResponse getConsolidation(String email, Long coId) {
        User user = userQueryService.findByEmail(email);
        Consolidation consolidation = findByIdAndUser(coId, user);

        List<ConsolidationMaterial> materials = fetchMaterials(consolidation);
        List<AttachmentResponse> attachments = fetchAttachments(consolidation);

        return ConsolidationResponse.fromDetail(consolidation, materials, attachments);
    }

    /**
     * 단권화 메인 정보, 첨부파일 매핑, 하위 자료를 하나의 트랜잭션으로 등록합니다.
     * 첨부파일은 학습 목표 등록 시 이미 업로드된 자료 중에서 선택된 것을 매핑합니다.
     */
    @Transactional
    public ConsolidationResponse createConsolidation(String email, ConsolidationRequest request) {
        User user = userQueryService.findByEmail(email);
        LearningGoal goal = learningGoalService.findGoalByIdAndUser(request.getLgId(), user);
        List<Attachment> attachments = validateAndFetchAttachments(request.getAttachmentIds(), user);

        Consolidation consolidation = consolidationRepository.save(Consolidation.builder()
                .user(user)
                .learningGoal(goal)
                .title(request.getTitle())
                .article(request.getArticle())
                .mode(request.getMode())
                .duplicateHandling(request.getDuplicateHandling())
                .importanceLevel(request.getImportanceLevel())
                .summaryNote(request.getSummaryNote())
                .build());

        saveAttachmentMappings(consolidation, attachments);
        saveMaterials(consolidation, request.getMaterials());

        List<ConsolidationMaterial> materials = fetchMaterials(consolidation);
        List<AttachmentResponse> attachmentResponses = attachments.stream()
                .map(AttachmentResponse::from).toList();
        return ConsolidationResponse.fromDetail(consolidation, materials, attachmentResponses);
    }

    /**
     * 단권화 텍스트 정보 및 하위 자료를 수정합니다. (첨부파일 매핑은 등록 시점 정보를 유지)
     */
    @Transactional
    public ConsolidationResponse updateConsolidation(String email, Long coId, ConsolidationRequest request) {
        User user = userQueryService.findByEmail(email);
        Consolidation consolidation = findByIdAndUser(coId, user);

        consolidation.update(request.getTitle(), request.getArticle(), request.getMode(),
                request.getDuplicateHandling(), request.getImportanceLevel(), request.getSummaryNote());

        if (request.getMaterials() != null) {
            consolidationMaterialRepository.deleteAllByConsolidation(consolidation);
            saveMaterials(consolidation, request.getMaterials());
        }

        List<ConsolidationMaterial> materials = fetchMaterials(consolidation);
        List<AttachmentResponse> attachments = fetchAttachments(consolidation);
        return ConsolidationResponse.fromDetail(consolidation, materials, attachments);
    }

    /**
     * 단권화를 소프트 삭제합니다.
     */
    @Transactional
    public void deleteConsolidation(String email, Long coId) {
        User user = userQueryService.findByEmail(email);
        findByIdAndUser(coId, user).deactivate();
    }

    // ── private 헬퍼 ─────────────────────────────────────────────────────────

    private Consolidation findByIdAndUser(Long coId, User user) {
        Consolidation consolidation = consolidationRepository.findByIdAndIsActiveTrue(coId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSOLIDATION_NOT_FOUND));
        if (!consolidation.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        return consolidation;
    }

    private List<Attachment> validateAndFetchAttachments(List<Long> ids, User user) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Attachment> attachments = attachmentRepository.findAllByIdInAndIsActiveTrue(ids);
        if (attachments.size() != ids.size()) {
            throw new BusinessException(ErrorCode.ATTACHMENT_NOT_FOUND);
        }
        if (attachments.stream().anyMatch(a -> !a.getUser().getId().equals(user.getId()))) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        return attachments;
    }

    private void saveAttachmentMappings(Consolidation consolidation, List<Attachment> attachments) {
        attachments.forEach(attachment -> consolidationAttachmentRepository.save(
                new ConsolidationAttachment(consolidation, attachment)));
    }

    private void saveMaterials(Consolidation consolidation, List<ConsolidationRequest.MaterialEntry> materials) {
        if (materials == null) return;
        for (int i = 0; i < materials.size(); i++) {
            ConsolidationRequest.MaterialEntry entry = materials.get(i);
            Integer sortOrder = entry.getSortOrder() != null ? entry.getSortOrder() : i;
            consolidationMaterialRepository.save(ConsolidationMaterial.builder()
                    .consolidation(consolidation)
                    .field(entry.getField())
                    .sortOrder(sortOrder)
                    .build());
        }
    }

    private List<ConsolidationMaterial> fetchMaterials(Consolidation consolidation) {
        return consolidationMaterialRepository.findAllByConsolidationAndIsActiveTrueOrderBySortOrderAsc(consolidation);
    }

    private List<AttachmentResponse> fetchAttachments(Consolidation consolidation) {
        return consolidationAttachmentRepository.findAllByConsolidationAndIsActiveTrue(consolidation)
                .stream()
                .map(ca -> AttachmentResponse.from(ca.getAttachment()))
                .toList();
    }
}
