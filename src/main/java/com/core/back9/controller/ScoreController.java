package com.core.back9.controller;

import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.ScoreDTO;
import com.core.back9.security.AuthMember;
import com.core.back9.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/scores")
public class ScoreController {

	private final ScoreService scoreService;

	@PatchMapping("/{scoreId}")
	public ResponseEntity<ScoreDTO.UpdateResponse> updateEvaluation(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long scoreId,
	  @RequestBody ScoreDTO.UpdateRequest updateRequest
	) {
		return ResponseEntity.ok(scoreService.updateScore(member, scoreId, updateRequest));
	}

	@PatchMapping("/{scoreId}/bookmark-add")
	public ResponseEntity<ScoreDTO.Info> addBookmark(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long scoreId
	) {
		return ResponseEntity.ok(scoreService.updateBookmark(member, scoreId, true));
	}

	@PatchMapping("/{scoreId}/bookmark-remove")
	public ResponseEntity<ScoreDTO.Info> removeBookmark(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long scoreId
	) {
		return ResponseEntity.ok(scoreService.updateBookmark(member, scoreId, false));
	}

}