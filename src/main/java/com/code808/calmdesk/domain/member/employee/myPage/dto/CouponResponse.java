package com.code808.calmdesk.domain.member.employee.myPage.dto;

import com.example.demo.entity.Order;
import com.example.demo.entity.Gifticon;
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
    private Integer price;
    private String expiryDate;
    private String status; // AVAILABLE, USED

    public static CouponResponse from(Order order) {
        Gifticon gifticon = order.getGifticon();
        LocalDate expiryDate = order.getOrderDate().plusDays(order.getPeriod());
        String status = expiryDate.isAfter(LocalDate.now()) ? "AVAILABLE" : "USED";

        // shop 정보는 gifticonName에서 추출하거나 별도 필드가 필요할 수 있음
        String shop = gifticon.getGifticonName(); // 임시로 이름 사용

        // Order는 복합키(OrderId)를 사용하므로, orderId는 gifticonId를 사용하거나 null로 설정
        // 실제로는 복합키를 문자열로 변환하거나 별도의 순차 ID가 필요할 수 있음
        Long orderIdValue = order.getId() != null ? order.getId().getGifticonId() : null;

        return CouponResponse.builder()
                .orderId(orderIdValue)
                .gifticonId(gifticon.getGifticonId())
                .gifticonName(gifticon.getGifticonName())
                .shop(shop)
                .price(order.getApprovalAmount())
                .expiryDate(expiryDate.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")))
                .status(status)
                .build();
    }
}
