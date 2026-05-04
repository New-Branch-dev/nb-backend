package com.newbranch.backend.domain.note.repository;

import com.newbranch.backend.domain.note.entity.Note;
import com.newbranch.backend.domain.note.entity.NoteMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * NoteMaterial 엔티티에 대한 Repository
 */
@Repository
public interface NoteMaterialRepository extends JpaRepository<NoteMaterial, Long> {

    /**
     * 노트에 속한 활성화된 자료 목록을 조회합니다
     */
    List<NoteMaterial> findAllByNoteAndIsActiveTrue(Note note);

    /**
     * ID로 활성화된 자료를 조회합니다
     */
    Optional<NoteMaterial> findByIdAndIsActiveTrue(Long id);
}
