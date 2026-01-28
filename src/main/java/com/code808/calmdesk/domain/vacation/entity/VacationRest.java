package com.code808.calmdesk.domain.vacation.entity;

import com.code808.calmdesk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "VACATION_REST")
public class VacationRest {

    @Id
    private Long restId;

    @Column(nullable = false)
    private Integer spentCount;

    @Column(nullable = false)
    private Integer totalCount;

    @OneToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "MEMBER_ID", unique = true)
    private Member member;

    /**
     * 사용한 연차 일수 추가
     */
    public void addSpentCount(int days) {
        this.spentCount += days;
    }
}
