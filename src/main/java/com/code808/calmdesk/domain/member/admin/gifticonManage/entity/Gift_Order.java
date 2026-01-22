package com.code808.calmdesk.domain.member.admin.gifticonManage.entity;

import com.code808.calmdesk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import java.time.LocalDateTime;

@Entity
@Table(name = "GIFT_ORDER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gift_Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long id;

    @Column(name = "GIFTICON_ID", nullable = false)
    private Long gifticonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member; // 필드 타입을 숫자가 아닌 'Member' 객체로!

    @Column(name = "PERIOD")
    private Integer period;

    @Column(name = "ORDER_DATE")
    private LocalDateTime orderDate;

    @Column(name = "APPROVAL_AMOUNT")
    private Long approvalAmount;

    // 생성자 수정
    public Gift_Order(Long gifticonId, Member member, Integer period, Long approvalAmount) {
        this.gifticonId = gifticonId;
        this.member = member;
        this.period = period;
        this.orderDate = LocalDateTime.now();
        this.approvalAmount = approvalAmount;
    }
}