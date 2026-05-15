package com.newbranch.backend.domain.note.service;

import com.newbranch.backend.domain.note.dto.NoteMaterialResponse;
import com.newbranch.backend.domain.note.dto.NoteMaterialTextRequest;
import com.newbranch.backend.domain.note.dto.NoteMaterialUrlRequest;
import com.newbranch.backend.domain.note.entity.MaterialType;
import com.newbranch.backend.domain.note.entity.Note;
import com.newbranch.backend.domain.note.entity.NoteMaterial;
import com.newbranch.backend.domain.note.repository.NoteMaterialRepository;
import com.newbranch.backend.domain.note.repository.NoteRepository;
import com.newbranch.backend.domain.user.entity.User;
import com.newbranch.backend.domain.user.repository.UserRepository;
import com.newbranch.backend.global.error.exception.BusinessException;
import com.newbranch.backend.global.error.exception.ErrorCode;
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
public class NoteMaterialService {

    private final NoteMaterialRepository noteMaterialRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;
    private final S3Uploader s3Uploader;

    // 파일 업로드 (PDF, PPT, Word) - 여러 개 가능
    @Transactional
    public List<NoteMaterialResponse> uploadFiles(String email, Long noteId, List<MultipartFile> files) {
        User user = findUserByEmail(email);
        Note note = findNoteByIdAndUser(noteId, user);

        return files.stream().map(file -> {
            String fileUrl = s3Uploader.upload(file, "notes/files", S3Uploader.FILE_CONTENT_TYPES);
            NoteMaterial material = NoteMaterial.builder()
                    .user(user)
                    .note(note)
                    .materialType(MaterialType.FILE)
                    .fileName(file.getOriginalFilename())
                    .fileUrl(fileUrl)
                    .build();
            return NoteMaterialResponse.from(noteMaterialRepository.save(material));
        }).collect(Collectors.toList());
    }

    // 강의 녹음 업로드 (오디오 파일)
    @Transactional
    public NoteMaterialResponse uploadLecture(String email, Long noteId, MultipartFile file) {
        User user = findUserByEmail(email);
        Note note = findNoteByIdAndUser(noteId, user);

        String fileUrl = s3Uploader.upload(file, "notes/lectures", S3Uploader.LECTURE_CONTENT_TYPES);
        NoteMaterial material = NoteMaterial.builder()
                .user(user)
                .note(note)
                .materialType(MaterialType.LECTURE)
                .fileName(file.getOriginalFilename())
                .fileUrl(fileUrl)
                .build();
        return NoteMaterialResponse.from(noteMaterialRepository.save(material));
    }

    // 영상(YouTube) 또는 웹 URL 저장
    @Transactional
    public NoteMaterialResponse saveUrl(String email, Long noteId, NoteMaterialUrlRequest request) {
        User user = findUserByEmail(email);
        Note note = findNoteByIdAndUser(noteId, user);

        NoteMaterial material = NoteMaterial.builder()
                .user(user)
                .note(note)
                .materialType(request.getType())
                .fileUrl(request.getUrl())
                .build();
        return NoteMaterialResponse.from(noteMaterialRepository.save(material));
    }

    // 텍스트 저장
    @Transactional
    public NoteMaterialResponse saveText(String email, Long noteId, NoteMaterialTextRequest request) {
        User user = findUserByEmail(email);
        Note note = findNoteByIdAndUser(noteId, user);

        NoteMaterial material = NoteMaterial.builder()
                .user(user)
                .note(note)
                .materialType(MaterialType.TEXT)
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        return NoteMaterialResponse.from(noteMaterialRepository.save(material));
    }

    // 자료 목록 조회
    public List<NoteMaterialResponse> getMaterials(String email, Long noteId) {
        User user = findUserByEmail(email);
        Note note = findNoteByIdAndUser(noteId, user);
        return noteMaterialRepository.findAllByNoteAndIsActiveTrue(note)
                .stream()
                .map(NoteMaterialResponse::from)
                .collect(Collectors.toList());
    }

    // 자료 삭제
    @Transactional
    public void deleteMaterial(String email, Long materialId) {
        User user = findUserByEmail(email);
        NoteMaterial material = noteMaterialRepository.findByIdAndIsActiveTrue(materialId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOTE_MATERIAL_NOT_FOUND));
        if (!material.getUser().getId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        // S3 파일인 경우에만 S3에서도 삭제
        if (material.getMaterialType() == MaterialType.FILE
                || material.getMaterialType() == MaterialType.LECTURE) {
            s3Uploader.delete(material.getFileUrl());
        }
        material.deactivate();
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