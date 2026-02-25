package com.code808.calmdesk.domain.businesscard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

/**
 * 명함 이미지에서 AI가 추출한 구조화 데이터. 필드 매핑 규칙: 명함 텍스트 → 이 DTO 필드로 매핑 (오류 시 null 또는 빈
 * 문자열).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BusinessCardExtractedDto {

    @Schema(description = "이름", example = "홍길동")
    private String name;           // 이름
    @Schema(description = "회사명", example = "코드808")
    private String company;        // 회사명
    @Schema(description = "부서", example = "개발팀")
    private String department;      // 부서
    @Schema(description = "직책/직급", example = "대리")
    private String title;          // 직책/직급
    @Schema(description = "전화번호", example = "02-123-4567")
    private String phone;          // 전화번호 (복수일 경우 하나로 합침 또는 대표 번호)
    @Schema(description = "휴대폰", example = "010-1234-5678")
    private String mobile;         // 휴대폰
    @Schema(description = "이메일", example = "hong@example.com")
    private String email;          // 이메일
    @Schema(description = "주소", example = "서울시 강남구...")
    private String address;        // 주소
    @Schema(description = "팩스", example = "02-123-4568")
    private String fax;            // 팩스
    @Schema(description = "웹사이트", example = "https://example.com")
    private String website;        // 웹사이트
    @Schema(description = "인식된 원문 라인들")
    @Builder.Default
    private List<String> rawLines = new ArrayList<>();  // 인식 실패 시 참고용 원문 라인

    /**
     * 추출 실패/오류 메시지 (정상이면 null)
     */
    @Schema(description = "추출 오류 메시지", example = "null")
    private String extractionError;
}
