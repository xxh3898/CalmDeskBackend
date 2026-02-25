package com.code808.calmdesk.domain.businesscard.entity;

import com.code808.calmdesk.domain.common.BaseTimeEntity;
import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.company.entity.Department;
import jakarta.persistence.*;
import lombok.*;

/**
 * 명함으로 등록한 연락처 (직원/외부인/협력사).
 * 프로젝트에 맞게 팀(부서) 선택 가능.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "BUSINESS_CARD_CONTACT", indexes = {
        @Index(name = "IDX_BC_COMPANY", columnList = "COMPANY_ID"),
        @Index(name = "IDX_BC_PHONE", columnList = "COMPANY_ID, PHONE"),
        @Index(name = "IDX_BC_EMAIL", columnList = "COMPANY_ID, EMAIL")
})
public class BusinessCardContact extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", nullable = false)
    private Company company;

    /** 팀(부서) 선택 - 직원/협력사 배치용 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DEPARTMENT_ID")
    private Department department;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ContactType contactType;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 200)
    private String companyName;

    @Column(length = 100)
    private String title;

    @Column(length = 50)
    private String phone;

    @Column(length = 50)
    private String mobile;

    @Column(length = 100)
    private String email;

    @Column(length = 500)
    private String address;

    @Column(length = 50)
    private String fax;

    @Column(length = 200)
    private String website;

    public enum ContactType {
        EMPLOYEE,   // 직원
        EXTERNAL,   // 외부인
        PARTNER     // 협력사
    }
}
