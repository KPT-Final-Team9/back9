package com.core.back9.controller;

import com.core.back9.dto.MemberDTO;
import com.core.back9.dto.ScoreDTO;
import com.core.back9.security.AuthMember;
import com.core.back9.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/app/scores")
@RestController
public class AppUserScoreController {

	private final ScoreService scoreService;

	@GetMapping
	public ResponseEntity<List<ScoreDTO.Info>> getAll(
	  @AuthMember MemberDTO.Info member
	  ) {
		return ResponseEntity.ok(scoreService.selectAllByMember(member));
	}

}
