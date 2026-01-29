package com.code808.calmdesk.domain.gifticon.controller.employee;

import com.code808.calmdesk.domain.gifticon.dto.MissionCompleteRequest;
import com.code808.calmdesk.domain.gifticon.dto.MissionResponse;
import lombok.RequiredArgsConstructor;

import com.code808.calmdesk.domain.gifticon.dto.PointMallResponse;
import com.code808.calmdesk.domain.gifticon.dto.PurchaseRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // 필수: @RestController, @PostMapping 등을 위함

import java.util.List;
import java.util.Map;
import com.code808.calmdesk.domain.gifticon.service.ShopEmployeeService;

@RestController
@RequestMapping("/api/employee/shop")
@RequiredArgsConstructor
public class ShopEmployeeController {

    private final ShopEmployeeService shopEmployeeService;


    // 포인트 몰 메인 데이터 조회 (포인트 + 미션 + 상점아이템)
    @GetMapping("/{userId}")
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


//        // 미션 목록 조회
//    @GetMapping("/mission/{userId}")
//    public ResponseEntity<List<MissionResponse>> getMissions(@PathVariable Long userId) {
//
//        // 서비스에서 getAllMissions(userId)로 수정되었으므로 인자 전달 필요
//        return ResponseEntity.ok(shopEmployeeService.getAllMissions(userId));
//    }

        // 미션 완료 및 보상 지급
        @PostMapping("/mission/complete")
        public ResponseEntity<?> completeMission(@RequestBody MissionCompleteRequest request) {
            try {
                // request에서 memberId와 missionId를 꺼내서 전달
                // 1. 진행도를 먼저 업데이트해서 목표치(1/1)를 채웁니다.
                shopEmployeeService.updateMissionProgress(request.getUserId(), "ATT_DAILY", 1, false);
                shopEmployeeService.updateMissionProgress(request.getUserId(), "ATT_RATE_80", 1, true);

                // 2. 그 다음에 보상을 지급합니다. (이제 1/1이므로 통과됨)
                shopEmployeeService.completeMission(request.getUserId(), request.getMissionId());


                return ResponseEntity.ok("미션 보상이 지급되었습니다.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            }
        }




    }

