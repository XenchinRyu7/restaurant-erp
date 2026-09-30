package com.fooderp.backend.procurement.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PurchaseOrderDto(
    UUID id,
    String orderNumber,
    UUID supplierId,
    String supplierCode,
    String supplierName,
    LocalDateTime orderDate,
    String status,
    BigDecimal totalAmount,
    String notes,
    String createdBy,
    List<PurchaseOrderItemDto> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
