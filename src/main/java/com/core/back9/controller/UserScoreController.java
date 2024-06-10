package com.core.back9.controller;

import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.ScoreDTO;
import com.core.back9.security.AuthMember;
import com.core.back9.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/scores")
public class UserScoreController {

    private final ScoreService scoreService;

    @Operation(summary = "생성된 평가 진행", description = "입주자가 이용하고 있는 호실에 대한 평가를 진행한다.")
    @PatchMapping("/{scoreId}")
    public ResponseEntity<ScoreDTO.UpdateResponse> updateEvaluation(
            @AuthMember MemberDTO.Info member,
            @PathVariable Long scoreId,
            @RequestBody ScoreDTO.UpdateRequest updateRequest
    ) {
        return ResponseEntity.ok(scoreService.updateScore(member, scoreId, updateRequest));
    }

    @Operation(summary = "진행 중인 평가 정보 조회", description = "진행 중인 평가 정보를 조회해 평가 완료 여부를 확인한다.")
    @GetMapping("/in-progress")
    public ResponseEntity<List<ScoreDTO.InfoWithCompletionStatus>> getEvaluationsInProgress(
            @AuthMember MemberDTO.Info member
    ) {
        return ResponseEntity.ok(scoreService.getEvaluationsInProgress(member));
    }

}
