package com.code808.calmdesk.global.exception.token;

public class InvalidTokenException extends RuntimeException {
    private final String errorCode;

    public InvalidTokenException() {
        super("유효하지 않은 토큰입니다.");
        this.errorCode = "TOKEN_INVALID";
    }

    public InvalidTokenException(String message) {
        super(message);
        this.errorCode = "TOKEN_INVALID";
    }

    public InvalidTokenException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
