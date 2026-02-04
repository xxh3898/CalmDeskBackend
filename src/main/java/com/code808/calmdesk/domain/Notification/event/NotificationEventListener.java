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
        // 1. DB 저장
        Notification notification = Notification.builder()
                .memberId(event.memberId())
                .title(event.title())
                .content(event.content())
                .status("N") // DTO의 status 필드 대응
                .build();
        notificationRepository.save(notification);

        // 2. SSE 전송 (DTO 직접 생성)
        // 리액트에서 'message'로 쓸 데이터를 'content' 필드에 담아 보냅니다.
        NotificationResponseDto response = NotificationResponseDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .status("N")
                .createDate(LocalDateTime.now())
                .build();

        notificationService.send(event.memberId(), response);
    }
}