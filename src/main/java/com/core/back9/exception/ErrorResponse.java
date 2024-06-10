package com.core.back9.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse<T> {

    private int statusCode;
    private String message;

    public static <T> ErrorResponse<T> error(ApiErrorCode errorCode) {
        return new ErrorResponse<>(
                errorCode.getErrorCode(),
                errorCode.getErrorMessage()
        );
    }

    public static <T> ErrorResponse<T> error(ApiErrorCode errorCode, String message) {
        return new ErrorResponse<>(
                errorCode.getErrorCode(),
                message
        );
    }
}
