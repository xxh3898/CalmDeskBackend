package com.code808.calmdesk.domain.common.converter;

import com.code808.calmdesk.domain.common.enums.CommonEnums;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * DB의 status 값(APPROVED, PENDING, REJECTED 또는 Y, N, R)을 CommonEnums.Status로 변환.
 * data.sql 등에서 APPROVED/PENDING/REJECTED를 사용한 기존 데이터 호환을 위한 변환기.
 */
@Converter(autoApply = false)
public class CommonStatusConverter implements AttributeConverter<CommonEnums.Status, String> {

    @Override
    public String convertToDatabaseColumn(CommonEnums.Status attribute) {
        if (attribute == null) return null;
        return attribute.getCode();
    }

    @Override
    public CommonEnums.Status convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) return CommonEnums.Status.N;
        return switch (dbData.toUpperCase()) {
            case "Y", "APPROVED" -> CommonEnums.Status.Y;
            case "N", "PENDING" -> CommonEnums.Status.N;
            case "R", "REJECTED" -> CommonEnums.Status.R;
            default -> throw new IllegalArgumentException(
                    "지원하지 않는 status 값: '" + dbData + "'. Y/N/R 또는 APPROVED/PENDING/REJECTED 사용.");
        };
    }
}
