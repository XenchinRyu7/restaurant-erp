package com.fooderp.backend.procurement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PurchaseOrderItemDto(
    UUID id,
    UUID productId,
    String productCode,
    String productName,
    String unit,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal receivedQuantity,
    BigDecimal subtotal,
    LocalDateTime createdAt
) {}
