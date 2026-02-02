package com.code808.calmdesk.domain.gifticon.dto;

import lombok.*;
import java.util.List; // 리스트 사용을 위해 반드시 필요합니다.

@Builder
@Getter
@Setter
@NoArgsConstructor // 역직렬화를 위해 기본 생성자 추가
@AllArgsConstructor // Builder 패턴 작동을 위해 전체 생성자 추가
public class PointMallResponse {
    private Integer currentPoint; // "나의 보유 포인트"
    private List<ItemResponse> shopItems; // 상점 아이템 목록
    private List<MissionResponse> missions; // 미션 목록
}