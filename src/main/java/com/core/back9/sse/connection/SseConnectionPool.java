package com.core.back9.sse.connection;

public interface SseConnectionPool<T, R> {

	void addSession(T uniqueKey, R session);

	R getSession(T uniqueKey);

	void onCompletionCallback(R session);

}
