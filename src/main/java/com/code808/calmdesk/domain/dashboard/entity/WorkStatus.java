package com.code808.calmdesk.domain.dashboard.entity;

public enum WorkStatus {
    READY("업무 준비"),
    WORKING("업무 중"),
    AWAY("자리 비움"),
    COOLDOWN("쿨다운"),
    OFF("퇴근 완료");

    private final String description;

    WorkStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
