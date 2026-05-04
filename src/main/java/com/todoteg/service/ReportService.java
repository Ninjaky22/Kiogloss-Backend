package com.todoteg.service;

import java.time.LocalDate;
import java.util.List;

import com.todoteg.dto.OrderStatusDistributionDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.todoteg.dto.ReportSummaryDTO;
import com.todoteg.dto.SalesByDayDTO;
import com.todoteg.dto.TopProductDTO;
import com.todoteg.repository.ReportRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    /**
     * Resumen general: KPIs principales del dashboard
     */
    public ReportSummaryDTO getSummary() {
        Double totalRevenue   = reportRepository.sumAllRevenue();
        Long totalOrders      = reportRepository.countAllOrders();
        Long activeProducts   = reportRepository.countActiveProducts();
        Double revenueToday   = reportRepository.sumRevenueToday(LocalDate.now());
        Long ordersToday      = reportRepository.countOrdersToday(LocalDate.now());

        return new ReportSummaryDTO(totalRevenue, totalOrders, activeProducts, revenueToday, ordersToday);
    }

    /**
     * Ventas agrupadas por día.
     * @param days cuántos días hacia atrás (ej: 7, 30)
     */
    public List<SalesByDayDTO> getSalesByDay(int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return reportRepository.findSalesByDay(startDate);
    }

    /**
     * Top N productos más vendidos por cantidad.
     * @param limit cuántos productos devolver (ej: 5)
     */
    public List<TopProductDTO> getTopProducts(int limit) {
        return reportRepository.findTopProducts(PageRequest.of(0, limit));
    }

    /**
     * Distribución de órdenes por estado para gráfica Donut.
     */
    public List<OrderStatusDistributionDTO> getOrderStatusDistribution() {
        List<OrderStatusDistributionDTO> list = reportRepository.findOrderStatusDistribution();
        long total = list.stream().mapToLong(OrderStatusDistributionDTO::getCount).sum();
        list.forEach(item -> {
            double pct = total > 0 ? Math.round((item.getCount() * 100.0 / total) * 100.0) / 100.0 : 0.0;
            item.setPercentage(pct);
        });
        return list;
    }
}
