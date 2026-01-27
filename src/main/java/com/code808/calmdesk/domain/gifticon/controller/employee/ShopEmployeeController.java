package com.code808.calmdesk.domain.gifticon.controller.employee;

import lombok.RequiredArgsConstructor;

import com.code808.calmdesk.domain.gifticon.dto.PointMallResponse;
import com.code808.calmdesk.domain.gifticon.dto.PurchaseRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // 필수: @RestController, @PostMapping 등을 위함
import java.util.Map;
import com.code808.calmdesk.domain.gifticon.service.ShopEmployeeService;

@RestController
@RequestMapping("/api/employee/shop")
@RequiredArgsConstructor
public class ShopEmployeeController {

    private final ShopEmployeeService shopEmployeeService;

    // 포인트 몰 메인 데이터 조회 (포인트 + 미션 + 상점아이템)
    @GetMapping("/main/{userId}")
    public ResponseEntity<PointMallResponse> getPointMallMain(@PathVariable Long userId) {
        PointMallResponse response = shopEmployeeService.getPointMallData(userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/purchase")
    public ResponseEntity<?> purchaseItem(@RequestBody PurchaseRequest request) {
        try {
            Long orderId = shopEmployeeService.processPurchase(request);
            // 프론트엔드 response.data.orderId에 전달됨
            return ResponseEntity.ok(Map.of("orderId", orderId, "message", "구매 성공"));

        } catch (RuntimeException e) {
            // 포인트 부족 등 발생 시 400 에러와 함께 메시지 전달
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

}