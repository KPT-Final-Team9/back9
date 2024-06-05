package com.core.back9.controller;

import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.ScoreDTO;
import com.core.back9.entity.constant.RatingType;
import com.core.back9.security.AuthMember;
import com.core.back9.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/scores")
public class OwnerScoreController {

	private final ScoreService scoreService;

	@Operation(summary = "평가 상세 보기 조회", description = "모든 조건에 부합하는 평가 항목을 조회한다.")
	@GetMapping("")
	public ResponseEntity<Page<ScoreDTO.Info>> searchScores(
	  @AuthMember MemberDTO.Info member,
	  @RequestParam Long buildingId,
	  @RequestParam(required = false) Long roomId,
	  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
	  @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
	  @RequestParam(required = false, defaultValue = "") RatingType ratingType,
	  @RequestParam(required = false, defaultValue = "false") Boolean bookmark,
	  @RequestParam(required = false) String keyword,
	  Pageable pageable
	) {
		Page<ScoreDTO.Info> response = scoreService.selectScores(
		  member, buildingId, roomId,
		  startDate.atTime(LocalTime.MIN), endDate.atTime(LocalTime.MAX),
		  ratingType, bookmark, keyword, pageable);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "평가 북마크", description = "해당 평가를 북마크 설정한다.")
	@PatchMapping("/{scoreId}/bookmark-add")
	public ResponseEntity<ScoreDTO.Info> addBookmark(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long scoreId
	) {
		return ResponseEntity.ok(scoreService.updateBookmark(member, scoreId, true));
	}

	@Operation(summary = "평가 북마크", description = "해당 평가를 북마크 해제한다.")
	@PatchMapping("/{scoreId}/bookmark-remove")
	public ResponseEntity<ScoreDTO.Info> removeBookmark(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long scoreId
	) {
		return ResponseEntity.ok(scoreService.updateBookmark(member, scoreId, false));
	}

}
