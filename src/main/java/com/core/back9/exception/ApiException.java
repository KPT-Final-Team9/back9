package com.core.back9.exception;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

	private final ApiErrorCode apiErrorCode;
	private final String errorMessage;

	public ApiException(ApiErrorCode apiErrorCode) {
		this.apiErrorCode = apiErrorCode;
		this.errorMessage = apiErrorCode.getErrorMessage();
	}

	public ApiException(ApiErrorCode apiErrorCode, String errorMessage) {
		this.apiErrorCode = apiErrorCode;
		this.errorMessage = errorMessage;
	}

}
