package com.fooderp.backend.masterdata.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record WarehouseDto(
    UUID id,
    String code,
    String name,
    String address,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
