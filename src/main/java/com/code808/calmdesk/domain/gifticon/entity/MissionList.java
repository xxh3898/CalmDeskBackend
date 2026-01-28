package com.code808.calmdesk.domain.gifticon.entity;

import com.code808.calmdesk.domain.common.enums.CommonEnums;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Entity(name = "MISSION_LIST")
@Table(name = "MISSION_LIST")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MissionList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long missionListId;

    @Column(nullable = false, length = 50)
    private String rewardName;

    @Column(nullable = false)
    private Integer pointAccount;

    @Column(nullable = false, length = 500)
    private String rewardDescription;

    @Column(length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CommonEnums.Status status = CommonEnums.Status.N;
}
