package com.code808.calmdesk.domain.gifticon.dto;

import com.code808.calmdesk.domain.gifticon.entity.MissionList;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MissionResponse {
    private Long id;
    private String title; // rewardName 매핑
    private String description; // rewardDescription 매핑
    private Integer reward; // pointAccount 매핑
    private String status; // status 매핑

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