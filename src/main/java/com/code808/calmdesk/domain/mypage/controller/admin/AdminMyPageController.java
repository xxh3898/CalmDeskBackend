package com.code808.calmdesk.domain.mypage.controller.admin;

import com.code808.calmdesk.global.dto.ApiResponse;
import com.code808.calmdesk.domain.mypage.dto.*;
import com.code808.calmdesk.domain.mypage.service.MyPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "MyPage Admin", description = "관리자용 사용자 마이페이지 관리 API (프로필 조회/수정, 포인트 내역 등)")
@RestController
@RequestMapping("/api/admin/mypage")
@RequiredArgsConstructor
public class AdminMyPageController {

    private final MyPageService myPageService;

    // 프로필 조회
    @Operation(summary = "프로필 조회", description = "특정 멤버의 프로필 정보를 조회합니다.")
    @GetMapping("/{memberId}/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfile(
            @Parameter(description = "멤버 ID", example = "1") @PathVariable Long memberId) {
        try {
            ProfileResponse response = myPageService.getProfile(memberId);
            return ResponseEntity.ok(ApiResponse.success("프로필 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    // 프로필 수정
    @Operation(summary = "프로필 수정", description = "특정 멤버의 프로필 정보를 수정합니다.")
    @PutMapping("/{memberId}/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @Parameter(description = "멤버 ID", example = "1") @PathVariable Long memberId,
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
    @Operation(summary = "비밀번호 변경", description = "특정 멤버의 비밀번호를 변경합니다.")
    @PutMapping("/{memberId}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @Parameter(description = "멤버 ID", example = "1") @PathVariable Long memberId,
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
    @Operation(summary = "포인트 내역 조회", description = "특정 멤버의 포인트 획득/사용 내역을 페이징하여 조회합니다.")
    @GetMapping("/{memberId}/points")
    public ResponseEntity<ApiResponse<Page<PointHistoryResponse>>> getPointHistory(
            @Parameter(description = "멤버 ID", example = "1") @PathVariable Long memberId,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "10") @RequestParam(defaultValue = "10") int size) {
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
    @Operation(summary = "기프티콘 목록 조회", description = "특정 멤버가 보유한 기프티콘 목록을 조회합니다.")
    @GetMapping("/{memberId}/coupons")
    public ResponseEntity<ApiResponse<List<CouponResponse>>> getCoupons(
            @Parameter(description = "멤버 ID", example = "1") @PathVariable Long memberId) {
        try {
            List<CouponResponse> response = myPageService.getCoupons(memberId);
            return ResponseEntity.ok(ApiResponse.success("기프티콘 목록 조회 성공", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

}
