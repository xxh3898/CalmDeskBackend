package com.code808.calmdesk.domain.mypage.dto;

import com.code808.calmdesk.domain.gifticon.entity.PointHistory;
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
        String pointType = pointHistory.getPointType() != null ? pointHistory.getPointType() : "";
        String title = "EARN".equalsIgnoreCase(pointType) ? "포인트 적립" : "포인트 사용";
        if (pointHistory.getSourceType() != null && !pointHistory.getSourceType().isEmpty()) {
            title = pointHistory.getSourceType();
        }

        return PointHistoryResponse.builder()
                .historyId(pointHistory.getId())
                .type(pointType)
                .title(title)
                .amount(pointHistory.getAmount() != null ? pointHistory.getAmount().intValue() : 0)
                .balanceAfter(pointHistory.getBalanceAfter() != null ? pointHistory.getBalanceAfter().intValue() : 0)
                .date(pointHistory.getCreateDate() != null
                        ? pointHistory.getCreateDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
                        : "")
                .build();
    }
}
