package com.code808.calmdesk.domain.member.employee.myPage.service;

import com.code808.calmdesk.domain.member.employee.myPage.dto.*;
import com.example.demo.entity.Member;
import com.example.demo.entity.Notification;
import com.example.demo.entity.Order;
import com.example.demo.entity.PointHistory;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageServiceImpl implements MyPageService {

    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final OrderRepository orderRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public ProfileResponse getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        return ProfileResponse.from(member);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(Long memberId, ProfileUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 이메일 중복 체크 (자신 제외)
        if (request.getEmail() != null && !request.getEmail().equals(member.getEmail())) {
            if (memberRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
            member.setEmail(request.getEmail());
        }

        // 전화번호 중복 체크 (자신 제외)
        if (request.getPhone() != null && !request.getPhone().equals(member.getPhone())) {
            if (memberRepository.existsByPhone(request.getPhone())) {
                throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
            }
            member.setPhone(request.getPhone());
        }

        memberRepository.save(member);
        return ProfileResponse.from(member);
    }

    @Override
    @Transactional
    public void changePassword(Long memberId, PasswordChangeRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 현재 비밀번호 확인 (추후 BCrypt로 변경 예정)
        if (!member.getPassword().equals(request.getCurrentPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 설정
        member.setPassword(request.getNewPassword());
        memberRepository.save(member);
    }

    @Override
    public List<PointHistoryResponse> getPointHistory(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        List<PointHistory> histories = pointHistoryRepository.findByMember(member);
        return histories.stream()
                .map(PointHistoryResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<CouponResponse> getCoupons(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        List<Order> orders = orderRepository.findByMemberOrderByOrderDateDesc(member);
        return orders.stream()
                .map(CouponResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationResponse> getNotifications(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        List<Notification> notifications = notificationRepository.findByMemberOrderByNotificationIdDesc(member);
        return notifications.stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markNotificationAsRead(Long memberId, Long notificationId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("알림을 찾을 수 없습니다."));

        // 본인의 알림인지 확인
        if (!notification.getMember().getMemberId().equals(memberId)) {
            throw new IllegalArgumentException("권한이 없습니다.");
        }

        notification.setStatus(com.example.demo.enums.CommonEnums.Status.Y);
        notificationRepository.save(notification);
    }
}
