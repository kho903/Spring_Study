package com.rest.docs.error;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_INPUT_VALUE("C001", "Invalid Input Value", 400),
    INTERNAL_SERVER_ERROR("C004", "Server Error", 500),
    ;

    private final String code;
    private final String message;
    private final int status;

    ErrorCode(final String code, final String message, final int status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
