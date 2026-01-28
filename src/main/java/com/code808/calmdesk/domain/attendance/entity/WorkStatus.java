package com.code808.calmdesk.domain.attendance.entity;

import com.code808.calmdesk.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "WORK_STATUS")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class WorkStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long workStatusId;

    @Column(nullable = false)
    private Integer status;

    @OneToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "MEMBER_ID", unique = true)
    private Member member;
}
