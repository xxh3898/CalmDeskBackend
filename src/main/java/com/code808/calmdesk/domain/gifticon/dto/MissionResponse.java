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
    private String status;      // 사용자의 완료 여부 (Y/N)
    private int progressCount;  // 현재 진행도
    private int goalCount;      // 목표치
    private String iconName;    // 리액트 아이콘 매핑용 필드 추가 (선택사항)

    /**
     * 마스터 정보와 유저의 진행 기록을 합쳐서 DTO로 변환
     */
    public static MissionResponse of(MissionList mission, int progressCount, String status) {
        return MissionResponse.builder()
                .id(mission.getMissionListId())
                .title(mission.getRewardName())
                .description(mission.getRewardDescription())
                .reward(mission.getPointAccount())
                .progressCount(progressCount)
                .goalCount(mission.getGoalCount())
                .status(status)
                // .iconName(mission.getIconName()) // 엔티티에 아이콘 필드가 있다면 추가
                .build();
    }
}