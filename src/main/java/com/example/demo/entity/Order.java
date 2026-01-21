package com.example.demo.entity;

import com.example.demo.id.OrderId;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity(name = "ORDERS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order {

    @EmbeddedId
    private OrderId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("gifticonId")
    @JoinColumn(name = "GIFTICON_ID")
    private Gifticon gifticon;

    @Column(nullable = false)
    private Integer period;

    @Column(nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false)
    private Integer approvalAmount;


}
