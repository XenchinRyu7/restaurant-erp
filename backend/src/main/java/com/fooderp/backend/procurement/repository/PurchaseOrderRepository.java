package com.fooderp.backend.procurement.repository;

import com.fooderp.backend.procurement.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, UUID> {
    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);
    List<PurchaseOrder> findBySupplierId(UUID supplierId);
    List<PurchaseOrder> findByStatus(String status);
    List<PurchaseOrder> findAllByOrderByCreatedAtDesc();
}
