package com.code808.calmdesk.domain.gifticon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest {

    @Schema(description = "아이템 ID", example = "10")
    private Long itemId; // GIFTICON_ID
    @Schema(description = "사용자 ID", example = "5")
    private Long userId; // MEMBER_ID
    @Schema(description = "승인 금액 (포인트)", example = "3500")
    private Long price; // APPROVAL_AMOUNT
}
