package com.code808.calmdesk.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// import com.code808.calmdesk.domain.attendance.entity.StressSummary;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StressResponse {

    private Integer avgStress;       // 평균 스트레스 (0~100)
    private String level;            // LOW, NORMAL, HIGH, CRITICAL
    private String levelText;        // 한글 레벨명
    private String message;          // 상태 메시지
    private String description;      // 상세 설명
    private String period;           // 기간 (WEEKLY 등)

    /**
     * 스트레스 레벨별 응답 생성
     */
    // public static StressResponse from(StressSummary summary) {
    //    if (summary == null) {
    //        return createDefault();
    //    }
    //    // ...
    //    // return StressResponse.builder()...
    // }
    // 임시 메서드: StressSummary 없이 동작하도록
    public static StressResponse from(Object summary) {
        return createDefault();
    }

    /**
     * 데이터 없을 때 기본값
     */
    public static StressResponse createDefault() {
        return StressResponse.builder()
                .avgStress(0)
                .level("LOW")
                .levelText("안정")
                .message("데이터 수집 중")
                .description("아직 충분한 데이터가 수집되지 않았습니다. 근무 시작 후 스트레스 분석이 진행됩니다.")
                .period("WEEKLY")
                .build();
    }
}
