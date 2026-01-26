package com.code808.calmdesk.domain.enums;

public final class CommonEnums {
    public enum Status {
        Y("Y"), N("N");
        private final String code;
        Status(String code) { this.code = code; }
        public String getCode() { return code; }
    }
}
