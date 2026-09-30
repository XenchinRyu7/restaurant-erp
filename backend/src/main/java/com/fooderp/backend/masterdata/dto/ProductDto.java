package com.fooderp.backend.masterdata.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductDto(
    UUID id,
    String code,
    String name,
    UUID categoryId,
    String categoryName,
    String unit,
    BigDecimal purchasePrice,
    BigDecimal sellingPrice,
    Integer minStock,
    String description,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
