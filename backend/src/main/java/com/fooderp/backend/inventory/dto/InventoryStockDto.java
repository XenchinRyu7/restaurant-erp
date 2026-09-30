package com.fooderp.backend.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryStockDto(
    UUID id,
    UUID productId,
    String productCode,
    String productName,
    String categoryName,
    String unit,
    UUID warehouseId,
    String warehouseCode,
    String warehouseName,
    BigDecimal quantity,
    Integer minStock,
    boolean isLowStock,
    LocalDateTime lastUpdatedAt
) {}
