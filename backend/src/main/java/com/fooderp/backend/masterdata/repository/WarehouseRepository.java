package com.fooderp.backend.masterdata.repository;

import com.fooderp.backend.masterdata.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, UUID> {
    Optional<Warehouse> findByCode(String code);
    boolean existsByCode(String code);
    List<Warehouse> findByStatus(String status);
}
