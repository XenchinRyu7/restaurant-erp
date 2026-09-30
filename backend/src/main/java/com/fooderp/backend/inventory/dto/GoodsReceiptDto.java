package com.fooderp.backend.inventory.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GoodsReceiptDto(
    UUID id,
    String receiptNumber,
    UUID purchaseOrderId,
    String purchaseOrderNumber,
    UUID warehouseId,
    String warehouseCode,
    String warehouseName,
    LocalDateTime receiptDate,
    String status,
    String notes,
    String receivedBy,
    List<GoodsReceiptItemDto> items,
    LocalDateTime createdAt
) {}
