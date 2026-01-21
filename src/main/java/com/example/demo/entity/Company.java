package com.example.demo.entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Entity
@Table(name = "COMPANY")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;

    @Column(nullable = false, length = 50)
    private String companyName;

    @Column(nullable = false, unique = true, length = 50)
    private String companyCode;

    @Column(nullable = false, length = 50)
    private String category;

    @Column(nullable = false)
    private Integer minValue;

    @Column(nullable = false)
    private Integer maxValue;

    @OneToMany(mappedBy = "company" , cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Department> departments = new ArrayList<>();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL,  orphanRemoval = true)
    private List<Member> members = new ArrayList<>();
}
