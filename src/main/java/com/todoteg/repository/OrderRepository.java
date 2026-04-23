package com.todoteg.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.todoteg.model.Order;
import com.todoteg.model.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByAccountIdOrderByIdDesc(Long accountId);
    
    @Query("SELECT o FROM Order o WHERE o.account.id = :accountId AND " +
    		"(:status IS NULL OR o.status = :status) AND " +
           "(:startDate IS NULL OR o.date >= :startDate) AND " +
           "(:endDate IS NULL OR o.date <= :endDate) " +
           "ORDER BY o.id DESC")
    Page<Order> findByAccountWithFilters(
        @Param("accountId") Long accountId,
        @Param("status") OrderStatus status,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate,
        Pageable pageable
    );
    
    
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    
    Page<Order> findByAccount_User_Id(Long userId, Pageable pageable);
    
    Page<Order> findByStatusAndAccount_User_Id(OrderStatus status, Long userId, Pageable pageable);
    
    @Query("SELECT SUM(o.amount) FROM Order o")
    Double sumTotalAmount();
}