package com.fooderp.backend.masterdata.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ProductCategoryDto(
    UUID id,
    String code,
    String name,
    String description,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
