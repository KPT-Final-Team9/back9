package com.core.back9.controller;

import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.RoomDTO;
import com.core.back9.entity.constant.RatingType;
import com.core.back9.security.AuthMember;
import com.core.back9.service.RoomService;
import com.core.back9.service.ScoreService;
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

	@GetMapping("")	// 대시보드에서 호실 목록 보여줄 때
	public ResponseEntity<Page<RoomDTO.Info>> getAll(
	  @PathVariable Long buildingId,
	  Pageable pageable
	) {
		return ResponseEntity.ok(roomService.selectAll(buildingId, pageable));
	}

	@GetMapping("/{roomId}")
	public ResponseEntity<RoomDTO.Info> getOne(
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId
	) {
		return ResponseEntity.ok(roomService.selectOne(buildingId, roomId));
	}

	@PostMapping("/{roomId}/setting-on")
	public ResponseEntity<RoomDTO.Info> turnOn(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId
	) {
		return ResponseEntity.ok(roomService.updateSwitch(member, buildingId, roomId, true));
	}

	@PostMapping("/{roomId}/setting-off")
	public ResponseEntity<RoomDTO.Info> turnOff(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId
	) {
		return ResponseEntity.ok(roomService.updateSwitch(member, buildingId, roomId, false));
	}

	@PatchMapping("/{roomId}/modify-encourage-message")
	public ResponseEntity<RoomDTO.Info> modify(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId,
	  @RequestBody String encourageMessage
	) {
		return ResponseEntity.ok(roomService.updateEncourageMessage(member, buildingId, roomId, encourageMessage));
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

	@PatchMapping("/{roomId}/setting-represent")
	public ResponseEntity<Page<RoomDTO.Info>> modifyRepresent(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId,
	  Pageable pageable
	) {
		return ResponseEntity.ok(roomService.updateRepresent(member, buildingId, roomId, pageable));
	}

	@PatchMapping("/{roomId}/scores-generate")
	public void evaluation(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId,
	  @RequestParam RatingType ratingType
	) {
		// 빌딩-호실에 대해 현재 계약중인 입주사에 평가를 수동으로 발생
		scoreService.create(member, buildingId, roomId, ratingType);
	}

}
