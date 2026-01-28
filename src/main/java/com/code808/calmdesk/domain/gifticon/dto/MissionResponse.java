package com.code808.calmdesk.domain.gifticon.dto;

import com.code808.calmdesk.domain.gifticon.entity.MissionList;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MissionResponse {
    private Long id;
    private String title;
    private String description;
    private int reward;
    private String status; // 사용자의 완료 여부 (Y/N)
    private int progressCount; // 현재 진행도
    private int goalCount;     // 목표치
    private String missionCode; // status 매핑

    public static MissionResponse fromEntity(MissionList mission) {
        return MissionResponse.builder()
                .id(mission.getMissionListId())
                .title(mission.getRewardName())
                .description(mission.getRewardDescription())
                .reward(mission.getPointAccount())
                .status(mission.getStatus().name())
                .build();
    }
}