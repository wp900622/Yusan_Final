package com.example.bank.Service;

import com.example.bank.DTO.OrderDto;
import com.example.bank.DTO.OrderRequest;
import com.example.bank.Entity.UserEntity;
import com.example.bank.Repository.OrderRepository;
import com.example.bank.Repository.ProductRepository;
import com.example.bank.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 建立完整訂單：包含主檔、明細扣庫存、計算總額
     */
    @Transactional(rollbackFor = Exception.class)
    public String placeOrder(String username, OrderRequest request) {
        // 1. 由 username 換到 user_id (orders.member_id 對 users.user_id 有 FK)
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("使用者不存在: " + username));

        // 2. 生成訂單編號 (範例: Ms20250801...)
        String orderId = generateOrderNo();

        // 3. 建立訂單主檔 (透過 Stored Procedure 寫入；總價先給 0，待明細加完後再更新)
        orderRepository.spInsertOrderMain(orderId, user.getUserId(), 0, 0);

        // 4. 逐一處理購買品項 (呼叫 Stored Procedure)
        for (OrderDto item : request.getItems()) {
            // 這個 Procedure 內部會：檢查庫存 -> 鎖定行 -> 扣庫存 -> 寫入 order_details
            productRepository.spProcessOrderItem(
                    orderId,
                    item.getProductId(),
                    item.getQuantity()
            );
        }

        // 5. 計算並更新訂單總金額
        // 透過我們在 Repository 定義的 Native Query 重新計算 Sum
        orderRepository.updateOrderTotal(orderId);

        return orderId;
    }

    /**
     * 生成符合格式的訂單編號
     */
    private String generateOrderNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int randomPart = new Random().nextInt(900) + 100; // 3位隨機數
        return "Ms" + datePart + randomPart;
    }
}
