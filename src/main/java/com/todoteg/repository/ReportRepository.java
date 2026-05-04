package com.todoteg.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.todoteg.dto.SalesByDayDTO;
import com.todoteg.dto.TopProductDTO;
import com.todoteg.model.DetailOrder;
import com.todoteg.dto.OrderStatusDistributionDTO;

public interface ReportRepository extends JpaRepository<DetailOrder, Long> {

    // Top productos más vendidos (por cantidad)
    @Query("SELECT new com.todoteg.dto.TopProductDTO(" +
            "d.variant.product.id, " +
            "d.variant.product.title, " +
            "SUM(d.quantity), " +
            "SUM(d.price * d.quantity)) " +
            "FROM DetailOrder d " +
            "GROUP BY d.variant.product.id, d.variant.product.title " +
            "ORDER BY SUM(d.quantity) DESC")
    List<TopProductDTO> findTopProducts(Pageable pageable);

    // Ventas agrupadas por día (últimos N días)
    @Query("SELECT new com.todoteg.dto.SalesByDayDTO(" +
            "o.date, SUM(o.amount), COUNT(o)) " +
            "FROM Order o " +
            "WHERE o.date >= :startDate " +
            "GROUP BY o.date " +
            "ORDER BY o.date ASC")
    List<SalesByDayDTO> findSalesByDay(@Param("startDate") LocalDate startDate);

    // Total de ingresos de hoy
    @Query("SELECT COALESCE(SUM(o.amount), 0.0) FROM Order o WHERE o.date = :today")
    Double sumRevenueToday(@Param("today") LocalDate today);

    // Total de órdenes de hoy
    @Query("SELECT COUNT(o) FROM Order o WHERE o.date = :today")
    Long countOrdersToday(@Param("today") LocalDate today);

    // Total de productos con status 'published'
    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = 'published'")
    Long countActiveProducts();

    // Total de todas las órdenes
    @Query("SELECT COUNT(o) FROM Order o")
    Long countAllOrders();

    // Total de ingresos general
    @Query("SELECT COALESCE(SUM(o.amount), 0.0) FROM Order o")
    Double sumAllRevenue();

    // Distribución de órdenes por estado con porcentaje
    @Query("SELECT new com.todoteg.dto.OrderStatusDistributionDTO(" +
            "o.status, " +
            "COUNT(o)) " +
            "FROM Order o " +
            "GROUP BY o.status")
    List<OrderStatusDistributionDTO> findOrderStatusDistribution();
}
