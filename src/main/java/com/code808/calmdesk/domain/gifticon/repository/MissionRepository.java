package com.code808.calmdesk.domain.gifticon.repository;

import com.code808.calmdesk.domain.gifticon.entity.MissionList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<MissionList, Long> {
    // 필요한 경우 상태가 'Y'인 활성 미션만 가져오는 메서드 추가 가능
    // List<MissionList> findByStatus(CommonEnums.Status status);
}