package com.fooderp.backend.inventory.repository;

import com.fooderp.backend.inventory.entity.InventoryStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryStockRepository extends JpaRepository<InventoryStock, UUID> {
    Optional<InventoryStock> findByProductIdAndWarehouseId(UUID productId, UUID warehouseId);
    List<InventoryStock> findByWarehouseId(UUID warehouseId);
    List<InventoryStock> findByProductId(UUID productId);

    @Query("SELECT s FROM InventoryStock s WHERE s.quantity <= s.product.minStock")
    List<InventoryStock> findLowStockItems();
}
