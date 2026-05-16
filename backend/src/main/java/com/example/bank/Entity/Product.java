package com.example.bank.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {
    
    @Id
    @Column(name = "product_id")
    @NotBlank(message = "商品編號不能為空")
    private String productId;

    @Column(name = "product_name")
    @NotBlank(message = "商品名稱不能為空")
    private String productName;

    @Min(value = 0, message = "價格不能小於 0")
    private Integer price;

    @Min(value = 0, message = "庫存不能小於 0")
    private Integer stock;

    // Getters and Setters...
}
