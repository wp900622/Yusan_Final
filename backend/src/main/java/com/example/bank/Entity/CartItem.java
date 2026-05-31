package com.example.bank.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_sn")
    private Long cartItemSn;

    // 對應 users.user_id (購物車的擁有者)
    @Column(name = "member_id", length = 50, nullable = false)
    private String memberId;

    // 對應 products.product_id
    @Column(name = "product_id", length = 50, nullable = false)
    private String productId;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;
}
