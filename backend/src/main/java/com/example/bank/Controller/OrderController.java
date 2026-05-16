package com.example.bank.Controller;

import com.example.bank.DTO.OrderRequest;
import com.example.bank.Service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 新增訂單 API
     * POST /api/orders
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderRequest request) {
        try {
            // 呼叫 Service 執行下單邏輯 (包含 Transaction 與 Stored Procedure)
            String orderId = orderService.placeOrder(request);

            // 下單成功，回傳訂單編號與 201 Created 狀態碼
            return ResponseEntity.status(HttpStatus.CREATED).body("訂單建立成功！編號為: " + orderId);

        } catch (RuntimeException e) {
            // 捕捉 Service 拋出的異常 (如：庫存不足、資料錯誤)
            // 這裡回傳 400 Bad Request 並帶上錯誤訊息
            return ResponseEntity.badRequest().body("建立訂單失敗: " + e.getMessage());

        } catch (Exception e) {
            // 捕捉未預期的伺服器錯誤
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("系統發生錯誤，請稍後再試");
        }
    }
}
