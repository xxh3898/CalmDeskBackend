package com.code808.calmdesk.domain.gifticon.repository;

import com.code808.calmdesk.domain.gifticon.entity.Point_History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<Point_History, Long> {

    /** 최신순(같은 시각이면 id 큰 것 = 나중에 저장된 것 우선) */
    List<Point_History> findByMemberIdOrderByCreateDateDescIdDesc(Long memberId);
}
