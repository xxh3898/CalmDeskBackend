package com.code808.calmdesk.domain.callrecord.repository;

import com.code808.calmdesk.domain.callrecord.entity.CallRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CallRecordRepository extends JpaRepository<CallRecord, Long> {

    /** 내 통화: 해당 직원이 받은 통화만, 최신순 */
    Page<CallRecord> findByEmployee_MemberIdOrderByCallStartedAtDesc(Long employeeId, Pageable pageable);

    /** 전체: 회사 전체 통화, 최신순 */
    Page<CallRecord> findByCompany_CompanyIdOrderByCallStartedAtDesc(Long companyId, Pageable pageable);

    /** 전화번호 검색: 해당 번호가 나온 모든 통화, 날짜순 */
    @Query("SELECT c FROM CallRecord c WHERE c.company.companyId = :companyId AND c.customerPhone = :phone ORDER BY c.callStartedAt DESC")
    List<CallRecord> findByCompanyIdAndCustomerPhoneOrderByCallStartedAtDesc(
            @Param("companyId") Long companyId,
            @Param("phone") String phone
    );
}
