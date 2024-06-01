package com.core.back9.controller;

import com.core.back9.dto.BuildingDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.ScoreDTO;
import com.core.back9.security.AuthMember;
import com.core.back9.service.BuildingService;
import com.core.back9.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/buildings")
@RestController
public class OwnerBuildingController {

	private final BuildingService buildingService;
	private final ScoreService scoreService;

	@GetMapping("")
	public ResponseEntity<Page<BuildingDTO.Info>> getAll(
	  @AuthMember MemberDTO.Info member,
	  Pageable pageable
	) {
		return ResponseEntity.ok(buildingService.selectAll(member, pageable));
	}

	@GetMapping("/{buildingId}")
	public ResponseEntity<BuildingDTO.Info> getOne(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  Pageable pageable
	) {
		return ResponseEntity.ok(buildingService.selectOne(member, buildingId, pageable));
	}

	@GetMapping("/{buildingId}/quarterly")
	public ResponseEntity<ScoreDTO.DetailByQuarter> searchScoresByQuarter(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @RequestParam int year,
	  @RequestParam int quarter,
	  Pageable pageable
	) {
		return ResponseEntity.ok(scoreService.selectScoresByQuarter(member, buildingId, year, quarter, pageable));
	}

}
