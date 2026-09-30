package com.fooderp.backend.masterdata.repository;

import com.fooderp.backend.masterdata.entity.ProductCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
    Optional<ProductCategory> findByCode(String code);
    boolean existsByCode(String code);
    List<ProductCategory> findByStatus(String status);
}
