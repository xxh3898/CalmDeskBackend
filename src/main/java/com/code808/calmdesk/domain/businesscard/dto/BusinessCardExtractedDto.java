package com.code808.calmdesk.domain.businesscard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 명함 이미지에서 AI가 추출한 구조화 데이터.
 * 필드 매핑 규칙: 명함 텍스트 → 이 DTO 필드로 매핑 (오류 시 null 또는 빈 문자열).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BusinessCardExtractedDto {

    private String name;           // 이름
    private String company;        // 회사명
    private String department;      // 부서
    private String title;          // 직책/직급
    private String phone;          // 전화번호 (복수일 경우 하나로 합침 또는 대표 번호)
    private String mobile;         // 휴대폰
    private String email;          // 이메일
    private String address;        // 주소
    private String fax;            // 팩스
    private String website;        // 웹사이트
    @Builder.Default
    private List<String> rawLines = new ArrayList<>();  // 인식 실패 시 참고용 원문 라인

    /** 추출 실패/오류 메시지 (정상이면 null) */
    private String extractionError;
}
