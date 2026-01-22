package com.example.demo.repository;

import com.example.demo.entity.Notification;
import com.example.demo.repository.custom.NotificationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationRepositoryCustom {
}
