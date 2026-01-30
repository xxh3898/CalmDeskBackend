package com.code808.calmdesk.domain.mypage.dto;

import com.code808.calmdesk.domain.gifticon.entity.Point_History;
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

    public static PointHistoryResponse from(Point_History pointHistory) {
        String pointType = pointHistory.getPointType() != null ? pointHistory.getPointType() : "";
        String title = "EARN".equalsIgnoreCase(pointType) ? "포인트 적립" : "포인트 사용";
        if (pointHistory.getSourceType() != null && !pointHistory.getSourceType().isEmpty()) {
            title = pointHistory.getSourceType();
        }

        int rawAmount = pointHistory.getAmount() != null ? pointHistory.getAmount().intValue() : 0;
        int amountWithSign = "EARN".equalsIgnoreCase(pointType) ? Math.abs(rawAmount) : -Math.abs(rawAmount);

        return PointHistoryResponse.builder()
                .historyId(pointHistory.getId())
                .type(pointType)
                .title(title)
                .amount(amountWithSign)
                .balanceAfter(pointHistory.getBalanceAfter() != null ? pointHistory.getBalanceAfter().intValue() : 0)
                .date(pointHistory.getCreateDate() != null
                        ? pointHistory.getCreateDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
                        : "")
                .build();
    }
}
