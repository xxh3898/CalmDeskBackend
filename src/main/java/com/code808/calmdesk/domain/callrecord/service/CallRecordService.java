package com.code808.calmdesk.domain.callrecord.service;

import com.code808.calmdesk.domain.callrecord.dto.CallRecordDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CallRecordService {

    /**
     * 통화 종료 후 녹음 파일 + 메타데이터 저장 → STT → 욕설 집계 → 기록 완료.
     * @param employeeEmail 로그인한 직원 이메일
     * @param file 고객 음성 녹음 (webm 등)
     * @param request customerPhone, callStartedAt, callEndedAt
     * @return 저장된 CallRecord id
     */
    Long uploadAndProcess(String employeeEmail, MultipartFile file, CallRecordDto.UploadRequest request);

    /**
     * 직원용 목록: scope=my (내 통화) | scope=all (회사 전체)
     */
    Page<CallRecordDto.ListItem> list(String employeeEmail, String scope, Pageable pageable);

    /**
     * 전화번호 검색: 해당 고객(전화번호)의 통화 목록 (날짜별 욕설)
     */
    List<CallRecordDto.PhoneSearchItem> searchByPhone(String employeeEmail, String phone);

    /**
     * STT 재처리: 실패한 통화 기록의 STT를 다시 처리
     * @param employeeEmail 로그인한 직원 이메일
     * @param recordId 재처리할 통화 기록 ID
     * @return 처리 완료 여부
     */
    boolean reprocessStt(String employeeEmail, Long recordId);
}
