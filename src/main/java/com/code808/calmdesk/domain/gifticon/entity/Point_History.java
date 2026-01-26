package com.code808.calmdesk.domain.gifticon.entity;

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
    private Long id;

    @Column(name = "POINT_TYPE", length = 20)
    private String pointType;

    @Column(name = "AMOUNT")
    private Long amount;

    @Column(name = "BALANCE_AFTER")
    private Long balanceAfter;

    @Column(name = "SOURCE_TYPE", length = 20)
    private String sourceType;

    @Column(name = "CREATE_DATE")
    private LocalDateTime createDate;

    @Column(name = "GIFTICON_ID")
    private Long gifticonId;

    @Column(name = "MEMBER_ID", nullable = false)
    private Long memberId;

    @Column(name = "MISSION_ID")
    private Long missionId;

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
