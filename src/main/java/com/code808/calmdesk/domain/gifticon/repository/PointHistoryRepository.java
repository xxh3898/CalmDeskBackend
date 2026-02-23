package com.code808.calmdesk.domain.gifticon.repository;

import com.code808.calmdesk.domain.gifticon.entity.PointHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

        List<PointHistory> findByMemberIdOrderByCreateDateDescIdDesc(Long memberId);

        Page<PointHistory> findByMemberIdOrderByCreateDateDescIdDesc(Long memberId, Pageable pageable);

        List<PointHistory> findByMember_Company_CompanyIdAndSourceTypeOrderByCreateDateDesc(
                        Long companyId,
                        String sourceType);

        Page<PointHistory> findByMember_Company_CompanyIdAndSourceType(
                        Long companyId, String sourceType, Pageable pageable);
}