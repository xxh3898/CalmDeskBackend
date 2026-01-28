package com.code808.calmdesk.domain.gifticon.dto;

import com.code808.calmdesk.domain.gifticon.entity.Gifticon;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemResponse {
    private Long id;
    private String name;
    private String img;
    private int price;
    private int quantity;
    private boolean isActive;

    public ItemResponse(Gifticon item) {
        this.id = item.getGifticonId();
        this.name = item.getGifticonName();
        this.img = item.getImage();
        this.price = item.getPrice() != null ? item.getPrice() : 0;
        this.quantity = item.getStockQuantity() != null ? item.getStockQuantity() : 0;
        this.isActive = item.getStatus() != null && "Y".equals(item.getStatus().getCode());
    }
}
