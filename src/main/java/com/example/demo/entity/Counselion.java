package com.example.demo.entity;

import com.example.demo.enums.CommonEnums;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "COUNSELION")
public class Counselion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long counselionId;

    @Column(length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CommonEnums.Status status = CommonEnums.Status.N;

    @Column(nullable = false,length = 30)
    private String title;

    @Column(nullable = false,length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID", nullable = false)
    private Member requestMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPROVE_ID", nullable = true)
    private Member approverMember;
}
