package com.code808.calmdesk.domain.member.admin.gifticonManage.repository;

import com.code808.calmdesk.domain.member.admin.gifticonManage.entity.Point_History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PointHistoryRepository extends JpaRepository<Point_History, Long> {
    // 특정 회원의 포인트 내역만 가져오는 기능 추가
    List<Point_History> findByMemberIdOrderByCreateDateDesc(Long memberId);
}