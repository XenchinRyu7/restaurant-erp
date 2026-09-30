package com.fooderp.backend.inventory.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record GoodsReceiptItemDto(
    UUID id,
    UUID productId,
    String productCode,
    String productName,
    String unit,
    BigDecimal quantity,
    BigDecimal unitCost,
    LocalDateTime createdAt
) {}
