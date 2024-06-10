package com.core.back9.sse.connection;

import com.core.back9.sse.connection.model.SseMemberConnection;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseConnectionPoolImpl implements SseConnectionPool<String, SseMemberConnection> {

	private final Map<String, SseMemberConnection> connectionPool = new ConcurrentHashMap<>();

	@Override
	public void addSession(String uniqueKey, SseMemberConnection session) {
		connectionPool.put(uniqueKey, session);
	}

	@Override
	public SseMemberConnection getSession(String uniqueKey) {
		return connectionPool.get(uniqueKey);
	}

	@Override
	public void onCompletionCallback(SseMemberConnection session) {
		connectionPool.remove(session.getUniqueKey());
	}

}
