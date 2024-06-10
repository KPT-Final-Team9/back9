package com.core.back9.controller;

import com.core.back9.dto.AlarmDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.security.AuthMember;
import com.core.back9.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/app/alarms")
@RestController
public class AppUserAlarmController {

	private final AlarmService alarmService;

	@GetMapping
	public ResponseEntity<List<AlarmDTO.Info>> getAll(
	  @AuthMember MemberDTO.Info member
	) {
		return ResponseEntity.ok(alarmService.selectAllById(member));
	}

	@PatchMapping("/{alarmId}")
	public ResponseEntity<Void> modifyRead(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long alarmId
	) {
		alarmService.updateRead(member, alarmId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/{alarmId}")
	public ResponseEntity<Void> unregister(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long alarmId
	) {
		alarmService.delete(member, alarmId);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/is-new")
	public ResponseEntity<Boolean> isNew(
	  @AuthMember MemberDTO.Info member
	) {
		return ResponseEntity.ok(alarmService.hasUnreadAlarms(member));
	}

}
