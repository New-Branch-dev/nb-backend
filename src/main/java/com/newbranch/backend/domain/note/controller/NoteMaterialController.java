package com.newbranch.backend.domain.note.controller;

import com.newbranch.backend.domain.note.dto.NoteMaterialResponse;
import com.newbranch.backend.domain.note.dto.NoteMaterialTextRequest;
import com.newbranch.backend.domain.note.dto.NoteMaterialUrlRequest;
import com.newbranch.backend.domain.note.service.NoteMaterialService;
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

@Tag(name = "Note Material", description = "학습 자료 API")
@RestController
@RequestMapping("/api/v1/notes/{noteId}/materials")
@RequiredArgsConstructor
public class NoteMaterialController {

    private final NoteMaterialService noteMaterialService;

    // F-GO-009: 파일 업로드 (PDF, PPT, Word) - 여러 개 가능
    @Operation(summary = "파일 업로드", description = "PDF, PPT, Word 파일을 업로드합니다 (여러 개 가능)")
    @PostMapping(value = "/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<NoteMaterialResponse>> uploadFiles(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId,
            @RequestPart("files") List<MultipartFile> files) {
        return ApiResponse.success(noteMaterialService.uploadFiles(userDetails.getUsername(), noteId, files));
    }

    // F-GO-009: 강의 녹음 업로드 (오디오)
    @Operation(summary = "강의 녹음 업로드", description = "녹음된 강의 오디오 파일을 업로드합니다")
    @PostMapping(value = "/lecture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<NoteMaterialResponse> uploadLecture(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId,
            @RequestPart("file") MultipartFile file) {
        return ApiResponse.success(noteMaterialService.uploadLecture(userDetails.getUsername(), noteId, file));
    }

    // F-GO-009: 영상(YouTube) 또는 웹 URL 저장
    @Operation(summary = "URL 저장", description = "유튜브 영상 또는 웹 페이지 URL을 저장합니다 (type: VIDEO 또는 WEB)")
    @PostMapping("/url")
    public ApiResponse<NoteMaterialResponse> saveUrl(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId,
            @Valid @RequestBody NoteMaterialUrlRequest request) {
        return ApiResponse.success(noteMaterialService.saveUrl(userDetails.getUsername(), noteId, request));
    }

    // F-GO-009: 텍스트 저장
    @Operation(summary = "텍스트 저장", description = "직접 입력한 텍스트를 저장합니다")
    @PostMapping("/text")
    public ApiResponse<NoteMaterialResponse> saveText(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId,
            @Valid @RequestBody NoteMaterialTextRequest request) {
        return ApiResponse.success(noteMaterialService.saveText(userDetails.getUsername(), noteId, request));
    }

    // F-GO-009: 자료 목록 조회
    @Operation(summary = "자료 목록 조회", description = "노트에 등록된 학습 자료 목록을 조회합니다")
    @GetMapping
    public ApiResponse<List<NoteMaterialResponse>> getMaterials(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId) {
        return ApiResponse.success(noteMaterialService.getMaterials(userDetails.getUsername(), noteId));
    }

    // F-GO-009: 자료 삭제
    @Operation(summary = "자료 삭제", description = "학습 자료를 삭제합니다")
    @DeleteMapping("/{materialId}")
    public ApiResponse<Void> deleteMaterial(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId,
            @PathVariable Long materialId) {
        noteMaterialService.deleteMaterial(userDetails.getUsername(), materialId);
        return ApiResponse.success();
    }
}