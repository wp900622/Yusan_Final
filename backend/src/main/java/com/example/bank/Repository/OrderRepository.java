package com.example.bank.Repository;

import com.example.bank.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, String> {
    @Modifying
    @Query(value = "INSERT INTO orders (order_id, member_id, total_price, pay_status) VALUES (:id, :member, :price, :status)", nativeQuery = true)
    void insertOrderMain(@Param("id") String id, @Param("member") String member, @Param("price") Integer price, @Param("status") Integer status);

    @Modifying
    @Query(value = "UPDATE orders SET total_price = (SELECT SUM(item_price) FROM order_details WHERE order_id = :id) WHERE order_id = :id", nativeQuery = true)
    void updateOrderTotal(@Param("id") String id);
}
