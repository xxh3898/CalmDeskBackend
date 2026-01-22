package com.example.demo.dto.mypage;

import com.example.demo.entity.PointHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointHistoryResponse {
    private Long historyId;
    private String type; // EARN, SPEND
    private String title;
    private Integer amount;
    private Integer balanceAfter;
    private String date;

    public static PointHistoryResponse from(PointHistory pointHistory) {
        String title = "";
        if (pointHistory.getType() == PointHistory.Type.EARN) {
            if (pointHistory.getMissionList() != null) {
                title = pointHistory.getMissionList().getRewardName();
            }
        } else {
            if (pointHistory.getOrder() != null && pointHistory.getOrder().getGifticon() != null) {
                title = pointHistory.getOrder().getGifticon().getGifticonName() + " 교환";
            }
        }

        return PointHistoryResponse.builder()
                .historyId(pointHistory.getHistoryId())
                .type(pointHistory.getType().name())
                .title(title)
                .amount(pointHistory.getAmount())
                .balanceAfter(pointHistory.getBalanceAfter())
                .date(pointHistory.getCreatedDate() != null
                        ? pointHistory.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
                        : "")
                .build();
    }
}
