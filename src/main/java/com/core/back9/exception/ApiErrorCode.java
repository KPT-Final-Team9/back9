package com.core.back9.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ApiErrorCode {

	NOT_FOUND_VALID_BUILDING(HttpStatus.NOT_FOUND.value(), "유효한 건물을 찾을 수 없습니다"),
	THREAD_POOL_REJECTED(HttpStatus.REQUEST_TIMEOUT.value(), "더 이상 요청을 처리 할 수 없습니다"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SERVER ERROR"),
	;

	private final Integer errorCode;
	private final String errorMessage;

}
