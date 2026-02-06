package com.code808.calmdesk.domain.vacation.entity;

import com.code808.calmdesk.domain.common.BaseTimeEntity;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import com.code808.calmdesk.domain.common.converter.CommonStatusConverter;

import java.time.LocalDateTime;
@Entity
@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "VACATION")
public class Vacation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vacationId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Type type;

    @Column(nullable = false)
    private LocalDateTime startDate;

    @Column(nullable = false)
    private LocalDateTime endDate;

    @Column(nullable = false, length = 200)
    private String reason;

    /** Y=승인완료, N=승인대기, R=반려 (DB: Y/N/R 또는 APPROVED/PENDING/REJECTED 호환) */
    @Column(length = 10, nullable = false)
    @Convert(converter = CommonStatusConverter.class)
    @Builder.Default
    private CommonEnums.Status status = CommonEnums.Status.N;

    @Column(nullable = false)
    private Integer vacationDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID", nullable = false)
    private Member requestMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPROVE_ID", nullable = true)
    private Member approverMember;

    /**
     * 휴가 승인 처리
     */
    public void approve(Member approver) {
        this.status = CommonEnums.Status.Y;
        this.approverMember = approver;
    }

    /**
     * 휴가 반려 처리
     */
    public void reject() {
        this.status = CommonEnums.Status.R;
    }

    public enum Type{
        ANNUAL,    // 연차
        HALF,      // 반차
        WORKCATION // 워케이션 (원격근무)
    }
}
