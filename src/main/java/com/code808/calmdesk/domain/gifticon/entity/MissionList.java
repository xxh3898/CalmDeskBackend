package com.code808.calmdesk.domain.gifticon.entity;

import com.code808.calmdesk.domain.common.enums.CommonEnums;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    // 추가: 미션 종류 식별용 (예: ATTENDANCE_DAILY, STRESS_LOW 등)
    @Column(nullable = false, unique = true)
    private String missionCode;

    // 추가: 목표 수치 (출근 미션은 1, 80% 출석은 16 등)
    @Column(nullable = false)
    private Integer goalCount;

    @Column(length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CommonEnums.Status status = CommonEnums.Status.N;

    @OneToMany(mappedBy = "missionList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "missionList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberMission> memberMissions = new ArrayList<>();
}
