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

    @Column(nullable = false)
    private Integer accountLeave; // 현재 잔액

    @Column(nullable = false)
    private Integer totalEarnedPoint;

    @Column(nullable = false)
    private Integer totalSpentPoint; // 누적 사용량

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


// [수정/추가] 포인트 적립 비즈니스 로직
public void deposit(int amount) {
    if (amount < 0) {
        throw new IllegalArgumentException("적립할 포인트는 0보다 커야 합니다.");
    }
    this.accountLeave += amount;        // 잔액 증가
    this.totalEarnedPoint += amount;   // 누적 획득량 증가
}

// [추가] 포인트 차감 비즈니스 로직
public void withdraw(int amount) {
    if (this.accountLeave < amount) {
        throw new RuntimeException("잔액이 부족합니다.");
    }
    this.accountLeave -= amount; // 잔액 차감
    this.totalSpentPoint += amount; // 누적 사용량 증가
}
}