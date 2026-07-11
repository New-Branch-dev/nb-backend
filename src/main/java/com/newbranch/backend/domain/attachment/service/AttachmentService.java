package com.newbranch.backend.domain.attachment.service;

import com.newbranch.backend.domain.attachment.dto.AttachmentResponse;
import com.newbranch.backend.domain.attachment.dto.AttachmentTextRequest;
import com.newbranch.backend.domain.attachment.dto.AttachmentUrlRequest;
import com.newbranch.backend.domain.attachment.entity.Attachment;
import com.newbranch.backend.domain.attachment.entity.AttachmentType;
import com.newbranch.backend.domain.attachment.repository.AttachmentRepository;
import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.domain.user.service.UserQueryService;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import com.newbranch.backend.infra.extraction.TextExtractionService;
import com.newbranch.backend.infra.openai.OpenAiClient;
import com.newbranch.backend.infra.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final UserQueryService userQueryService;
    private final S3Uploader s3Uploader;
    private final TextExtractionService textExtractionService;
    private final OpenAiClient openAiClient;

    /**
     * 사용자의 활성 첨부파일 목록을 최신순으로 조회합니다.
     */
    public List<AttachmentResponse> getAttachments(String email) {
        User user = userQueryService.findByEmail(email);
        return attachmentRepository.findAllByUserAndIsActiveTrueOrderByCreatedAtDesc(user)
                .stream().map(AttachmentResponse::from).collect(Collectors.toList());
    }

    /**
     * 첨부파일 상세 정보를 조회합니다.
     */
    public AttachmentResponse getAttachment(String email, Long attachmentId) {
        User user = userQueryService.findByEmail(email);
        return AttachmentResponse.from(findByIdAndUser(attachmentId, user));
    }

    /**
     * PDF, PPT, Word 파일을 업로드하고 텍스트를 추출합니다.
     */
    @Transactional
    public List<AttachmentResponse> uploadFiles(String email, List<MultipartFile> files) {
        User user = userQueryService.findByEmail(email);
        return files.stream().map(file -> {
            String extractedText = textExtractionService.extract(file);
            String fileUrl = s3Uploader.upload(file, "attachments/files", S3Uploader.FILE_CONTENT_TYPES);
            Attachment attachment = Attachment.builder()
                    .user(user)
                    .attachmentType(AttachmentType.FILE)
                    .fileName(file.getOriginalFilename())
                    .fileUrl(fileUrl)
                    .extractedText(extractedText)
                    .build();
            return AttachmentResponse.from(attachmentRepository.save(attachment));
        }).collect(Collectors.toList());
    }

    /**
     * 강의 녹음 파일을 업로드하고 Whisper로 텍스트를 추출합니다.
     */
    @Transactional
    public AttachmentResponse uploadLecture(String email, MultipartFile file) {
        User user = userQueryService.findByEmail(email);
        String extractedText = openAiClient.transcribeAudio(file);
        String fileUrl = s3Uploader.upload(file, "attachments/lectures", S3Uploader.LECTURE_CONTENT_TYPES);
        Attachment attachment = Attachment.builder()
                .user(user)
                .attachmentType(AttachmentType.LECTURE)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .extractedText(extractedText)
                .build();
        return AttachmentResponse.from(attachmentRepository.save(attachment));
    }

    /**
     * YouTube 또는 웹 URL을 저장합니다.
     */
    @Transactional
    public AttachmentResponse saveUrl(String email, AttachmentUrlRequest request) {
        User user = userQueryService.findByEmail(email);
        Attachment attachment = Attachment.builder()
                .user(user)
                .attachmentType(request.getType())
                .title(request.getTitle())
                .fileUrl(request.getUrl())
                .build();
        return AttachmentResponse.from(attachmentRepository.save(attachment));
    }

    /**
     * 직접 입력한 텍스트를 저장합니다.
     */
    @Transactional
    public AttachmentResponse saveText(String email, AttachmentTextRequest request) {
        User user = userQueryService.findByEmail(email);
        Attachment attachment = Attachment.builder()
                .user(user)
                .attachmentType(AttachmentType.TEXT)
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        return AttachmentResponse.from(attachmentRepository.save(attachment));
    }

    /**
     * 첨부파일을 소프트 삭제합니다. FILE/LECTURE 타입은 S3에서도 삭제합니다.
     */
    @Transactional
    public void deleteAttachment(String email, Long attachmentId) {
        User user = userQueryService.findByEmail(email);
        Attachment attachment = findByIdAndUser(attachmentId, user);
        if (attachment.getAttachmentType() == AttachmentType.FILE
                || attachment.getAttachmentType() == AttachmentType.LECTURE) {
            s3Uploader.delete(attachment.getFileUrl());
        }
        attachment.deactivate();
    }

    private Attachment findByIdAndUser(Long attachmentId, User user) {
        Attachment attachment = attachmentRepository.findByIdAndIsActiveTrue(attachmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ATTACHMENT_NOT_FOUND));
        if (!attachment.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        return attachment;
    }
}
