package com.todoteg.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.todoteg.model.DetailOrder;

public interface DetailOrderRepository extends JpaRepository<DetailOrder, Long> {
    List<DetailOrder> findByOrderId(Long orderId);
}
