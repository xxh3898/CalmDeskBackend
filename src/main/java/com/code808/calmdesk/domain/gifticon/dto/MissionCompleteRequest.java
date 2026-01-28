package com.code808.calmdesk.domain.gifticon.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MissionCompleteRequest {
    private Long missionId;
    private Long userId;
}