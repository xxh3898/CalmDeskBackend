package com.code808.calmdesk.domain.gifticon.dto;

import com.code808.calmdesk.domain.gifticon.entity.CompanyGifticon;
import com.code808.calmdesk.domain.gifticon.entity.Gifticon;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ItemResponse {

    @Schema(description = "아이템 ID", example = "10")
    private Long id;
    @Schema(description = "아이템 명칭", example = "아메리카노")
    private String name;
    @Schema(description = "이미지 URL", example = "http://example.com/item.png")
    private String img;
    @Schema(description = "가격 (포인트)", example = "3500")
    private int price;
    @Schema(description = "재고 수량", example = "100")
    private int quantity; // 👈 이제 이 값은 CompanyGifticon의 재고가 됩니다.
    @Schema(description = "활성화 여부", example = "true")
    private boolean isActive;

    // 1. 기존 생성자 (전체 공통 조회용)
    public ItemResponse(Gifticon item) {
        this.id = item.getGifticonId();
        this.name = item.getGifticonName();
        this.img = item.getImage();
        this.price = Math.toIntExact(item.getPrice());
        this.quantity = item.getStockQuantity() != null ? item.getStockQuantity() : 0;
        this.isActive = item.getStatus() != null && "Y".equals(item.getStatus().getCode());
    }

    // 2. ✨ 추가된 생성자: 회사별 설정(CompanyGifticon) 반영용
    public ItemResponse(CompanyGifticon cg) {
        Gifticon item = cg.getGifticon(); // 마스터 정보 추출
        this.id = cg.getId();

        this.name = item.getGifticonName();
        this.img = item.getImage();
        this.price = Math.toIntExact(item.getPrice());

        // 💡 핵심: 재고와 활성화 여부를 CompanyGifticon 기준으로 채웁니다.
        this.quantity = cg.getStockQuantity() != null ? cg.getStockQuantity() : 0;
        this.isActive = cg.getIsActive() != null && cg.getIsActive();
    }

    public static ItemResponse fromEntity(Gifticon item) {
        return new ItemResponse(item);
    }

    // ✨ CompanyGifticon 전용 static 메서드 추가
    public static ItemResponse fromCompanyEntity(CompanyGifticon cg) {
        return new ItemResponse(cg);
    }
}
