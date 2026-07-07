package com.newbranch.backend.domain.note.service;

import com.newbranch.backend.domain.note.dto.NoteRequest;
import com.newbranch.backend.domain.note.dto.NoteResponse;
import com.newbranch.backend.domain.note.entity.Note;
import com.newbranch.backend.domain.note.entity.NoteStatus;
import com.newbranch.backend.domain.note.repository.NoteRepository;
import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.domain.user.repository.UserRepository;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    // F-GO-007: 노트 목록 조회 (ACTIVE 상태만)
    public List<NoteResponse> getNotes(String email) {
        User user = findUserByEmail(email);
        return noteRepository.findAllByUserAndIsActiveTrueAndStatus(user, NoteStatus.ACTIVE)
                .stream()
                .map(NoteResponse::from)
                .collect(Collectors.toList());
    }

    // F-GO-009: 노트 생성 - DRAFT 상태로 생성 후 noteId 반환
    @Transactional
    public NoteResponse createNote(String email) {
        User user = findUserByEmail(email);
        Note note = Note.builder()
                .user(user)
                .build();
        return NoteResponse.from(noteRepository.save(note));
    }

    // F-GO-008: 노트 상세 조회 (DRAFT 포함)
    public NoteResponse getNote(String email, Long noteId) {
        User user = findUserByEmail(email);
        Note note = findNoteByIdAndUser(noteId, user);
        return NoteResponse.from(note);
    }

    // F-GO-010: 노트 수정 + 최종 저장 시 ACTIVE로 전환
    @Transactional
    public NoteResponse updateNote(String email, Long noteId, NoteRequest request) {
        User user = findUserByEmail(email);
        Note note = findNoteByIdAndUser(noteId, user);
        note.publish(request.getTitle(), request.getContent(), request.getSourceType());
        return NoteResponse.from(note);
    }

    // F-GO-011: 노트 삭제
    @Transactional
    public void deleteNote(String email, Long noteId) {
        User user = findUserByEmail(email);
        Note note = findNoteByIdAndUser(noteId, user);
        note.deactivate();
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    private Note findNoteByIdAndUser(Long noteId, User user) {
        Note note = noteRepository.findByIdAndIsActiveTrue(noteId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTE_NOT_FOUND));
        if (!note.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        return note;
    }
}