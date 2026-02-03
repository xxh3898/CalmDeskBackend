package com.code808.calmdesk.domain.common.enums;

public final class CommonEnums {
    public enum Status {
        Y("Y"), N("N"), R("R");
        private final String code;
        Status(String code) { this.code = code; }
        public String getCode() { return code; }
    }
}