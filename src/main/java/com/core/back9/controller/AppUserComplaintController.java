package com.core.back9.controller;

import com.core.back9.dto.ComplaintDTO;
import com.core.back9.dto.MemberDTO;
import com.core.back9.security.AuthMember;
import com.core.back9.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/app/complaints")
@RestController
public class AppUserComplaintController {

	private final ComplaintService complaintService;

	@PostMapping("")
	public ResponseEntity<Void> register(
	  @AuthMember MemberDTO.Info member,
	  @RequestBody ComplaintDTO.RegisterRequest registerRequest
	) {
		complaintService.create(member, registerRequest);
		return ResponseEntity.ok().build();
	}

	@GetMapping("")
	public ResponseEntity<List<ComplaintDTO.Info>> getAll(
	  @AuthMember MemberDTO.Info member
	) {
		return ResponseEntity.ok(complaintService.selectAllByMemberId(member));
	}

	@PatchMapping("/{complaintId}/completed")
	public ResponseEntity<Void> modifyCompleted(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long complaintId,
	  @RequestBody(required = false) String completeMessage
	) {
		complaintService.updateCompleted(member, complaintId, completeMessage);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("/{complaintId}/rejected")
	public ResponseEntity<Void> modifyRejected(
	  @AuthMember MemberDTO.Info member,
	  @PathVariable Long complaintId,
	  @RequestBody(required = false) String rejectMessage
	) {
		complaintService.updateRejected(member, complaintId, rejectMessage);
		return ResponseEntity.noContent().build();
	}


	@DeleteMapping("/{complaintId}")
	public ResponseEntity<Void> unregister(
	  @AuthMember MemberDTO.Info user,
	  @PathVariable Long complaintId
	) {
		complaintService.delete(user, complaintId);
		return ResponseEntity.ok().build();
	}

}
