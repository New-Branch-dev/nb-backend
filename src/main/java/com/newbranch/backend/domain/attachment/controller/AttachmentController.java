package com.newbranch.backend.domain.attachment.controller;

import com.newbranch.backend.domain.attachment.dto.AttachmentResponse;
import com.newbranch.backend.domain.attachment.dto.AttachmentTextRequest;
import com.newbranch.backend.domain.attachment.dto.AttachmentUrlRequest;
import com.newbranch.backend.domain.attachment.service.AttachmentService;
import com.newbranch.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Attachment", description = "첨부파일 API")
@RestController
@RequestMapping("/api/v1/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentService attachmentService;

    @Operation(summary = "첨부파일 목록 조회")
    @GetMapping
    public ApiResponse<List<AttachmentResponse>> getAttachments(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(attachmentService.getAttachments(userDetails.getUsername()));
    }

    @Operation(summary = "첨부파일 상세 조회")
    @GetMapping("/{attachmentId}")
    public ApiResponse<AttachmentResponse> getAttachment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long attachmentId) {
        return ApiResponse.success(attachmentService.getAttachment(userDetails.getUsername(), attachmentId));
    }

    @Operation(summary = "파일 업로드 (PDF, PPT, Word)")
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<AttachmentResponse>> uploadFiles(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("files") List<MultipartFile> files) {
        return ApiResponse.success(attachmentService.uploadFiles(userDetails.getUsername(), files));
    }

    @Operation(summary = "강의 녹음 업로드")
    @PostMapping(value = "/lecture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AttachmentResponse> uploadLecture(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("file") MultipartFile file) {
        return ApiResponse.success(attachmentService.uploadLecture(userDetails.getUsername(), file));
    }

    @Operation(summary = "URL 저장 (VIDEO, WEB)")
    @PostMapping("/url")
    public ApiResponse<AttachmentResponse> saveUrl(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AttachmentUrlRequest request) {
        return ApiResponse.success(attachmentService.saveUrl(userDetails.getUsername(), request));
    }

    @Operation(summary = "텍스트 저장")
    @PostMapping("/text")
    public ApiResponse<AttachmentResponse> saveText(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody AttachmentTextRequest request) {
        return ApiResponse.success(attachmentService.saveText(userDetails.getUsername(), request));
    }

    @Operation(summary = "첨부파일 삭제")
    @DeleteMapping("/{attachmentId}")
    public ApiResponse<Void> deleteAttachment(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long attachmentId) {
        attachmentService.deleteAttachment(userDetails.getUsername(), attachmentId);
        return ApiResponse.success();
    }
}
