package com.fooderp.backend.sales.repository;

import com.fooderp.backend.sales.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {
    Optional<SalesOrder> findByOrderNumber(String orderNumber);
    List<SalesOrder> findByCustomerId(UUID customerId);
    List<SalesOrder> findByStatus(String status);
    List<SalesOrder> findAllByOrderByCreatedAtDesc();
}
