package com.example.bank.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 購物車單項回傳格式：結合 cart_items 與 products 的欄位，
 * 對齊前端購物車所需 (productId / productName / price / stock / quantity / subtotal)。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private String productId;
    private String productName;
    private Integer price;
    private Integer stock;
    private Integer quantity;
    private Integer subtotal;
}
