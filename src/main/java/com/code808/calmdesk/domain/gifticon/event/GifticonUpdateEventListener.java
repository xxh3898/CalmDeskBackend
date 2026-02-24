package com.code808.calmdesk.domain.gifticon.event;

import com.code808.calmdesk.domain.gifticon.dto.ItemResponse;
import com.code808.calmdesk.domain.gifticon.entity.CompanyGifticon;
import com.code808.calmdesk.domain.gifticon.repository.CompanyGifticonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class GifticonUpdateEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final CompanyGifticonRepository companyGifticonRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleGifticonUpdate(GifticonUpdateEvent event) {
        Long companyId = event.companyId();
        log.info("📢 기프티콘 업데이트 이벤트 수신 (WebSocket - After Commit): {}", companyId);

        // 1. 해당 회사의 최신 기프티콘 목록 조회 (ID 순 정렬 보장 - UI 꼬임 방지)
        List<CompanyGifticon> companyItems = companyGifticonRepository
                .findAllByCompany_CompanyIdOrderByIdAsc(companyId);

        List<ItemResponse> items = companyItems.stream()
                .map(ItemResponse::fromCompanyEntity)
                .collect(Collectors.toList());

        // 2. WebSocket 브로드캐스트 전송
        // 구독 경로: /sub/shop/company/{companyId}
        messagingTemplate.convertAndSend("/sub/shop/company/" + companyId, items);
        log.info("📤 WebSocket 브로드캐스트 완료: /sub/shop/company/{}", companyId);
    }
}
