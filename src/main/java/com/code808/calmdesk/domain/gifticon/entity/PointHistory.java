package com.code808.calmdesk.domain.gifticon.entity;

import com.code808.calmdesk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import java.time.LocalDateTime;

@Entity
@Table(name = "POINT_HISTORY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POINT_HISTORY_ID")
    private Long id; // 포인트 내역 아이디

    @Column(name = "POINT_TYPE", length = 20)
    private String pointType; // 포인트 종류 ('EARN', 'SPEND')

    @Column(name = "AMOUNT")
    private Long amount; // 포인트 금액

    @Column(name = "BALANCE_AFTER")
    private Long balanceAfter; // 거래 후 잔액

    @Column(name = "SOURCE_TYPE", length = 20)
    private String sourceType; // 출처 유형 ('REWORD', 'GIFTICON')

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate; // 거래 발생 시각

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GIFTICON_ID")
    private Gifticon gifticon; // 기프티콘 아이디 (FK 역할)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID", nullable = false)
    private Member member; // 멤버 아이디 (FK 역할)

    @Column(name = "MISSION_ID")
    private Long missionId; // 미션 아이디 (FK 역할)

    // 생성자
    public PointHistory(String pointType, Long amount, Long balanceAfter,
            String sourceType, Member member, Gifticon gifticon, Long missionId) {
        this.pointType = pointType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.sourceType = sourceType;
        this.member = member;
        this.gifticon = gifticon;
        this.missionId = missionId;
        this.createDate = LocalDateTime.now();
    }
}