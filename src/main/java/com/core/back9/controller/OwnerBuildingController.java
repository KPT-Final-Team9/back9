package com.core.back9.controller;

import com.core.back9.dto.BuildingDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.ScoreDTO;
import com.core.back9.security.AuthMember;
import com.core.back9.service.BuildingService;
import com.core.back9.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/buildings")
@RestController
public class OwnerBuildingController {

	private final BuildingService buildingService;
	private final ScoreService scoreService;

	@Operation(summary = "전체 빌딩 정보 조회",
	  description = "로그인 한 소유자(owner)의 전체 빌딩 정보를 조회한다.")
	@GetMapping("")
	public ResponseEntity<Page<BuildingDTO.Info>> getAll(
	  @AuthMember MemberDTO.Info member,
	  Pageable pageable
	) {
		return ResponseEntity.ok(buildingService.selectAll(member, pageable));
	}

	@Operation(summary = "단일 빌딩 정보 조회",
	  description = "로그인 한 소유자(owner)의 단일 빌딩 정보를 조회한다.")
	@GetMapping("/{buildingId}")
	public ResponseEntity<BuildingDTO.Info> getOne(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  Pageable pageable
	) {
		return ResponseEntity.ok(buildingService.selectOne(member, buildingId, pageable));
	}

	@Operation(summary = "대시보드 페이지",
	  description = "해당 빌딩의 내 모든 호실의 현재분기의 총 평균 점수, 평가 타입별 평균 점수를 조회한다.")
	@GetMapping("/{buildingId}/my-quarterly-score")
	public ResponseEntity<ScoreDTO.AvgByQuarter> searchScoreByQuarter(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @RequestParam int year,
	  @RequestParam int quarter
	) {
		return ResponseEntity.ok(scoreService.selectScoresByQuarter(member, buildingId, year, quarter));
	}

	@Operation(summary = "대시보드 페이지",
	  description = "해당 빌딩의 내 각 호실의 분기별 총 평균 점수를 조회한다.")
	@GetMapping("/{buildingId}/my-rooms-quarterly-score")
	public ResponseEntity<ScoreDTO.CurrentAndBeforeQuarterlyTotalAvg> searchQuarterlyScoreOfMyRooms(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @RequestParam int year,
	  @RequestParam int quarter
	) {
		return ResponseEntity.ok(scoreService.selectQuarterlyScoreOfMyRooms(member, buildingId, year, quarter));
	}

	@Operation(summary = "대시보드 페이지",
	  description = "해당 빌딩의 내 각 호실의 1년간의 총 평균 점수, 평가 타입별 평균 점수를 조회한다.")
	@GetMapping("/{buildingId}/my-rooms-year-score")
	public ResponseEntity<List<ScoreDTO.AllAvgByRoom>> searchYearScoresOfMyRooms(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId
	) {
		return ResponseEntity.ok(scoreService.selectYearScoreOfMyRooms(member, buildingId));
	}

	@Operation(summary = "대시보드 페이지", description = "해당 빌딩의 내 모든 호실에 대해 최근 2년 동안 유효한 평가가 1건도 없으면 false를 반환한다.")
	@GetMapping("/{buildingId}/valid-score")
	public ResponseEntity<Boolean> hasValidScore(@AuthMember MemberDTO.Info member, @PathVariable Long buildingId) {
		return ResponseEntity.ok(scoreService.hasValidScore(member, buildingId));
	}

}
