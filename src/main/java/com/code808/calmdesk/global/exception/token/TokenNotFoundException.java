package com.code808.calmdesk.global.exception.token;

public class TokenNotFoundException extends RuntimeException {

    private final String errorCode;

    public TokenNotFoundException() {
        super("토큰을 찾을 수 없습니다.");
        this.errorCode = "TOKEN_NOT_FOUND";
    }

    public TokenNotFoundException(String message) {
        super(message);
        this.errorCode = "TOKEN_NOT_FOUND";
    }

    public TokenNotFoundException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
