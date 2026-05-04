package com.todoteg.controller;

import java.util.List;

import com.todoteg.dto.OrderStatusDistributionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.todoteg.dto.ReportSummaryDTO;
import com.todoteg.dto.SalesByDayDTO;
import com.todoteg.dto.TopProductDTO;
import com.todoteg.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * GET /admin/reports/summary
     * Devuelve KPIs: ingresos totales, órdenes totales, productos activos,
     * ingresos de hoy y órdenes de hoy.
     */
    @GetMapping("/summary")
    public ResponseEntity<ReportSummaryDTO> getSummary() {
        return ResponseEntity.ok(reportService.getSummary());
    }

    /**
     * GET /admin/reports/sales-by-day?days=30
     * Devuelve ventas agrupadas por día para la gráfica de línea/área.
     * Por defecto muestra los últimos 30 días.
     */
    @GetMapping("/sales-by-day")
    public ResponseEntity<List<SalesByDayDTO>> getSalesByDay(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(reportService.getSalesByDay(days));
    }

    /**
     * GET /admin/reports/top-products?limit=5
     * Devuelve los productos más vendidos por cantidad.
     * Por defecto devuelve el top 5.
     */
    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductDTO>> getTopProducts(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(reportService.getTopProducts(limit));
    }

    /**
     * GET /admin/reports/orders-by-status
     * Devuelve la distribución de órdenes por estado para la gráfica Donut.
     * Ejemplo de respuesta:
     * [
     *   { "status": "COMPLETED", "count": 280, "percentage": 81.87 },
     *   { "status": "PENDING",   "count": 45,  "percentage": 13.16 },
     *   { "status": "CANCELLED", "count": 17,  "percentage": 4.97  }
     * ]
     */
    @GetMapping("/orders-by-status")
    public ResponseEntity<List<OrderStatusDistributionDTO>> getOrderStatusDistribution() {
        return ResponseEntity.ok(reportService.getOrderStatusDistribution());
    }
}
