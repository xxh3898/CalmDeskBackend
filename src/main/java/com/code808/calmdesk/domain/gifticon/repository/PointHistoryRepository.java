package com.code808.calmdesk.domain.gifticon.repository;

import com.code808.calmdesk.domain.gifticon.entity.PointHistory;
import com.code808.calmdesk.domain.gifticon.entity.Point_History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {

    List<Point_History> findByMemberIdOrderByCreateDateDesc(Long memberId);
}