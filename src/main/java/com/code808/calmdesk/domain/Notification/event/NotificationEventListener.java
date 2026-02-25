package com.code808.calmdesk.domain.Notification.event;

import com.code808.calmdesk.domain.Notification.dto.NotificationResponseDto;
import com.code808.calmdesk.domain.Notification.entitiy.Notification;
import com.code808.calmdesk.domain.Notification.repository.NotificationRepository;
import com.code808.calmdesk.domain.Notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW) // 새로운 트랜잭션 시작
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT) // 메인 트랜잭션 성공 시에만 실행
    public void handleNotification(NotificationEvent event) {
        // 1. 알림 데이터 생성 및 DB 저장
        Notification notification = Notification.builder()
                .memberId(event.memberId())
                .title(event.title())
                .content(event.content())
                .targetRole(event.targetRole())
                .redirectUrl(event.redirectUrl())
                .status("N")
                .build();

        // 커밋 이후이므로 별도의 트랜잭션 내에서 저장해야 함
        notificationRepository.save(notification);

        // 2. DTO 변환 및 SSE 전송
        NotificationResponseDto response = NotificationResponseDto.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .targetRole(notification.getTargetRole())
                .redirectUrl(event.redirectUrl())
                .status("N")
                .createDate(LocalDateTime.now())
                .build();

        // SSE 전송은 네트워크 작업이므로 트랜잭션 범위 밖에서 수행되는 것이 이상적
        notificationService.send(event.memberId(), response);
    }
}