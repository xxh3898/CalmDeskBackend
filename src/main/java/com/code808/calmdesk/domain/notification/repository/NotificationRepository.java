package com.code808.calmdesk.domain.notification.repository;

import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByMemberOrderByNotificationIdDesc(Member member);
}
