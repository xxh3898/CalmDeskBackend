package com.code808.calmdesk.domain.gifticon.entity;

import com.code808.calmdesk.domain.common.BaseTimeEntity;
import com.code808.calmdesk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity(name = "ORDERS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Order extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private Integer period;

    @Column(nullable = false)
    private LocalDate orderDate;

    @Column(nullable = false)
    private Integer approvalAmount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Order.Type type;

    @Column(nullable = false)
    private Integer earnPoint;

    @Column(nullable = false)
    private Integer spendPoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MISSION_ID")
    private MissionList missionList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GIFTICON_ID")
    private Gifticon gifticon;

    public enum Type {
        EARN, SPEND
    }
}
