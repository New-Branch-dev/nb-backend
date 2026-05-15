package com.newbranch.backend.domain.note.entity;

public enum MaterialType {
    LECTURE,  // 강의 녹음 (오디오 파일 → S3)
    FILE,     // 파일 업로드 (PDF, PPT, Word → S3)
    VIDEO,    // 유튜브 링크 URL
    WEB,      // 웹 페이지 URL
    TEXT      // 직접 입력 텍스트
}