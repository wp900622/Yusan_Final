package com.example.bank.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
@Table(name="orders")
public class Order {
    @Id
    @Column(name = "order_id", length = 50)
    private String orderId;

    @Column(name = "member_id", length = 50, nullable = false)
    private String memberId;

    @Column(name = "total_price")
    private Integer totalPrice;

    // 設定預設值為 0 (對應圖片中的 integer = 0)
    @Column(name = "pay_status", columnDefinition = "integer default 0")
    private Integer payStatus = 0;
}
