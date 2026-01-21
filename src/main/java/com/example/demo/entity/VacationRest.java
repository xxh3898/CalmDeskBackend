package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "VACATION_REST")
public class VacationRest {

    @Id
    private Long restId;

    @Column(nullable = false)
    private Integer spentCount;

    @Column(nullable = false)
    private Integer totalCount;

    @Column(nullable = false)
    private Integer levedCount;


    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "MEMBER_ID")
    private Member member;
}
