package com.fooderp.backend.sales.controller;

import com.fooderp.backend.sales.dto.CreateSalesOrderRequest;
import com.fooderp.backend.sales.dto.SalesOrderDto;
import com.fooderp.backend.sales.service.SalesService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/sales")
@CrossOrigin(origins = "*")
public class SalesController {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping("/orders")
    public ResponseEntity<List<SalesOrderDto>> getAllOrders() {
        return ResponseEntity.ok(salesService.getAllSalesOrders());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<SalesOrderDto> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(salesService.getSalesOrderById(id));
    }

    @PostMapping("/orders")
    public ResponseEntity<SalesOrderDto> createOrder(@RequestBody CreateSalesOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salesService.createSalesOrder(request));
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<SalesOrderDto> updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(salesService.updateStatus(id, status));
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        salesService.deleteSalesOrder(id);
        return ResponseEntity.noContent().build();
    }
}
