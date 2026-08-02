package com.newbranch.backend.domain.mylearning.controller;

import com.newbranch.backend.domain.mylearning.dto.MyLearningRequest;
import com.newbranch.backend.domain.mylearning.service.MyLearningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "My Learning", description = "나만의 학습 API")
@RestController
@RequestMapping("/api/mylearning")
@RequiredArgsConstructor
public class MyLearningController {

    private final MyLearningService myLearningService;

    // 회원 설정, 학습유형 조회
    @Operation(summary = "회원 설정, 학습유형 조회")
    @GetMapping("/{usersId}")
    public ResponseEntity<MyLearningRequest> getMyLearningData(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long usersId) {
        // fix: usersId를 검증 없이 신뢰하던 IDOR을 막기 위해 토큰 주체와 대조하도록 변경 (siwon)
        return ResponseEntity.ok(myLearningService.getMyLearningData(userDetails.getUsername(), usersId));
    }

    // 회원 설정, 학습유형  등록
    @Operation(summary = "회원 설정, 학습유형 생성")
    @PostMapping
    public ResponseEntity<MyLearningRequest> createMyLearningData(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MyLearningRequest dto) {
        // fix: 요청 바디의 usersId 대신 토큰 주체 기준으로 생성하도록 변경 (siwon)
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(myLearningService.createMyLearningData(userDetails.getUsername(), dto));
    }

    // 회원 설정, 학습유형 수정
    @Operation(summary = "회원 설정, 학습유형 수정")
    @PutMapping("/{usersId}")
    public ResponseEntity<MyLearningRequest> updateMyLearningData(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long usersId,
            @Valid @RequestBody MyLearningRequest dto) {
        // fix: usersId를 검증 없이 신뢰하던 IDOR을 막기 위해 토큰 주체와 대조하도록 변경 (siwon)
        return ResponseEntity.ok(myLearningService.updateMyLearningData(userDetails.getUsername(), usersId, dto));
    }

    // 회원 설정, 학습유형 삭제
    @Operation(summary = "회원 설정, 학습유형 삭제")
    @DeleteMapping("/{usersId}")
    public ResponseEntity<Void> deleteMyLearningData(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long usersId) {
        // fix: usersId를 검증 없이 신뢰하던 IDOR을 막기 위해 토큰 주체와 대조하도록 변경 (siwon)
        myLearningService.deleteMyLearningData(userDetails.getUsername(), usersId);
        return ResponseEntity.noContent().build();
    }
}