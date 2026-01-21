package com.example.demo.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@Entity(name = "DEPARTMENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long departmentId;

    @Column(nullable = false, length = 50)
    private String departmentName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID",nullable = false)
    private Company company;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Member> members = new ArrayList<>();
}
