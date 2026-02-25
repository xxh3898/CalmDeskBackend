package com.code808.calmdesk.domain.mypage.controller.employee;

import com.code808.calmdesk.global.dto.ApiResponse;
import com.code808.calmdesk.domain.mypage.dto.*;
import com.code808.calmdesk.domain.mypage.service.MyPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "My Page", description = "사용자 마이페이지 API (프로필, 포인트, 기프티콘, 스트레스 요약)")
@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    // 프로필 조회
    @Operation(summary = "프로필 조회", description = "사용자의 기본 프로필 정보를 조회합니다.")
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
            @Parameter(description = "사용자 ID", example = "1") @RequestParam Long memberId) {
        try {
            ProfileResponse response = myPageService.getProfile(memberId);
            return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 프로필 수정
    @Operation(summary = "프로필 수정", description = "사용자의 프로필 정보(이름, 전화번호, 닉네임)를 수정합니다.")
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @Parameter(description = "사용자 ID", example = "1") @RequestParam Long memberId,
            @Valid @RequestBody ProfileUpdateRequest request) {
        try {
            ProfileResponse response = myPageService.updateProfile(memberId, request);
            return ResponseEntity.ok(ApiResponse.success("프로필 수정 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 비밀번호 변경
    @Operation(summary = "비밀번호 변경", description = "현재 비밀번호 확인 후 새 비밀번호로 변경합니다.")
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Parameter(description = "사용자 ID", example = "1") @RequestParam Long memberId,
            @Valid @RequestBody PasswordChangeRequest request) {
        try {
            myPageService.changePassword(memberId, request);
            return ResponseEntity.ok(ApiResponse.success("비밀번호 변경 성공", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 포인트 내역 조회
    @Operation(summary = "포인트 내역 조회", description = "사용자의 포인트 획득 및 사용 내역을 조회합니다.")
    @GetMapping("/points")
    public ResponseEntity<ApiResponse<Page<PointHistoryResponse>>> getPointHistory(
            @Parameter(description = "사용자 ID", example = "1") @RequestParam Long memberId,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size) {
        try {
            Page<PointHistoryResponse> response = myPageService.getPointHistory(
                    memberId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate", "id")));
            return ResponseEntity.ok(ApiResponse.success("포인트 내역 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 기프티콘 목록 조회
    @Operation(summary = "기프티콘 목록 조회", description = "사용자가 구매한 미사용 기프티콘 목록을 조회합니다.")
    @GetMapping("/coupons")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getCoupons(
            @Parameter(description = "사용자 ID", example = "1") @RequestParam Long memberId) {
        try {
            List<CouponResponse> response = myPageService.getCoupons(memberId);
            return ResponseEntity.ok(ApiResponse.success("기프티콘 목록 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 스트레스 요약 조회
    @Operation(summary = "스트레스 요약 조회", description = "사용자의 최근 스트레스 지수와 요약 정보를 조회합니다.")
    @GetMapping("/stress")
    public ResponseEntity<ApiResponse<StressResponse>> getStressSummary(
            @Parameter(description = "사용자 ID", example = "1") @RequestParam Long memberId) {
        try {
            StressResponse response = myPageService.getStressSummary(memberId);
            return ResponseEntity.ok(ApiResponse.success("스트레스 요약 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

}
