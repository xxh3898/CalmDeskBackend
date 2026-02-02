package com.code808.calmdesk.domain.dashboard.dto.employee;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmotionCheckInRequest {

    private Integer stressLevel;
    private List<String> stressFactors;
    private String memo;
}
