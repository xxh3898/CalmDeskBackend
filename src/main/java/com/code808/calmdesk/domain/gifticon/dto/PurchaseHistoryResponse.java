package com.code808.calmdesk.domain.gifticon.dto;


import lombok.Builder;
import lombok.Getter;// PurchaseHistoryResponse.java (DTO)
import java.time.LocalDateTime;

@Getter
@Builder
public class PurchaseHistoryResponse {
    private Long id;
    private String userName;
    private String itemName;
    private Integer itemPrice;
    private String itemImg;
    private LocalDateTime purchaseDate;
}
