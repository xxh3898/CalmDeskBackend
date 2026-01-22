package com.example.demo.repository.impl;

import com.example.demo.entity.Member;
import com.example.demo.entity.Notification;
import com.example.demo.repository.custom.NotificationRepositoryCustom;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<Notification> findByMemberOrderByNotificationIdDesc(Member member) {
        String jpql = "SELECT n FROM NOTIFICATION n WHERE n.member = :member ORDER BY n.notificationId DESC";
        return entityManager.createQuery(jpql, Notification.class)
                .setParameter("member", member)
                .getResultList();
    }
}
