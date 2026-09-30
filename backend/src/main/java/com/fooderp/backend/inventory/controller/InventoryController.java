package com.fooderp.backend.inventory.controller;

import com.fooderp.backend.inventory.dto.*;
import com.fooderp.backend.inventory.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // ================= RECEIPTS =================
    @GetMapping("/receipts")
    public ResponseEntity<List<GoodsReceiptDto>> getAllReceipts() {
        return ResponseEntity.ok(inventoryService.getAllGoodsReceipts());
    }

    @GetMapping("/receipts/{id}")
    public ResponseEntity<GoodsReceiptDto> getReceiptById(@PathVariable UUID id) {
        return ResponseEntity.ok(inventoryService.getGoodsReceiptById(id));
    }

    @PostMapping("/receipts")
    public ResponseEntity<GoodsReceiptDto> createReceipt(@RequestBody CreateGoodsReceiptRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.createGoodsReceipt(request));
    }

    // ================= STOCKS =================
    @GetMapping("/stocks")
    public ResponseEntity<List<InventoryStockDto>> getAllStocks(
            @RequestParam(required = false) UUID warehouseId) {
        if (warehouseId != null) {
            return ResponseEntity.ok(inventoryService.getStocksByWarehouse(warehouseId));
        }
        return ResponseEntity.ok(inventoryService.getAllStocks());
    }

    @GetMapping("/stocks/low")
    public ResponseEntity<List<InventoryStockDto>> getLowStockAlerts() {
        return ResponseEntity.ok(inventoryService.getLowStockAlerts());
    }

    @PostMapping("/stocks/adjust")
    public ResponseEntity<InventoryStockDto> adjustStock(@RequestBody Map<String, Object> body) {
        UUID productId = UUID.fromString((String) body.get("productId"));
        UUID warehouseId = UUID.fromString((String) body.get("warehouseId"));
        BigDecimal quantity = new BigDecimal(body.get("quantity").toString());
        String notes = (String) body.getOrDefault("notes", "Manual adjustment");
        return ResponseEntity.ok(inventoryService.adjustStock(productId, warehouseId, quantity, notes));
    }

    // ================= TRANSACTIONS =================
    @GetMapping("/transactions")
    public ResponseEntity<List<InventoryTransactionDto>> getAllTransactions() {
        return ResponseEntity.ok(inventoryService.getAllTransactions());
    }
}
