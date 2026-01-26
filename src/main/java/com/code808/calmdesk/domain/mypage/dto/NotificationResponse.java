package com.code808.calmdesk.domain.mypage.dto;

import com.code808.calmdesk.domain.notification.entity.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long notificationId;
    private String title;
    private String message;
    private String date;
    private String time;
    private Boolean read;

    public static NotificationResponse from(Notification notification) {
        String date = "";
        String time = "";
        if (notification.getCreatedDate() != null) {
            date = notification.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            time = notification.getCreatedDate().format(DateTimeFormatter.ofPattern("HH:mm"));
        }

        return NotificationResponse.builder()
                .notificationId(notification.getId())
                .title(notification.getTitle())
                .message(notification.getContent())
                .date(date)
                .time(time)
                .read(notification.getStatus() != null && "Y".equals(notification.getStatus().getCode()))
                .build();
    }
}
