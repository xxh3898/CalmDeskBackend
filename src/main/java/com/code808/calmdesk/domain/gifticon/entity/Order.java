package com.code808.calmdesk.domain.gifticon.entity;

import com.code808.calmdesk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity(name = "ORDERS")
@Table(name = "GIFT_ORDER")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "PERIOD", nullable = false)
    private Integer period;

    @Column(name = "ORDER_DATE", nullable = false)
    private LocalDate orderDate;

    @Column(name = "APPROVAL_AMOUNT", nullable = false)
    private Integer approvalAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GIFTICON_ID", nullable = false)
    private Gifticon gifticon;

    public Long getId() { return orderId; }
}
