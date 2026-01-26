package com.code808.calmdesk.domain.gifticon.dto;

import com.code808.calmdesk.domain.enums.CommonEnums;
import com.code808.calmdesk.domain.gifticon.entity.Gifticon;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemResponse {
    private Long id;
    private String name; // 리액트에서 item.name으로 쓸 수 있게 매핑
    private String img; // 리액트에서 item.img로 쓸 수 있게 매핑
    private int price;
    private int quantity;
    private boolean isActive;

    public ItemResponse(Gifticon item) {
        this.id = item.getGifticonId();
        // 엔티티의 productName을 DTO의 name으로 매핑
        this.name = item.getGifticonName();
        // 엔티티의 image를 DTO의 img로 매핑
        this.img = item.getImg();
        this.price = Math.toIntExact(item.getPrice());
        this.quantity = item.getQuantity();
        // boolean 필드의 getter는 보통 is...() 형식입니다.
        this.isActive = (item.getStatus() == CommonEnums.Status.Y);
    }
}