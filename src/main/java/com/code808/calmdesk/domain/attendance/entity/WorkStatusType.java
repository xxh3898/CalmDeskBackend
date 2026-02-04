package com.code808.calmdesk.domain.attendance.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WorkStatusType {
    READY("출근 전"),
    WORKING("업무 중"),
    AWAY("자리 비움"),
    COOLDOWN("쿨다운"),
    OFF("퇴근");

    private final String description;
}
