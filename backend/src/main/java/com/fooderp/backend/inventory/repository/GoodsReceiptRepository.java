package com.fooderp.backend.inventory.repository;

import com.fooderp.backend.inventory.entity.GoodsReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoodsReceiptRepository extends JpaRepository<GoodsReceipt, UUID> {
    Optional<GoodsReceipt> findByReceiptNumber(String receiptNumber);
    List<GoodsReceipt> findByPurchaseOrderId(UUID purchaseOrderId);
    List<GoodsReceipt> findByWarehouseId(UUID warehouseId);
    List<GoodsReceipt> findAllByOrderByCreatedAtDesc();
}
