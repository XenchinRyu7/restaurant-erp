package com.fooderp.backend.masterdata.repository;

import com.fooderp.backend.masterdata.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByCode(String code);
    boolean existsByCode(String code);
    List<Product> findByCategoryId(UUID categoryId);
    List<Product> findByStatus(String status);
}
