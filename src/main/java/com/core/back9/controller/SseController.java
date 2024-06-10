package com.core.back9.controller;

import com.core.back9.dto.MemberDTO;
import com.core.back9.security.AuthMember;
import com.core.back9.sse.connection.SseConnectionPoolImpl;
import com.core.back9.sse.connection.model.SseMemberConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

@RequiredArgsConstructor
@RequestMapping("/public-api/sse")
@RestController
public class SseController {

	private final SseConnectionPoolImpl sseConnectionPool;
	private final ObjectMapper objectMapper;

	@GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public ResponseBodyEmitter connect(
	  @AuthMember MemberDTO.Info member
	) {
		var memberConnection = SseMemberConnection.connect(member.getId().toString(), sseConnectionPool, objectMapper);
		sseConnectionPool.addSession(member.getId().toString(), memberConnection);
		return memberConnection.getSseEmitter();
	}

}
