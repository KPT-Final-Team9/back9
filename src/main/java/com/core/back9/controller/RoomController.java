package com.core.back9.controller;

import com.core.back9.dto.RoomDTO;
import com.core.back9.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/buildings/{buildingId}/rooms")
@RestController
public class RoomController {

	private final RoomService roomService;

	@PostMapping("")
	public ResponseEntity<RoomDTO.Response> register(
	  /* TODO Member 추가 */
	  @PathVariable Long buildingId,
	  @Valid
	  @RequestBody RoomDTO.Request request
	) {
		return ResponseEntity.ok(roomService.create(buildingId, request));
	}

	@PatchMapping("/{roomId}")
	public ResponseEntity<RoomDTO.Info> modify(
	  /* TODO Member 추가 */
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId,
	  @Valid
	  @RequestBody RoomDTO.Request request
	) {
		return ResponseEntity.ok(roomService.update(roomId, request));
	}

	@DeleteMapping("/{roomId}")
	public ResponseEntity<Boolean> unregister(
	  /* TODO Member 추가 */
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId
	) {
		return ResponseEntity.ok(roomService.delete(buildingId, roomId));
	}

	@GetMapping("")
	public ResponseEntity<Page<RoomDTO.Info>> getAll(
	  /* TODO Member 추가 */
	  @PathVariable Long buildingId,
	  Pageable pageable
	) {
		return ResponseEntity.ok(roomService.selectAll(buildingId, pageable));
	}

	@GetMapping("/{roomId}")
	public ResponseEntity<RoomDTO.Info> getOne(
	  /* TODO Member 추가 */
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId
	) {
		return ResponseEntity.ok(roomService.selectOne(roomId));
	}

}
