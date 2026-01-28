package com.code808.calmdesk.domain.gifticon.entity;

import com.code808.calmdesk.domain.enums.CommonEnums;
import com.code808.calmdesk.domain.member.entity.Member; // 사용자 엔티티 가정
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "USER_MISSION")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class MemberMission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userMissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mission_list_id", nullable = false)
    private MissionList missionList;

    @Enumerated(EnumType.STRING)
    @Column(length = 1, nullable = false)
    @Builder.Default
    private CommonEnums.Status status = CommonEnums.Status.N; // 완료 여부 (Y/N)

    @Column(nullable = false)
    @Builder.Default
    private int progressCount = 0;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime completedAt; // 미션 완료 시점 기록

    // 상태 변경 메서드
    public void complete() {
        this.status = CommonEnums.Status.Y;
    }


    // 1. 진행도 업데이트 (setter 대신 사용)
    public void updateProgress(int value, boolean isAccumulative) {
        if (isAccumulative) {
            this.progressCount += value;
        } else {
            this.progressCount = value;
        }
    }


    // MemberMission 엔티티 내부에 추가
    public void resetMission() {
        this.progressCount = 0;
        this.status = CommonEnums.Status.N;
        this.completedAt = null; // 완료 시간도 초기화
    }
}