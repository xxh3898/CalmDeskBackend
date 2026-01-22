package com.code808.calmdesk.domain.member.admin.gifticonManage.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import java.time.LocalDateTime;

@Entity
@Table(name = "POINT_HISTORY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point_History {

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

    @Column(name = "GIFTICON_ID")
    private Long gifticonId; // 기프티콘 아이디 (FK 역할)

    @Column(name = "MEMBER_ID", nullable = false)
    private Long memberId; // 멤버 아이디 (FK 역할)

    @Column(name = "MISSION_ID")
    private Long missionId; // 미션 아이디 (FK 역할)

    // 생성자
    public Point_History(String pointType, Long amount, Long balanceAfter,
                         String sourceType, Long memberId, Long gifticonId, Long missionId) {
        this.pointType = pointType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.sourceType = sourceType;
        this.memberId = memberId;
        this.gifticonId = gifticonId;
        this.missionId = missionId;
        this.createDate = LocalDateTime.now();
    }
}