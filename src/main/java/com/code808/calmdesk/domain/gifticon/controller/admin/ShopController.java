package com.code808.calmdesk.domain.gifticon.controller.admin;

import com.code808.calmdesk.domain.gifticon.dto.ItemResponse;
import lombok.RequiredArgsConstructor;
import com.code808.calmdesk.domain.gifticon.dto.PurchaseRequest;
import com.code808.calmdesk.domain.gifticon.service.ShopService;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class ShopController {

    private final ShopService shopService;

    // 1. 아이템 목록 조회
    @GetMapping("/items")
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        List<ItemResponse> items = shopService.findAllGifiticon()
                .stream()
                .map(ItemResponse::new) // Gifticon 엔티티를 ItemResponse DTO로 변환
                .toList();

        return ResponseEntity.ok(items);
    }

    // 2. 구매 처리 (핵심)
    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseItem(@RequestBody PurchaseRequest request) {
        try {
            Long orderId = shopService.processPurchase(request);
            // 프론트엔드 response.data.orderId에 맞게 반환
            return ResponseEntity.ok(Map.of("orderId", orderId, "message", "구매 성공"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 3. 아이템 활성 상태 토글
    @PatchMapping("/items/{id}/toggle")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id) {
        shopService.toggleActive(id);
        return ResponseEntity.ok().build();
    }

    // 아이템 전체 활성화
    @PostMapping("/items/activate-all")
    public ResponseEntity<Void> activateAll() {
        shopService.activateAll();
        return ResponseEntity.ok().build();
    }

    // 아이템 전체 비활성화
    @PostMapping("/items/deactivate-all")
    public ResponseEntity<Void> deactivateAll() {
        shopService.deactivateAll();
        return ResponseEntity.ok().build();
    }

    // 4. 수량 업데이트
    @PutMapping("/items/{id}/{quantity}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long id, @PathVariable Integer quantity) {
        shopService.updateQuantity(id, quantity);
        return ResponseEntity.ok().build();
    }
}