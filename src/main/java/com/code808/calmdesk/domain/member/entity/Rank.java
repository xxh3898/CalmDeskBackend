package com.code808.calmdesk.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity(name = "MEMBER_RANK")
public class Rank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rankId;

    @Column(nullable = false, length = 30)
    private String rankName;

    @OneToMany(mappedBy = "rank")
    private List<Member> members = new ArrayList<>();
}
