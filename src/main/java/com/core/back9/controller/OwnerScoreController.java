package com.core.back9.controller;

import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.ScoreDTO;
import com.core.back9.entity.constant.RatingType;
import com.core.back9.security.AuthMember;
import com.core.back9.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/buildings/{buildingId}/rooms/{roomId}/scores")
public class OwnerScoreController {

	private final ScoreService scoreService;

	/*
	조회 관련 api 필터링 목록
	- 기간, 건물, 호실, 평가항목, 북마크
	 */

	@GetMapping("")
	public ResponseEntity<Page<ScoreDTO.Info>> searchScores(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId,
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

	@GetMapping("/monthly")
	public ResponseEntity<List<ScoreDTO.DetailByMonth>> searchScoresByMonth(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId,
	  @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth
	) {
		return ResponseEntity.ok(scoreService.selectScoresByMonth(member, buildingId, roomId, yearMonth));
	}

	@PatchMapping("/{scoreId}/bookmark-add")
	public ResponseEntity<ScoreDTO.Info> addBookmark(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId,
	  @PathVariable Long scoreId
	) {
		return ResponseEntity.ok(scoreService.updateBookmark(member, scoreId, true));
	}

	@PatchMapping("/{scoreId}/bookmark-remove")
	public ResponseEntity<ScoreDTO.Info> removeBookmark(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId,
	  @PathVariable Long scoreId
	) {
		return ResponseEntity.ok(scoreService.updateBookmark(member, scoreId, false));
	}

}
