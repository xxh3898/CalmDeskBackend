package com.code808.calmdesk.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * 계좌 테이블 (멤버 도메인)
 * 회원별 잔여포인트·누적 획득/사용 포인트 보관 (Member 1:1)
 */
@Entity
@Table(name = "ACCOUNT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", unique = true, nullable = false)
    private Member member;

    @Column(name = "REMAINING_POINT", nullable = false)
    @Builder.Default
    @Setter
    private Long remainingPoint = 0L;

    @Column(name = "TOTAL_EARNED", nullable = false)
    @Builder.Default
    @Setter
    private Long totalEarned = 0L;

    @Column(name = "TOTAL_SPENT", nullable = false)
    @Builder.Default
    @Setter
    private Long totalSpent = 0L;
}
