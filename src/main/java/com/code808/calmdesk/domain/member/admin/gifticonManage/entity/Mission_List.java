package com.code808.calmdesk.domain.member.admin.gifticonManage.entity;

import com.code808.calmdesk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "MISSION_LIST")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mission_List {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MISSION_ID")
    private Long id; // 미션 아이디 (NUMBER)

    @Column(name = "REWARD_NAME", nullable = false, length = 50)
    private String rewardName; // 리워드 규칙 이름 (VARCHAR2(50))

    @Column(name = "POINT_ACCOUNT")
    private Long pointAccount; // 지급 리워드 (NUMBER)

    @Column(name = "DESCRIPTION", length = 500)
    private String description; // 리워드 설명 (VARCHAR2(500))

    @Column(name = "IS_ACTIVE", columnDefinition = "CHAR(1) CHECK (IS_ACTIVE IN ('Y', 'N'))")
    private String isActive; // 활성화 여부 (CHAR 'Y' or 'N')

    // Member와의 연관관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}