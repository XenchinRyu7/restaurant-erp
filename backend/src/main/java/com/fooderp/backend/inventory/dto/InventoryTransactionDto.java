package com.fooderp.backend.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryTransactionDto(
    UUID id,
    UUID productId,
    String productCode,
    String productName,
    UUID warehouseId,
    String warehouseCode,
    String warehouseName,
    String transactionType,
    BigDecimal quantity,
    String referenceNumber,
    String notes,
    LocalDateTime createdAt
) {}
