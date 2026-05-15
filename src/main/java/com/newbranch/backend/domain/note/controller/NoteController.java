package com.newbranch.backend.domain.note.controller;

import com.newbranch.backend.domain.note.dto.NoteRequest;
import com.newbranch.backend.domain.note.dto.NoteResponse;
import com.newbranch.backend.domain.note.service.NoteService;
import com.newbranch.backend.global.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Note", description = "노트 API")
@RestController
@RequestMapping("/api/v1/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    // F-GO-007: 노트 목록 조회
    @Operation(summary = "노트 목록 조회")
    @GetMapping
    public ApiResponse<List<NoteResponse>> getNotes(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(noteService.getNotes(userDetails.getUsername()));
    }

    // F-GO-009: 노트 생성 (DRAFT) - "+ 노트 추가" 클릭 시 호출, noteId 반환
    @Operation(summary = "노트 생성", description = "DRAFT 상태로 노트를 생성하고 noteId를 반환합니다")
    @PostMapping
    public ApiResponse<NoteResponse> createNote(@AuthenticationPrincipal UserDetails userDetails) {
        return ApiResponse.success(noteService.createNote(userDetails.getUsername()));
    }

    // F-GO-008: 노트 상세 조회
    @Operation(summary = "노트 상세 조회")
    @GetMapping("/{noteId}")
    public ApiResponse<NoteResponse> getNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId) {
        return ApiResponse.success(noteService.getNote(userDetails.getUsername(), noteId));
    }

    // F-GO-010: 노트 수정 + 최종 저장 (ACTIVE 전환)
    @Operation(summary = "노트 저장", description = "노트 내용을 저장하고 ACTIVE 상태로 전환합니다")
    @PutMapping("/{noteId}")
    public ApiResponse<NoteResponse> updateNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId,
            @Valid @RequestBody NoteRequest request) {
        return ApiResponse.success(noteService.updateNote(userDetails.getUsername(), noteId, request));
    }

    // F-GO-011: 노트 삭제
    @Operation(summary = "노트 삭제")
    @DeleteMapping("/{noteId}")
    public ApiResponse<Void> deleteNote(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long noteId) {
        noteService.deleteNote(userDetails.getUsername(), noteId);
        return ApiResponse.success();
    }
}