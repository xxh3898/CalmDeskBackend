package com.code808.calmdesk.domain.mypage.service.employee;

import com.code808.calmdesk.domain.mypage.dto.*;

import java.util.List;

public interface MyPageService {
    // 프로필 조회
    ProfileResponse getProfile(Long memberId);

    // 프로필 수정
    ProfileResponse updateProfile(Long memberId, ProfileUpdateRequest request);

    // 비밀번호 변경
    void changePassword(Long memberId, PasswordChangeRequest request);

    // 포인트 내역 조회
    List<PointHistoryResponse> getPointHistory(Long memberId);

    // 기프티콘 목록 조회
    List<CouponResponse> getCoupons(Long memberId);

    // 알림 목록 조회
    List<NotificationResponse> getNotifications(Long memberId);

    // 알림 읽음 처리
    void markNotificationAsRead(Long memberId, Long notificationId);
}
