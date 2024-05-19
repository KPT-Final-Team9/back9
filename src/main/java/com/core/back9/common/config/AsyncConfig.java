package com.core.back9.common.config;

import com.core.back9.exception.ApiErrorCode;
import com.core.back9.exception.ApiException;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);		// 스레드풀에 항상 살아있는 최소 스레드, 예상 최대 동시 작업 수
		executor.setMaxPoolSize(10);		// 스레드풀의 확장 최대 스레드 수
		executor.setQueueCapacity(5);		// 스레드풀에서 사용할 최대 큐의 크기
		executor.setKeepAliveSeconds(30);	// 스레드 개수가 corePoolSize 초과인 상태에서 대기 상태의 스레드가 종료되기까지 대기 시간
		executor.setThreadNamePrefix("async-executor-");
		executor.setRejectedExecutionHandler(((r, exec) -> {
			throw new ApiException(ApiErrorCode.THREAD_POOL_REJECTED);
		}));
		executor.initialize();
		return executor;
	}

}
