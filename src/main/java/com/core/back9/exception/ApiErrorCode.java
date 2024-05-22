package com.core.back9.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ApiErrorCode {

	NOT_FOUND_VALID_BUILDING(HttpStatus.NOT_FOUND.value(), "유효한 건물을 찾을 수 없습니다"),
	NOT_FOUND_VALID_ROOM(HttpStatus.NOT_FOUND.value(), "유효한 호실을 찾을 수 없습니다"),
	NOT_FOUND_VALID_TENANT(HttpStatus.NOT_FOUND.value(), "유효한 입주사을 찾을 수 없습니다"),
	DELETE_FAIL(HttpStatus.BAD_REQUEST.value(), "삭제가 완료되지 않았습니다."),
	THREAD_POOL_REJECTED(HttpStatus.REQUEST_TIMEOUT.value(), "더 이상 요청을 처리 할 수 없습니다"),
	DO_NOT_HAVE_PERMISSION(HttpStatus.UNAUTHORIZED.value(), "권한이 없습니다"),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SERVER ERROR"),
	;

	private final Integer errorCode;
	private final String errorMessage;

}
