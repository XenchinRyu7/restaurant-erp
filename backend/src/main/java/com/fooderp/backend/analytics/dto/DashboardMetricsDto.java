package com.fooderp.backend.analytics.dto;

import com.fooderp.backend.inventory.dto.InventoryStockDto;
import com.fooderp.backend.inventory.dto.InventoryTransactionDto;
import com.fooderp.backend.procurement.dto.PurchaseOrderDto;
import com.fooderp.backend.sales.dto.SalesOrderDto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardMetricsDto(
    long totalProducts,
    long totalSuppliers,
    long totalCustomers,
    long totalWarehouses,
    long lowStockCount,
    long activePurchaseOrders,
    long completedSalesOrders,
    BigDecimal totalStockValuation,
    BigDecimal totalProcurementSpend,
    BigDecimal totalSalesRevenue,
    List<InventoryStockDto> lowStockAlerts,
    List<PurchaseOrderDto> recentPurchaseOrders,
    List<SalesOrderDto> recentSalesOrders,
    List<InventoryTransactionDto> recentTransactions
) {}
