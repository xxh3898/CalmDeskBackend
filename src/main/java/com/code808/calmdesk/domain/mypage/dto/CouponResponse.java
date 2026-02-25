package com.code808.calmdesk.domain.mypage.dto;

import com.code808.calmdesk.domain.gifticon.entity.Gifticon;
import com.code808.calmdesk.domain.gifticon.entity.Order;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "주문 ID", example = "100")
    private Long orderId;
    @Schema(description = "기프티콘 ID", example = "50")
    private Long gifticonId;
    @Schema(description = "기프티콘 상품명", example = "아이스 아메리카노")
    private String gifticonName;
    @Schema(description = "판매처/브랜드", example = "스타벅스")
    private String shop;
    @Schema(description = "상품 이미지 URL", example = "http://example.com/image.jpg")
    private String image;
    @Schema(description = "구매 가격", example = "4500")
    private Integer price;
    @Schema(description = "유효 기간 (yyyy.MM.dd)", example = "2024.12.31")
    private String expiryDate;
    @Schema(description = "상태 (AVAILABLE: 사용 가능, USED: 사용 완료/만료)", example = "AVAILABLE")
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
