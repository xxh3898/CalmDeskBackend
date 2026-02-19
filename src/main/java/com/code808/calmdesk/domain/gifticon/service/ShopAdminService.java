package com.code808.calmdesk.domain.gifticon.service;

import com.code808.calmdesk.domain.common.enums.CommonEnums;
import com.code808.calmdesk.domain.company.entity.Company;
import com.code808.calmdesk.domain.company.repository.CompanyRepository;
import com.code808.calmdesk.domain.gifticon.dto.PurchaseRequest;
import com.code808.calmdesk.domain.gifticon.dto.ItemResponse;
import com.code808.calmdesk.domain.gifticon.entity.CompanyGifticon;
import com.code808.calmdesk.domain.gifticon.entity.Order;
import com.code808.calmdesk.domain.gifticon.entity.Gifticon;
import com.code808.calmdesk.domain.gifticon.entity.PointHistory;
import com.code808.calmdesk.domain.gifticon.repository.CompanyGifticonRepository;
import com.code808.calmdesk.domain.gifticon.repository.OrderRepository;
import com.code808.calmdesk.domain.gifticon.repository.GifticonRepository;
import com.code808.calmdesk.domain.gifticon.repository.PointHistoryRepository;
import com.code808.calmdesk.domain.member.entity.Member;
import com.code808.calmdesk.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopAdminService {

    private final OrderRepository orderRepository;
    private final PointHistoryRepository pointHistoryRepository;
    private final GifticonRepository gifticonRepository;
    private final MemberRepository memberRepository;
    private final CompanyGifticonRepository companyGifticonRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public Long processPurchase(PurchaseRequest request) {
        // 1. 회원 및 회사별 아이템 조회
        Member member = memberRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("해당 회원을 찾을 수 없습니다."));

        // 💡 중요: 단순 Gifticon이 아니라, 이 회사의 CompanyGifticon을 조회해야 합니다.
        CompanyGifticon cg = companyGifticonRepository.findByCompany_CompanyIdAndGifticon_Id(
                        member.getCompany().getCompanyId(), request.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("해당 회사에서 판매 중인 아이템이 아닙니다."));

        if (!cg.getIsActive()) {
            throw new RuntimeException("현재 판매 중지된 상품입니다.");
        }
        // 2. 재고 확인 및 차감
        if (cg.getStockQuantity() <= 0) {
            throw new RuntimeException("재고가 부족하여 구매할 수 없습니다.");
        }
        cg.setStockQuantity(cg.getStockQuantity() - 1); // 재고 1 감소

        // 3. 포인트 잔액 확인 및 차감 (생략된 기존 로직 유지)
        long price = request.getPrice();
        Long balanceAfter = 95500L; // 실제 구현 시: member.getAccount().getBalance() - price;

        // 4. Order 및 PointHistory 저장 (Gifticon 마스터 정보 활용)
        Order order = Order.builder()
                .gifticon(cg.getGifticon()) // 마스터 정보 연결
                .member(member)
                .period(30)
                .approvalAmount((int) price)
                .spendPoint((int) price)
                .type(Order.Type.SPEND)
                .orderDate(java.time.LocalDate.now())
                .build();

        Order savedOrder = orderRepository.save(order);

        PointHistory history = new PointHistory(
                "SPEND", price, balanceAfter, "GIFTICON",
                member, cg.getGifticon(), null
        );
        pointHistoryRepository.save(history);

        return savedOrder.getOrderId();
    }

    @Transactional
    public List<ItemResponse> findAllByCompany(Long companyId) {
        // 1. 먼저 조회
        List<CompanyGifticon> items = companyGifticonRepository.findAllByCompany_CompanyId(companyId);

        // 2. 비어있다면 초기화 함수 실행 결과를 대입 (재대입 없이 깔끔하게 처리)
        if (items.isEmpty()) {
            items = initializeItemsForCompany(companyId);
        }

        return items.stream()
                .map(ItemResponse::new)
                .collect(Collectors.toList());
    }

    private List<CompanyGifticon> initializeItemsForCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("회사를 찾을 수 없습니다."));

        // 마스터에서 복사하여 저장 후 즉시 리스트로 반환
        return gifticonRepository.findAll().stream()
                .map(master -> CompanyGifticon.builder()
                        .company(company)
                        .gifticon(master)
                        .stockQuantity(100) // 초기 기본값
                        .isActive(true)
                        .build())
                .map(companyGifticonRepository::save)
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleStatus(Long id) { // 매개변수명을 id(CompanyGifticon의 PK)로 명확히 함
        // companyId가 아니라 전달받은 아이템의 고유 ID(PK)로 조회해야 합니다.
        CompanyGifticon cg = companyGifticonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템 설정을 찾을 수 없습니다."));

        // 상태 변경
        cg.toggleActive();

        // @Transactional이 걸려있으므로 메서드가 끝날 때 자동으로 DB에 반영(Flush)됩니다.
    }

    // 💡 일괄 처리를 마스터가 아닌 특정 회사의 데이터만 건드리도록 수정
    @Transactional
    public void activateAll(Long companyId) {
        List<CompanyGifticon> items = companyGifticonRepository.findAllByCompany_CompanyId(companyId);
        // 💡 setIsActive 대신 엔티티에 정의된 setActive 사용
        items.forEach(item -> item.setActive(true));
    }

    @Transactional
    public void deactivateAll(Long companyId) {
        List<CompanyGifticon> items = companyGifticonRepository.findAllByCompany_CompanyId(companyId);
        // 💡 setIsActive 대신 엔티티에 정의된 setActive 사용
        items.forEach(item -> item.setActive(false));
    }

    @Transactional
    public void updateQuantity(Long id, Integer quantity) {
        CompanyGifticon cg = companyGifticonRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템을 찾을 수 없습니다."));

        // 서비스에서 if문으로 체크하는 대신 엔티티 메서드 호출
        cg.updateStock(quantity);
    }
}