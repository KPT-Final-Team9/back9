package com.core.back9.controller;

import com.core.back9.dto.BuildingDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.security.AuthMember;
import com.core.back9.service.BuildingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/buildings")
@RestController
public class OwnerBuildingController {

	private final BuildingService buildingService;

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

}
