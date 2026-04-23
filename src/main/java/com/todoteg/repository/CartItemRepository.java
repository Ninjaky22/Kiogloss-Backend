package com.todoteg.repository;

import com.todoteg.model.CartItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT c FROM CartItem c WHERE c.account.id = :accountId ORDER BY c.id ASC")
    List<CartItem> findByAccountId(@Param("accountId") Long accountId);

    @Query("SELECT c FROM CartItem c WHERE c.account.id = :accountId ORDER BY c.id ASC")
    Page<CartItem> findByAccountId(@Param("accountId") Long accountId, Pageable pageable);

    /**
     * Suma price * quantity de todos los ítems del carrito para una cuenta.
     * Devuelve 0 si el carrito está vacío.
     */
    @Query("SELECT COALESCE(SUM(c.variant.product.price * c.quantity), 0) FROM CartItem c WHERE c.account.id = :accountId")
    BigDecimal sumGrandTotalByAccountId(@Param("accountId") Long accountId);

    Optional<CartItem> findByAccountIdAndVariantId(Long accountId, Long variantId);
    void deleteByAccountId(Long accountId);
}
