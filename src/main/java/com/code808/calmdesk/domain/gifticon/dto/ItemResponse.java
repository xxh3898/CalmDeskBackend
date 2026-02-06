package com.code808.calmdesk.domain.gifticon.dto;

import com.code808.calmdesk.domain.gifticon.entity.CompanyGifticon;
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
    private int quantity; // 👈 이제 이 값은 CompanyGifticon의 재고가 됩니다.
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