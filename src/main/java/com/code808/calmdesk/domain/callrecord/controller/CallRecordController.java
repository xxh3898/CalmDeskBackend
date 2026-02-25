package com.code808.calmdesk.domain.callrecord.controller;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.code808.calmdesk.domain.callrecord.dto.CallRecordDto;
import com.code808.calmdesk.domain.callrecord.service.CallRecordService;
import com.code808.calmdesk.global.dto.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 통화 녹음 업로드 → STT → 욕설 집계. 직원: 내 통화 / 전체 목록, 전화번호 검색.
 */
@Tag(name = "Call Record", description = "전화 발신 기록 관리 및 STT 변환 API")
@RestController
@RequestMapping("/api/call-records")
@RequiredArgsConstructor
public class CallRecordController {

    private final CallRecordService callRecordService;

    /**
     * 통화 종료 후 녹음 파일 + 메타 업로드. (multipart: file + customerPhone, callStartedAt,
     * callEndedAt)
     */
    @Operation(summary = "통화 녹음 파일 업로드", description = "통화 녹음 파일과 관련 정보를 업로드하고 STT 처리를 시작합니다.")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Long>> upload(
            @Parameter(description = "통화 녹음 파일") @RequestParam("file") MultipartFile file,
            @Parameter(description = "고객 전화번호") @RequestParam("customerPhone") String customerPhone,
            @Parameter(description = "통화 시작 시간 (ISO 8601)") @RequestParam("callStartedAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime callStartedAt,
            @Parameter(description = "통화 종료 시간 (ISO 8601)") @RequestParam("callEndedAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime callEndedAt,
            Principal principal) {
        CallRecordDto.UploadRequest request = CallRecordDto.UploadRequest.builder()
                .customerPhone(customerPhone)
                .callStartedAt(callStartedAt)
                .callEndedAt(callEndedAt)
                .build();
        Long id = callRecordService.uploadAndProcess(principal.getName(), file, request);
        return ResponseEntity.ok(ApiResponse.success(id));
    }

    /**
     * 목록: scope=my | scope=all, page, size
     */
    @Operation(summary = "통화 기록 목록 조회", description = "조회 범위(my/all)와 페이징 정보를 사용하여 통화 기록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<CallRecordDto.ListItem>>> list(
            @Parameter(description = "조회 범위 (my: 내 기록, all: 전체 기록)") @RequestParam(defaultValue = "my") String scope,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "20") int size,
            Principal principal) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(callRecordService.list(principal.getName(), scope, pageable)));
    }

    /**
     * 전화번호 검색: 해당 고객의 통화 목록 (날짜별 욕설)
     */
    @Operation(summary = "전화번호 검색", description = "특정 고객의 전화번호를 기반으로 통화 기록을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CallRecordDto.PhoneSearchItem>>> searchByPhone(
            @Parameter(description = "검색할 전화번호") @RequestParam("phone") String phone,
            Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(callRecordService.searchByPhone(principal.getName(), phone)));
    }

    /**
     * STT 재처리: 실패한 통화 기록의 STT를 다시 처리
     */
    @Operation(summary = "STT 재처리", description = "실패한 통화 기록의 STT 변환을 다시 요청합니다.")
    @PostMapping("/{recordId}/reprocess")
    public ResponseEntity<ApiResponse<Boolean>> reprocessStt(
            @Parameter(description = "통화 기록 ID") @PathVariable Long recordId,
            Principal principal) {
        boolean success = callRecordService.reprocessStt(principal.getName(), recordId);
        return ResponseEntity.ok(ApiResponse.success(success));
    }
}
