package com.code808.calmdesk.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ACCOUNT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column(nullable = false)
    private Integer accountLeave; // 현재 잔액

    @Column(nullable = false)
    private Integer totalEarnedPoint;

    @Column(nullable = false)
    private Integer totalSpentPoint; // 누적 사용량

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member;


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