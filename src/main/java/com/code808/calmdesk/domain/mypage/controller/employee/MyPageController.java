package com.code808.calmdesk.domain.mypage.controller.employee;

import com.code808.calmdesk.global.dto.ApiResponse;
import com.code808.calmdesk.domain.mypage.dto.*;
import com.code808.calmdesk.domain.mypage.service.MyPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MyPageController {

    private final MyPageService myPageService;

    // 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(@RequestParam Long memberId) {
        try {
            ProfileResponse response = myPageService.getProfile(memberId);
            return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 프로필 수정
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @RequestParam Long memberId,
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
    @PutMapping("/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestParam Long memberId,
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
    @GetMapping("/points")
    public ResponseEntity<ApiResponse<List<PointHistoryResponse>>> getPointHistory(@RequestParam Long memberId) {
        try {
            List<PointHistoryResponse> response = myPageService.getPointHistory(memberId);
            return ResponseEntity.ok(ApiResponse.success("포인트 내역 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 기프티콘 목록 조회
    @GetMapping("/coupons")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getCoupons(@RequestParam Long memberId) {
        try {
            List<CouponResponse> response = myPageService.getCoupons(memberId);
            return ResponseEntity.ok(ApiResponse.success("기프티콘 목록 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 스트레스 요약 조회
    @GetMapping("/stress")
    public ResponseEntity<ApiResponse<StressResponse>> getStressSummary(@RequestParam Long memberId) {
        try {
            StressResponse response = myPageService.getStressSummary(memberId);
            return ResponseEntity.ok(ApiResponse.success("스트레스 요약 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

}
