package com.core.back9.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ApiErrorCode {

	NOT_FOUND_VALID_BUILDING(HttpStatus.NOT_FOUND.value(), "유효한 건물을 찾을 수 없습니다"),
	NOT_FOUND_VALID_ROOM(HttpStatus.NOT_FOUND.value(), "유효한 호실을 찾을 수 없습니다"),
    NOT_FOUND_VALID_TENANT(HttpStatus.NOT_FOUND.value(), "유효한 입주사를 찾을 수 없습니다"),
    NOT_FOUND_VALID_CONTRACT(HttpStatus.NOT_FOUND.value(), "유효한 계약을 찾을 수 없습니다."),
    NOT_FOUND_VALID_MEMBER(HttpStatus.NOT_FOUND.value(), "유효한 사용자를 찾을 수 없습니다."),
    NOT_FOUND_VALID_PRINCIPAL(HttpStatus.NOT_FOUND.value(), "유효한 주체를 찾을 수 없습니다."),
    NOT_FOUND_VALID_EVALUATION(HttpStatus.NOT_FOUND.value(), "유효한 평가를 찾을 수 없습니다."),

    ROOM_ALREADY_ASSIGNED(HttpStatus.BAD_REQUEST.value(), "이미 계약된 호실이 존재합니다."),
    CONTRACT_NOT_IN_PROGRESS(HttpStatus.BAD_REQUEST.value(), "계약이 이행 중인 상태가 아닙니다."),
    INVALID_CHANGE(HttpStatus.BAD_REQUEST.value(), "유효한 변경이 아닙니다."),
    DELETE_FAIL(HttpStatus.BAD_REQUEST.value(), "삭제가 완료되지 않았습니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST.value(), "이미 가입된 이메일입니다."),
    DUPLICATE_PHONE_NUMBER(HttpStatus.BAD_REQUEST.value(), "이미 가입된 전화번호입니다."),
    INCORRECT_EMAIL_FORMAT(HttpStatus.BAD_REQUEST.value(), "잘못된 이메일 형식입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST.value(), "잘못된 비밀번호입니다."),
    NOT_AUTHENTICATED_USER(HttpStatus.BAD_REQUEST.value(), "인증된 사용자가 아닙니다."),
    EMPTY_TOKEN(HttpStatus.BAD_REQUEST.value(), "토큰이 비어 있습니다."),
    NOT_BEARER_TOKEN(HttpStatus.BAD_REQUEST.value(), "BEARER 토큰이 아닙니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST.value(), "잘못된 JWT입니다."),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST.value(), "만료된 JWT입니다."),

	THREAD_POOL_REJECTED(HttpStatus.REQUEST_TIMEOUT.value(), "더 이상 요청을 처리 할 수 없습니다"),

	DO_NOT_HAVE_PERMISSION(HttpStatus.UNAUTHORIZED.value(), "권한이 없습니다"),

	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "SERVER ERROR");

	private final Integer errorCode;
	private final String errorMessage;

}
