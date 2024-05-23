package com.core.back9.controller;

import com.core.back9.dto.BuildingDTO;
import com.core.back9.service.BuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/buildings")
@RestController
public class BuildingController {

	private final BuildingService buildingService;

	@PostMapping("")
	public ResponseEntity<BuildingDTO.Response> register(
	  @Valid
	  @RequestBody BuildingDTO.Request request
	) {
		return ResponseEntity.ok(buildingService.create(request));
	}

	@GetMapping("")
	public ResponseEntity<Page<BuildingDTO.Info>> getAll(Pageable pageable) {
		return ResponseEntity.ok(buildingService.selectAll(pageable));
	}

	@GetMapping("/{buildingId}")
	public ResponseEntity<BuildingDTO.Info> getOne(
	  @PathVariable Long buildingId,
	  Pageable pageable
	) {
		return ResponseEntity.ok(buildingService.selectOne(buildingId, pageable));
	}

	@PatchMapping("/{buildingId}")
	public ResponseEntity<BuildingDTO.Info> modify(
	  @PathVariable Long buildingId,
	  @Valid
	  @RequestBody BuildingDTO.Request request,
	  Pageable pageable
	) {
		return ResponseEntity.ok(buildingService.update(buildingId, request, pageable));
	}

	@DeleteMapping("/{buildingId}")
	public ResponseEntity<Boolean> unregister(
	  @PathVariable Long buildingId
	) {
		return ResponseEntity.ok(buildingService.delete(buildingId));
	}

}
