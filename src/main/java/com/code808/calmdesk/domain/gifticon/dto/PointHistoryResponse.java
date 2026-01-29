package com.code808.calmdesk.domain.gifticon.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointHistoryResponse {
    private String pointType; // EARN, SPEND
    private int amount; // 변동 금액
    private int balanceAfter; // 변동 후 잔액
    private String sourceType; // REWARD, GIFTICON
    private LocalDateTime date; // 발생 시간
}
