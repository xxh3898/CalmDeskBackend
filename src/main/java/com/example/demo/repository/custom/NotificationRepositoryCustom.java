package com.example.demo.repository.custom;

import com.example.demo.entity.Member;
import com.example.demo.entity.Notification;

import java.util.List;

public interface NotificationRepositoryCustom {
    List<Notification> findByMemberOrderByNotificationIdDesc(Member member);
}
