package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

@Entity(name = "POINT_HISTORY")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PointHistory extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private Integer balanceAfter;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumns({
            @JoinColumn(name = "ORDER_MEMBER_ID", referencedColumnName = "MEMBER_ID"),
            @JoinColumn(name = "ORDER_GIFTICON_ID", referencedColumnName = "GIFTICON_ID")
    })
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "MISSION_ID", nullable = false)
    private MissionList missionList;

    public enum Type{
        EARN, SPEND
    }
}
