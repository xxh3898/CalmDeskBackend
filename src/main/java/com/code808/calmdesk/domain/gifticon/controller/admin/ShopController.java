package com.code808.calmdesk.domain.gifticon.controller.admin;

import com.code808.calmdesk.domain.gifticon.dto.ItemResponse;
import com.code808.calmdesk.domain.gifticon.dto.PurchaseRequest;
import com.code808.calmdesk.domain.gifticon.service.ShopService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/items")
    public ResponseEntity<List<ItemResponse>> getAllItems() {
        List<ItemResponse> items = shopService.findAllGifiticon()
                .stream()
                .map(ItemResponse::new)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseItem(@RequestBody PurchaseRequest request) {
        try {
            Long orderId = shopService.processPurchase(request);
            return ResponseEntity.ok(Map.of("orderId", orderId, "message", "구매 성공"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PatchMapping("/items/{id}/toggle")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id) {
        shopService.toggleActive(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/items/activate-all")
    public ResponseEntity<Void> activateAll() {
        shopService.activateAll();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/items/deactivate-all")
    public ResponseEntity<Void> deactivateAll() {
        shopService.deactivateAll();
        return ResponseEntity.ok().build();
    }

    @PutMapping("/items/{id}/{quantity}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long id, @PathVariable Integer quantity) {
        shopService.updateQuantity(id, quantity);
        return ResponseEntity.ok().build();
    }
}
