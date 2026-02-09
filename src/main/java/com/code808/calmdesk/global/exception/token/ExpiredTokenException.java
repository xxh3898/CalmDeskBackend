package com.code808.calmdesk.global.exception.token;

public class ExpiredTokenException extends RuntimeException {

    private final String errorCode;

    public ExpiredTokenException() {
        super("만료된 토큰입니다.");
        this.errorCode = "TOKEN_EXPIRED";
    }

    public ExpiredTokenException(String message) {
        super(message);
        this.errorCode = "TOKEN_EXPIRED";
    }

    public ExpiredTokenException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}