package com.example.demo.dto.mypage;

import com.example.demo.entity.Notification;
import com.example.demo.enums.CommonEnums;
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
        // Notification 엔티티에 BaseTimeEntity가 없으므로 임시 처리
        // 추후 Notification 엔티티에 createdDate 필드 추가 필요
        String date = "";
        String time = "";

        return NotificationResponse.builder()
                .notificationId(notification.getNotificationId())
                .title(notification.getTitle())
                .message(notification.getContent())
                .date(date)
                .time(time)
                .read(notification.getStatus() == CommonEnums.Status.Y)
                .build();
    }
}
