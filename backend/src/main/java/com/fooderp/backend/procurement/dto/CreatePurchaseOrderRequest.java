package com.fooderp.backend.procurement.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreatePurchaseOrderRequest(
    UUID supplierId,
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
