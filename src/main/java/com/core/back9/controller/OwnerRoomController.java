package com.core.back9.controller;

import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.RoomDTO;
import com.core.back9.dto.ScoreDTO;
import com.core.back9.entity.constant.RatingType;
import com.core.back9.security.AuthMember;
import com.core.back9.service.RoomService;
import com.core.back9.service.ScoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RequiredArgsConstructor
@RequestMapping("/api/buildings/{buildingId}/rooms")
@RestController
public class OwnerRoomController {

	private final RoomService roomService;
	private final ScoreService scoreService;

	@Operation(summary = "전체 호실 조회",
	  description = "해당 건물의 전체 호실의 기본정보와 계약목록, 설정상태를 조회한다.")
	@GetMapping("")
	public ResponseEntity<Page<RoomDTO.Info>> getAll(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  Pageable pageable
	) {
		return ResponseEntity.ok(roomService.selectAll(member, buildingId, pageable));
	}

	@Operation(summary = "단일 호실 조회",
	  description = "해당 건물의 해당 호실의 기본정보와 계약목록, 설정상태를 조회한다.")
	@GetMapping("/{roomId}")
	public ResponseEntity<RoomDTO.Info> getOne(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId
	) {
		return ResponseEntity.ok(roomService.selectOne(member, buildingId, roomId));
	}

	@Operation(summary = "평가받기 설정 변경",
	  description = "평가받기 설정을 켠다")
	@PostMapping("/{roomId}/setting-on")
	public ResponseEntity<RoomDTO.Info> turnOn(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId
	) {
		return ResponseEntity.ok(roomService.updateSwitch(member, buildingId, roomId, true));
	}

	@Operation(summary = "평가받기 설정 변경",
	  description = "평가받기 설정을 끈다")
	@PostMapping("/{roomId}/setting-off")
	public ResponseEntity<RoomDTO.Info> turnOff(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId
	) {
		return ResponseEntity.ok(roomService.updateSwitch(member, buildingId, roomId, false));
	}

	@Operation(summary = "평가 독려 메시지 수정")
	@PatchMapping("/{roomId}/modify-encourage-message")
	public ResponseEntity<RoomDTO.Info> modify(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId,
	  @RequestBody String encourageMessage
	) {
		return ResponseEntity.ok(roomService.updateEncourageMessage(member, buildingId, roomId, encourageMessage));
	}

	@Operation(summary = "대표 호실 설정")
	@PatchMapping("/{roomId}/setting-represent")
	public ResponseEntity<Page<RoomDTO.Info>> modifyRepresent(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId,
	  Pageable pageable
	) {
		return ResponseEntity.ok(roomService.updateRepresent(member, buildingId, roomId, pageable));
	}

	@Operation(summary = "대표 호실 조회")
	@GetMapping("/represent")
	public ResponseEntity<RoomDTO.Info> getRepresent(
			@AuthMember MemberDTO.Info member,
			@PathVariable Long buildingId
	) {
		return ResponseEntity.ok(roomService.getRepresent(buildingId, member));
	}

	@Operation(summary = "평가 레코드 발행",
	  description = "해당 호실에 계약중인 입주사의 유효한 입주자에게 평가 레코드를 발생시킨다.")
	@PatchMapping("/{roomId}/scores-generate")
	public void evaluation(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId,
	  @RequestParam RatingType ratingType
	) {
		scoreService.create(member, buildingId, roomId, ratingType);
	}

	@Operation(summary = "호실 상세 페이지",
	  description = "해당 호실과 타호실의 선택한 년/월로부터 이전 1년간 총 평균 점수, 평가 항목별 점수, 평가 진행률을 배열로 조회한다.")
	@GetMapping("/{roomId}/yearly-score-interval-month")
	public ResponseEntity<ScoreDTO.ListOfYearAvgWithMeAndOthers> searchYearScoresIntervalMonth(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long buildingId,
	  @PathVariable Long roomId,
	  @RequestParam @DateTimeFormat(pattern = "yyyy-MM")
	  @Parameter(description = "선택 년-월", example = "2024-05") YearMonth yearMonth
	) {
		return ResponseEntity.ok(scoreService.selectYearScoresIntervalMonth(member, buildingId, roomId, yearMonth));
	}

}
