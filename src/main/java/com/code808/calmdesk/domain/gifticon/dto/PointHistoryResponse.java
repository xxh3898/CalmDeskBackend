package com.code808.calmdesk.domain.gifticon.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointHistoryResponse {
    private String pointType;
    private int amount;
    private int balanceAfter;
    private String sourceType;
    private LocalDateTime date;
}
