package com.example.bank.Service;

import com.example.bank.DTO.OrderDto;
import com.example.bank.DTO.OrderRequest;
import com.example.bank.Entity.Order;
import com.example.bank.Repository.OrderRepository;
import com.example.bank.Repository.ProductRepository;
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

    /**
     * 建立完整訂單：包含主檔、明細扣庫存、計算總額
     */
    @Transactional(rollbackFor = Exception.class)
    public String placeOrder(OrderRequest request) {
        // 1. 生成訂單編號 (範例: Ms20250801...)
        String orderId = generateOrderNo();

        // 2. 建立訂單主檔 (先給予總價 0，待明細新增完後更新)
        Order newOrder = new Order();
        newOrder.setOrderId(orderId);
        newOrder.setMemberId(request.getMemberId());
        newOrder.setTotalPrice(0);
        newOrder.setPayStatus(0); // 預設未付款
        orderRepository.save(newOrder);

        // 3. 逐一處理購買品項 (呼叫 Stored Procedure)
        for (OrderDto item : request.getItems()) {
            // 這個 Procedure 內部會：檢查庫存 -> 鎖定行 -> 扣庫存 -> 寫入 order_details
            productRepository.spProcessOrderItem(
                    orderId,
                    item.getProductId(),
                    item.getQuantity()
            );
        }

        // 4. 計算並更新訂單總金額
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
