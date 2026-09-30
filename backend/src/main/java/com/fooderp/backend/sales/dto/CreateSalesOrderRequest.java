package com.fooderp.backend.sales.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateSalesOrderRequest(
    UUID customerId,
    UUID warehouseId,
    String notes,
    String createdBy,
    List<ItemRequest> items
) {
    public record ItemRequest(
        UUID productId,
        BigDecimal quantity,
        BigDecimal unitPrice
    ) {}
}
