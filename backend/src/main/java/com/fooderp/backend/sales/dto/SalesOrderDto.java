package com.fooderp.backend.sales.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record SalesOrderDto(
    UUID id,
    String orderNumber,
    UUID customerId,
    String customerCode,
    String customerName,
    UUID warehouseId,
    String warehouseCode,
    String warehouseName,
    LocalDateTime orderDate,
    String status,
    BigDecimal totalAmount,
    String notes,
    String createdBy,
    List<SalesOrderItemDto> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
