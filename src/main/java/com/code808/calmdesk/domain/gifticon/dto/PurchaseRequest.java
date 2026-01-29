package com.code808.calmdesk.domain.gifticon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequest {

    private Long itemId; // GIFTICON_ID
    private Long userId; // MEMBER_ID
    private Long price; // APPROVAL_AMOUNT
}
