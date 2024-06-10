package com.core.back9.controller;

import com.core.back9.dto.MemberDTO;
import com.core.back9.security.AuthMember;
import com.core.back9.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/app/members")
@RestController
public class AppUserMemberController {

	private final MemberService memberService;

	@GetMapping("/info")
	public ResponseEntity<MemberDTO.Info> me(
	  @AuthMember MemberDTO.Info member
	) {
		return ResponseEntity.ok(memberService.selectOne(member));
	}

	@GetMapping("/all")
	public ResponseEntity<List<MemberDTO.Info>> getAllMembers(
	  @AuthMember MemberDTO.Info member
	) {
		return ResponseEntity.ok(memberService.selectAllMembersByTenantId(member));
	}

}
