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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "Gifticon Admin", description = "관리자용 기프티콘 샵 관리 API")
@RestController
@RequestMapping("/api/admin/shop")
@RequiredArgsConstructor
public class ShopAdminController {

    private final ShopAdminService shopAdminService;
    private final ShopEmployeeService shopEmployeeService;

    @Operation(summary = "아이템 목록 조회", description = "회사 ID로 필터링된 기프티콘 아이템 목록을 조회합니다.")
    @GetMapping("/items")
    public ResponseEntity<List<ItemResponse>> getItemsByCompany(
            @Parameter(description = "회사 ID", example = "1") @RequestParam("companyId") Long companyId) {
        // Service에서 회사 ID로 필터링된 목록을 가져옴
        List<ItemResponse> responses = shopAdminService.findAllByCompany(companyId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "아이템 활성 상태 토글", description = "특정 기프티콘 아이템의 활성/비활성 상태를 전환합니다.")
    @PatchMapping("/items/{id}/toggle")
    public ResponseEntity<?> toggleStatus(
            @Parameter(description = "아이템 ID", example = "10") @PathVariable Long id) {
        shopAdminService.toggleStatus(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "아이템 전체 활성화", description = "해당 회사의 모든 기프티콘 아이템을 활성화합니다.")
    @PostMapping("/items/activate-all")
    public ResponseEntity<Void> activateAll(
            @Parameter(description = "회사 ID", example = "1") @RequestParam Long companyId) {
        shopAdminService.activateAll(companyId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "아이템 전체 비활성화", description = "해당 회사의 모든 기프티콘 아이템을 비활성화합니다.")
    @PostMapping("/items/deactivate-all")
    public ResponseEntity<Void> deactivateAll(
            @Parameter(description = "회사 ID", example = "1") @RequestParam Long companyId) {
        shopAdminService.deactivateAll(companyId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "아이템 수량 업데이트", description = "특정 기프티콘 아이템의 재고 수량을 업데이트합니다.")
    @PutMapping("/items/{id}/{quantity}")
    public ResponseEntity<?> updateQuantity(
            @Parameter(description = "아이템 ID", example = "10") @PathVariable Long id,
            @Parameter(description = "수정할 수량", example = "50") @PathVariable Integer quantity) {
        shopAdminService.updateQuantity(id, quantity);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "전체 구매 내역 조회", description = "회사의 모든 직원들의 기프티콘 구매 내역을 페이징하여 조회합니다.")
    @GetMapping("/history/all")
    public ResponseEntity<Page<PurchaseHistoryResponse>> getAllPurchaseHistory(
            @Parameter(description = "회사 ID", example = "1") @RequestParam Long companyId,
            @PageableDefault(size = 6, sort = "createDate", direction = Sort.Direction.DESC) Pageable pageable) {
        // 모든 직원의 기프티콘 구매 내역을 가져옵니다.
        Page<PurchaseHistoryResponse> history = shopEmployeeService.getAllPurchaseHistory(companyId, pageable);
        return ResponseEntity.ok(history);
    }

}
