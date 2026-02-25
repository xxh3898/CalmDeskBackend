package com.code808.calmdesk.domain.businesscard.service;

import com.code808.calmdesk.domain.businesscard.dto.BusinessCardExtractedDto;
import com.code808.calmdesk.domain.businesscard.dto.BusinessCardRegisterRequest;
import com.code808.calmdesk.domain.businesscard.entity.BusinessCardContact;

import java.util.List;

public interface BusinessCardService {

    /**
     * 명함 이미지에서 텍스트 추출 및 구조화 (AI 사용).
     * 프로바이더는 app.business-card.ai.provider 로 교체 가능 (gemini | ollama).
     */
    BusinessCardExtractedDto extractFromImage(byte[] imageBytes, String contentType);

    /**
     * 추출 결과를 직원/외부인/협력사로 등록.
     * - 중복: 같은 회사 내 동일 전화번호 또는 이메일이면 기존 연락처 업데이트
     * - 오류: 회사/부서 없음, 필수값(name) 없음 등 검증
     */
    BusinessCardContact register(String adminEmail, BusinessCardRegisterRequest request);

    List<BusinessCardContact> listByCompany(Long companyId);
}
