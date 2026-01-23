package com.code808.calmdesk.domain.gifticon.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "GIFTICON")
@Getter
@Setter
@NoArgsConstructor
public class Gifticon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GIFTICON_ID")
    private Long id; // 기프티콘 아이디 (NUMBER)

    @Column(name = "PRODUCT_NAME", nullable = false, length = 100)
    private String name; // 상품 이름 (VARCHAR2(100))

    @Column(name = "IMAGE", nullable = false, length = 100)
    private String img;

    @Column(name = "PRICE", nullable = false)
    private Long price; // 가격 (NUMBER)

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity; // 재고 (NUMBER)

    @CreationTimestamp
    @Column(name = "ORDER_DATE", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP") // DB
                                                                                                                                // 레벨
                                                                                                                                // 기본값
                                                                                                                                // 설정
    private LocalDateTime orderDate = LocalDateTime.now();

    @Column(name = "IS_ACTIVE", nullable = false)
    private boolean isActive = true;
}