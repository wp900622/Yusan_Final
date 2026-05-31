package com.example.bank.Repository;

import com.example.bank.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByMemberId(String memberId);

    Optional<CartItem> findByMemberIdAndProductId(String memberId, String productId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.memberId = :memberId AND c.productId = :productId")
    void deleteByMemberIdAndProductId(@Param("memberId") String memberId, @Param("productId") String productId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.memberId = :memberId")
    void deleteByMemberId(@Param("memberId") String memberId);
}
