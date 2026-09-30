package com.fooderp.backend.sales.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SalesOrderItemDto(
    UUID id,
    UUID productId,
    String productCode,
    String productName,
    String unit,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal subtotal,
    LocalDateTime createdAt
) {}
