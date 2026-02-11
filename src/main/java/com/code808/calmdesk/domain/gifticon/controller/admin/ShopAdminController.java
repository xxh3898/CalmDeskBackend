package com.code808.calmdesk.domain.gifticon.controller.admin;

import com.code808.calmdesk.domain.gifticon.dto.ItemResponse;
import com.code808.calmdesk.domain.gifticon.dto.PurchaseHistoryResponse;
import com.code808.calmdesk.domain.gifticon.service.ShopEmployeeService;
import lombok.RequiredArgsConstructor;
import com.code808.calmdesk.domain.gifticon.service.ShopAdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/shop")
@RequiredArgsConstructor
public class ShopAdminController {

    private final ShopAdminService shopAdminService;
    private final ShopEmployeeService shopEmployeeService;

    // 1. 아이템 목록 조회
    @GetMapping("/items")
    public ResponseEntity<List<ItemResponse>> getItemsByCompany(@RequestParam("companyId") Long companyId) {
        // Service에서 회사 ID로 필터링된 목록을 가져옴
        List<ItemResponse> responses = shopAdminService.findAllByCompany(companyId);
        return ResponseEntity.ok(responses);
    }

    // 3. 아이템 활성 상태 토글
    @PatchMapping("/items/{id}/toggle")
    public ResponseEntity<?> toggleStatus(@PathVariable Long id) {
        shopAdminService.toggleStatus(id);
        return ResponseEntity.ok().build();
    }

    // 아이템 전체 활성화
    @PostMapping("/items/activate-all")
    public ResponseEntity<Void> activateAll(@RequestParam Long companyId) {
        shopAdminService.activateAll(companyId);
        return ResponseEntity.ok().build();
    }

    // 아이템 전체 비활성화
    @PostMapping("/items/deactivate-all")
    public ResponseEntity<Void> deactivateAll(@RequestParam Long companyId) {
        shopAdminService.deactivateAll(companyId);
        return ResponseEntity.ok().build();
    }

    // 4. 수량 업데이트
    @PutMapping("/items/{id}/{quantity}")
    public ResponseEntity<?> updateQuantity(@PathVariable Long id, @PathVariable Integer quantity) {
        shopAdminService.updateQuantity(id, quantity);
        return ResponseEntity.ok().build();
    }


    // 모든 사용자의 전체 구매 내역 조회
    @GetMapping("/history/all")
    public ResponseEntity<Page<PurchaseHistoryResponse>> getAllPurchaseHistory(@RequestParam Long companyId,
                                                                               @PageableDefault(size = 6, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) {
        // 모든 직원의 기프티콘 구매 내역을 가져옵니다.
        Page<PurchaseHistoryResponse> history = shopEmployeeService.getAllPurchaseHistory(companyId, pageable);
        return ResponseEntity.ok(history);
    }

}