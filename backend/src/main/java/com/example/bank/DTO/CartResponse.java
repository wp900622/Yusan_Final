package com.example.bank.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 整個購物車的回傳格式：明細 + 件數 + 總金額。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private List<CartItemResponse> items;
    private Integer count;        // 商品總件數 (數量加總)
    private Integer totalAmount;  // 訂單總金額
}
