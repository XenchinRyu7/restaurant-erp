package com.fooderp.backend.masterdata.repository;

import com.fooderp.backend.masterdata.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {
    Optional<Supplier> findByCode(String code);
    boolean existsByCode(String code);
    List<Supplier> findByStatus(String status);
}
