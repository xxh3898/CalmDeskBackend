package com.code808.calmdesk.domain.callrecord.controller;

import com.code808.calmdesk.domain.callrecord.dto.CallRecordDto;
import com.code808.calmdesk.domain.callrecord.service.CallRecordService;
import com.code808.calmdesk.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 통화 녹음 업로드 → STT → 욕설 집계.
 * 직원: 내 통화 / 전체 목록, 전화번호 검색.
 */
@RestController
@RequestMapping("/api/call-records")
@RequiredArgsConstructor
public class CallRecordController {

    private final CallRecordService callRecordService;

    /**
     * 통화 종료 후 녹음 파일 + 메타 업로드. (multipart: file + customerPhone, callStartedAt, callEndedAt)
     */
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Long>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("customerPhone") String customerPhone,
            @RequestParam("callStartedAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime callStartedAt,
            @RequestParam("callEndedAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime callEndedAt,
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
    @GetMapping
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<CallRecordDto.ListItem>>> list(
            @RequestParam(defaultValue = "my") String scope,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Principal principal) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(ApiResponse.success(callRecordService.list(principal.getName(), scope, pageable)));
    }

    /**
     * 전화번호 검색: 해당 고객의 통화 목록 (날짜별 욕설)
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<CallRecordDto.PhoneSearchItem>>> searchByPhone(
            @RequestParam("phone") String phone,
            Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(callRecordService.searchByPhone(principal.getName(), phone)));
    }

    /**
     * STT 재처리: 실패한 통화 기록의 STT를 다시 처리
     */
    @PostMapping("/{recordId}/reprocess")
    public ResponseEntity<ApiResponse<Boolean>> reprocessStt(
            @PathVariable Long recordId,
            Principal principal) {
        boolean success = callRecordService.reprocessStt(principal.getName(), recordId);
        return ResponseEntity.ok(ApiResponse.success(success));
    }
}
