package com.example.bank.Repository;

import com.example.bank.Entity.Product;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    // 呼叫資料庫中的新增商品 Procedure
    @Procedure(procedureName = "sp_insert_product")
    void insertProduct(
            @Param("p_id") String id,
            @Param("p_name") String name,
            @Param("p_price") Integer price,
            @Param("p_stock") Integer stock
    );
    @Query(value = "SELECT * FROM products WHERE stock > 0", nativeQuery = true)
    List<Product> findAllAvailableProducts();

    @Modifying
    @Query(value = "CALL sp_process_order_item(:oid, :pid, :qty)", nativeQuery = true)
    void spProcessOrderItem(
            @Param("oid") String oid,
            @Param("pid") String pid,
            @Param("qty") Integer qty
    );

}