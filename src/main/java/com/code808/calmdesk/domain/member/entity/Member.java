package com.code808.calmdesk.domain.member.entity;

import com.code808.calmdesk.domain.common.BaseTimeEntity;
import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.company.entity.Department;
import jakarta.persistence.Entity;;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@AllArgsConstructor
@Getter
@Entity(name = "MEMBER")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    @Column(nullable = false, length = 30)
    private String  name;

    @Column(nullable = false, unique = true, length = 50)
    private String  email;

    @Column(nullable = false, length = 100)
    private String  password;

    @Column(nullable = false, unique = true, length = 30)
    private String  phone;

    @Column(nullable = false)
    private LocalDate registerDate;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 1, nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private CommonEnums.Status status = CommonEnums.Status.N;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RANK_ID")
    private Rank rank;

    public void updateCompanyInfo(
            Company company,
            Department department,
            Rank rank,
            Role role,
            CommonEnums.Status status
    ){
        this.company = company;
        this.department = department;
        this.rank = rank;
        this.role = role;
        this.status = status;
    }

    public enum Role{
        EMPLOYEE, ADMIN
    }
}
