package com.core.back9.sse.connection.model;

import com.core.back9.sse.connection.SseConnectionPool;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Getter
public class SseMemberConnection {

	private final String uniqueKey;
	private final SseEmitter sseEmitter;
	private final SseConnectionPool<String, SseMemberConnection> connectionPool;
	private final ObjectMapper objectMapper;

	private SseMemberConnection(
	  String uniqueKey,
	  SseConnectionPool<String, SseMemberConnection> connectionPool,
	  ObjectMapper objectMapper
	) {
		this.uniqueKey = uniqueKey;
		this.sseEmitter = new SseEmitter(1000L * 20);
		this.connectionPool = connectionPool;
		this.objectMapper = objectMapper;

		this.sseEmitter.onCompletion(() -> connectionPool.onCompletionCallback(this));

		this.sseEmitter.onTimeout(this.sseEmitter::complete);

		sendMessage("onopen", "connect");
	}

	public static SseMemberConnection connect(
	  String uniqueKey,
	  SseConnectionPool<String, SseMemberConnection> connectionPool,
	  ObjectMapper objectMapper
	) {
		return new SseMemberConnection(uniqueKey, connectionPool, objectMapper);
	}

	public void sendMessage(String eventName, Object data) {
		try {
			String jsonData = this.objectMapper.writeValueAsString(data);
			SseEmitter.SseEventBuilder event = SseEmitter.event()
			  .name(eventName)
			  .data(jsonData);
			if (this.connectionPool.getSession(uniqueKey) != null) {
				this.sseEmitter.send(event);
			}
		} catch (IOException e) {
			this.sseEmitter.completeWithError(e);
		}
	}

	public void sendMessage(Object data) {
		try {
			String jsonData = this.objectMapper.writeValueAsString(data);
			SseEmitter.SseEventBuilder event = SseEmitter.event()
			  .data(jsonData);
			if (this.connectionPool.getSession(uniqueKey) != null) {
				this.sseEmitter.send(event);
			}
		} catch (IOException e) {
			this.sseEmitter.completeWithError(e);
		}
	}

}
