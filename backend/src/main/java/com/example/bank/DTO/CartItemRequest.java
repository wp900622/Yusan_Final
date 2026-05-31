package com.example.bank.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CartItemRequest {

    @NotBlank(message = "商品編號不能為空")
    private String productId;

    @Min(value = 1, message = "數量至少為 1")
    private Integer quantity = 1;
}
