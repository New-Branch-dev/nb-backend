package com.newbranch.backend.domain.mylearning.controller;

import com.newbranch.backend.domain.mylearning.dto.MyLearningRequest;
import com.newbranch.backend.domain.mylearning.service.MyLearningService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<MyLearningRequest> getMyLearningData(@PathVariable Long usersId) {
        return ResponseEntity.ok(myLearningService.getMyLearningData(usersId));
    }

    // 회원 설정, 학습유형  등록
    @Operation(summary = "회원 설정, 학습유형 생성")
    @PostMapping
    public ResponseEntity<MyLearningRequest> createMyLearningData(@RequestBody MyLearningRequest dto) {
        return ResponseEntity.ok(myLearningService.createMyLearningData(dto));
    }

    // 회원 설정, 학습유형 수정
    @Operation(summary = "회원 설정, 학습유형 수정")
    @PutMapping("/{usersId}")
    public ResponseEntity<MyLearningRequest> updateMyLearningData(
            @PathVariable Long usersId,
            @RequestBody MyLearningRequest dto) {
        return ResponseEntity.ok(myLearningService.updateMyLearningData(usersId, dto));
    }

    // 회원 설정, 학습유형 삭제
    @Operation(summary = "회원 설정, 학습유형 삭제")
    @DeleteMapping("/{usersId}")
    public ResponseEntity<Void> deleteMyLearningData(@PathVariable Long usersId) {
        myLearningService.deleteMyLearningData(usersId);
        return ResponseEntity.noContent().build();
    }
}