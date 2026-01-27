package com.code808.calmdesk.domain.mypage.service.employee;

import com.code808.calmdesk.domain.gifticon.entity.Order;
import com.code808.calmdesk.domain.gifticon.entity.Point_History;
import com.code808.calmdesk.domain.gifticon.repository.OrderRepository;
import com.code808.calmdesk.domain.gifticon.repository.PointHistoryRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import com.code808.calmdesk.domain.mypage.dto.*;
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

    @Override
    public ProfileResponse getProfile(Long memberId) {
        Member member = memberRepository.findByIdWithCompanyAndDepartmentAndRank(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
        return ProfileResponse.from(member);
    }

    @Override
    @Transactional
    public ProfileResponse updateProfile(Long memberId, ProfileUpdateRequest request) {
        Member member = memberRepository.findByIdWithCompanyAndDepartmentAndRank(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        if (request.getEmail() != null && !request.getEmail().equals(member.getEmail())) {
            if (memberRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
            }
            member.setEmail(request.getEmail());
        }

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

        if (!member.getPassword().equals(request.getCurrentPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        member.setPassword(request.getNewPassword());
        memberRepository.save(member);
    }

    @Override
    public List<PointHistoryResponse> getPointHistory(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        List<Point_History> histories = pointHistoryRepository.findByMemberIdOrderByCreateDateDesc(member.getId());
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
                .map(order -> CouponResponse.from(order, order.getGifticon()))
                .collect(Collectors.toList());
    }
}
