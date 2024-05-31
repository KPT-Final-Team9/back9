package com.core.back9.controller;

import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.RoomDTO;
import com.core.back9.security.AuthMember;
import com.core.back9.service.RoomService;
import com.core.back9.service.ScoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/buildings/{buildingId}/rooms")
@RestController
public class RoomController {

	private final RoomService roomService;
	private final ScoreService scoreService;

	@PostMapping("")
	public ResponseEntity<RoomDTO.Response> register(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @Valid
	  @RequestBody RoomDTO.Request request
	) {
		return ResponseEntity.ok(roomService.create(member, buildingId, request));
	}

	@PatchMapping("/{roomId}")
	public ResponseEntity<RoomDTO.Info> modify(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId,
	  @Valid
	  @RequestBody RoomDTO.Request request
	) {
		return ResponseEntity.ok(roomService.update(member, buildingId, roomId, request));
	}

	@DeleteMapping("/{roomId}")
	public ResponseEntity<Boolean> unregister(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId
	) {
		return ResponseEntity.ok(roomService.delete(member, buildingId, roomId));
	}

	@PatchMapping("/{roomId}/owners/{ownerId}")
	public ResponseEntity<RoomDTO.InfoWithOwner> giveRoom(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId,
	  @PathVariable Long ownerId
	) {
		return ResponseEntity.ok(roomService.settingOwner(member, buildingId, roomId, ownerId));
	}

}
