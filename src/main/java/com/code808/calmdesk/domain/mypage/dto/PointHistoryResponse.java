package com.code808.calmdesk.domain.mypage.dto;

import com.code808.calmdesk.domain.gifticon.entity.PointHistory;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "내역 ID", example = "200")
    private Long historyId;
    @Schema(description = "유형 (EARN: 적립, SPEND: 사용)", example = "EARN")
    private String type; // EARN, SPEND
    @Schema(description = "내역 명칭", example = "출석 체크 적립")
    private String title;
    @Schema(description = "변동 금액 (기호 포함)", example = "100")
    private Integer amount;
    @Schema(description = "변동 후 잔액", example = "5100")
    private Integer balanceAfter;
    @Schema(description = "일시 (yyyy.MM.dd HH:mm)", example = "2024.02.25 15:30")
    private String date;

    public static PointHistoryResponse from(PointHistory pointHistory) {
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
