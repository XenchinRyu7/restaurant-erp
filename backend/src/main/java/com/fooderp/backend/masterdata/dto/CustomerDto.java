package com.fooderp.backend.masterdata.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record CustomerDto(
    UUID id,
    String code,
    String name,
    String email,
    String phone,
    String address,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
