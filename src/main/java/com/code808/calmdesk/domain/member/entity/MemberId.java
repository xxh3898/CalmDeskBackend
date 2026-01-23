package com.code808.calmdesk.domain.member.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MEMBER")
@Getter
@Setter
@NoArgsConstructor
public class Member {
    @Id
    @Column(name = "MEMBER_ID")
    private Long id;
}
