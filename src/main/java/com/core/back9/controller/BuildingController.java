package com.core.back9.controller;

import com.core.back9.dto.BuildingDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.security.AuthMember;
import com.core.back9.service.BuildingService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/buildings")
@RestController
public class BuildingController {

	private final BuildingService buildingService;

	@Operation(summary = "빌딩 정보 등록", description = "빌딩 정보를 등록한다. (admin 계정만 가능)")
	@PostMapping("")
	public ResponseEntity<BuildingDTO.Response> register(
	  @AuthMember MemberDTO.Info member,
	  @Valid
	  @RequestBody BuildingDTO.Request request
	) {
		return ResponseEntity.ok(buildingService.create(member, request));
	}

	@Operation(summary = "빌딩 정보 수정", description = "빌딩 정보를 수정한다. (admin 계정만 가능)")
	@PatchMapping("/{buildingId}")
	public ResponseEntity<BuildingDTO.Info> modify(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @Valid
	  @RequestBody BuildingDTO.Request request,
	  Pageable pageable
	) {
		return ResponseEntity.ok(buildingService.update(member, buildingId, request, pageable));
	}

	@Operation(summary = "빌딩 정보 삭제", description = "빌딩 정보를 삭제한다. (admin 계정만 가능)")
	@DeleteMapping("/{buildingId}")
	public ResponseEntity<Boolean> unregister(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId
	) {
		return ResponseEntity.ok(buildingService.delete(member, buildingId));
	}

}
