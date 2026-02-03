package com.code808.calmdesk.domain.mypage.controller.admin;

import com.code808.calmdesk.global.dto.ApiResponse;
import com.code808.calmdesk.domain.mypage.dto.*;
import com.code808.calmdesk.domain.mypage.service.MyPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/mypage")
@RequiredArgsConstructor
public class AdminMyPageController {

    private final MyPageService myPageService;

    // 프로필 조회
    @GetMapping("/{memberId}/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(@PathVariable Long memberId) {
        try {
            ProfileResponse response = myPageService.getProfile(memberId);
            return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 프로필 수정
    @PutMapping("/{memberId}/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @PathVariable Long memberId,
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
    @PutMapping("/{memberId}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long memberId,
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
    @GetMapping("/{memberId}/points")
    public ResponseEntity<ApiResponse<List<PointHistoryResponse>>> getPointHistory(@PathVariable Long memberId) {
        try {
            List<PointHistoryResponse> response = myPageService.getPointHistory(memberId);
            return ResponseEntity.ok(ApiResponse.success("포인트 내역 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 기프티콘 목록 조회
    @GetMapping("/{memberId}/coupons")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getCoupons(@PathVariable Long memberId) {
        try {
            List<CouponResponse> response = myPageService.getCoupons(memberId);
            return ResponseEntity.ok(ApiResponse.success("기프티콘 목록 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

}

