package com.code808.calmdesk.domain.Notification.repository;

import com.code808.calmdesk.domain.Notification.entitiy.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // 특정 회원의 알림 목록을 최신순으로 조회
    List<Notification> findAllByMemberIdOrderByCreateDateDesc(Long memberId);
    
    // 특정 회원의 읽지 않은 알림 개수 조회 (배지 카운트용)
    long countByMemberIdAndStatus(Long memberId, String status);
    
    // 특정 회원의 모든 알림 삭제 (선택 사항)
    void deleteAllByMemberId(Long memberId);

}