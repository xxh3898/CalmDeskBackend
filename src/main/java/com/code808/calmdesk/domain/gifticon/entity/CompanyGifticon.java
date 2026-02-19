package com.code808.calmdesk.domain.gifticon.entity;

import com.code808.calmdesk.domain.company.entity.Company;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA를 위한 기본 생성자
@AllArgsConstructor // Builder를 위한 전체 필드 생성자
public class CompanyGifticon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gifticon_id")
    private Gifticon gifticon;

    private Integer stockQuantity; // 회사별 재고
    private Boolean isActive;      // 회사별 판매 여부

    // --- 비즈니스 로직 (Setter 대신 사용 권장) ---

    public void updateStock(Integer quantity) {
        if (quantity < 0) throw new IllegalArgumentException("재고는 0보다 작을 수 없습니다.");
        this.stockQuantity = quantity;
    }

    public void toggleActive() {
        this.isActive = !this.isActive;
    }

    public void setActive(Boolean active) {
        this.isActive = active;
    }

    // 초기 생성을 위한 편의 생성자 (서비스 로직에서 new CompanyGifticon(...) 사용 시 필요)
    public CompanyGifticon(Company company, Gifticon gifticon, Integer stockQuantity, Boolean isActive) {
        this.company = company;
        this.gifticon = gifticon;
        this.stockQuantity = stockQuantity;
        this.isActive = isActive;
    }
}