package com.fooderp.backend.inventory.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateGoodsReceiptRequest(
    UUID purchaseOrderId,
    UUID warehouseId,
    String notes,
    String receivedBy,
    List<ItemRequest> items
) {
    public record ItemRequest(
        UUID productId,
        BigDecimal quantity,
        BigDecimal unitCost
    ) {}
}
