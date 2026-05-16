package com.example.bank.Repository;

import com.example.bank.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderRepository extends JpaRepository<Order, String> {
    // 訂單主檔新增：透過 Stored Procedure 寫入
    @Modifying
    @Query(value = "CALL sp_insert_order_main(:id, :member, :price, :status)", nativeQuery = true)
    void spInsertOrderMain(@Param("id") String id, @Param("member") String member, @Param("price") Integer price, @Param("status") Integer status);

    @Modifying
    @Query(value = "UPDATE orders SET total_price = (SELECT SUM(item_price) FROM order_details WHERE order_id = :id) WHERE order_id = :id", nativeQuery = true)
    void updateOrderTotal(@Param("id") String id);
}
