package com.code808.calmdesk.domain.Notification.event;

import com.code808.calmdesk.domain.Notification.dto.NotificationResponseDto;
import com.code808.calmdesk.domain.Notification.entitiy.Notification;
import com.code808.calmdesk.domain.Notification.repository.NotificationRepository;
import com.code808.calmdesk.domain.Notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @EventListener
    public void handleNotification(NotificationEvent event) {
        // 1. DB 저장 (targetRole 필드 저장)
        Notification notification = Notification.builder()
                .memberId(event.memberId())
                .title(event.title())
                .content(event.content())
                .targetRole(event.targetRole()) // ⭐ 추가
                .status("N")
                .build();
        notificationRepository.save(notification);

        // 2. SSE 전송 (프론트엔드에서 구분할 수 있게 targetRole 포함)
        NotificationResponseDto response = NotificationResponseDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .targetRole(notification.getTargetRole()) // ⭐ 추가
                .status("N")
                .createDate(LocalDateTime.now())
                .build();

        notificationService.send(event.memberId(), response);
    }
}