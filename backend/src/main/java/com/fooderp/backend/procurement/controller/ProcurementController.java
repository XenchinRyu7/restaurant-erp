package com.fooderp.backend.procurement.controller;

import com.fooderp.backend.procurement.dto.CreatePurchaseOrderRequest;
import com.fooderp.backend.procurement.dto.PurchaseOrderDto;
import com.fooderp.backend.procurement.service.ProcurementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/procurement")
@CrossOrigin(origins = "*")
public class ProcurementController {

    private final ProcurementService procurementService;

    public ProcurementController(ProcurementService procurementService) {
        this.procurementService = procurementService;
    }

    @GetMapping("/orders")
    public ResponseEntity<List<PurchaseOrderDto>> getAllOrders() {
        return ResponseEntity.ok(procurementService.getAllPurchaseOrders());
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<PurchaseOrderDto> getOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(procurementService.getPurchaseOrderById(id));
    }

    @PostMapping("/orders")
    public ResponseEntity<PurchaseOrderDto> createOrder(@RequestBody CreatePurchaseOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(procurementService.createPurchaseOrder(request));
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<PurchaseOrderDto> updateStatus(@PathVariable UUID id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(procurementService.updateStatus(id, status));
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        procurementService.deletePurchaseOrder(id);
        return ResponseEntity.noContent().build();
    }
}
