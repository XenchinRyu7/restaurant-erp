package com.fooderp.backend.inventory.repository;

import com.fooderp.backend.inventory.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, UUID> {
    List<InventoryTransaction> findAllByOrderByCreatedAtDesc();
    List<InventoryTransaction> findByProductId(UUID productId);
    List<InventoryTransaction> findByWarehouseId(UUID warehouseId);
}
