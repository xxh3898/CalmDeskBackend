package com.code808.calmdesk.domain.mypage.dto;

import com.code808.calmdesk.domain.gifticon.entity.Gifticon;
import com.code808.calmdesk.domain.gifticon.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponResponse {
    private Long orderId;
    private Long gifticonId;
    private String gifticonName;
    private String shop;
    private String image;
    private Integer price;
    private String expiryDate;
    private String status; // AVAILABLE, USED

    public static CouponResponse from(Order order, Gifticon gifticon) {
        int period = order.getPeriod() != null ? order.getPeriod() : 0;
        LocalDate expiryDate = order.getOrderDate() != null
                ? order.getOrderDate().plusDays(period)
                : LocalDate.now();
        String status = expiryDate.isAfter(LocalDate.now()) ? "AVAILABLE" : "USED";

        String gifticonName = gifticon != null ? gifticon.getGifticonName() : "";
        String shop = gifticon != null ? gifticon.getGifticonName() : "";
        String image = gifticon != null ? gifticon.getImage() : null;
        Integer price = order.getApprovalAmount() != null ? order.getApprovalAmount() : 0;

        return CouponResponse.builder()
                .orderId(order.getId())
                .gifticonId(gifticon != null ? gifticon.getGifticonId() : null)
                .gifticonName(gifticonName)
                .shop(shop)
                .image(image)
                .price(price)
                .expiryDate(expiryDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                .status(status)
                .build();
    }
}
