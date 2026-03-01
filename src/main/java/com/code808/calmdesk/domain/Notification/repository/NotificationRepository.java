package com.code808.calmdesk.domain.Notification.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.code808.calmdesk.domain.Notification.entitiy.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 특정 회원의 알림 목록을 최신순으로 조회
    Page<Notification> findAllByMemberIdOrderByCreateDateDesc(Long memberId, Pageable pageable);

    // 특정 회원의 읽지 않은 알림 개수 조회 (배지 카운트용)
    long countByMemberIdAndStatus(Long memberId, String status);

    // 특정 회원의 모든 알림 삭제 (선택 사항)
    void deleteAllByMemberId(Long memberId);

    // 전체 읽음 처리
    @Modifying
    @Query("UPDATE Notification n SET n.status = 'Y' WHERE n.memberId = :memberId AND n.status = 'N'")
    void markAllAsRead(@Param("memberId") Long memberId);
}
