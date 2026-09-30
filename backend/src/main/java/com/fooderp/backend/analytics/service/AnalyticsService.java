package com.fooderp.backend.analytics.service;

import com.fooderp.backend.analytics.dto.DashboardMetricsDto;
import com.fooderp.backend.inventory.dto.InventoryStockDto;
import com.fooderp.backend.inventory.service.InventoryService;
import com.fooderp.backend.masterdata.repository.*;
import com.fooderp.backend.procurement.dto.PurchaseOrderDto;
import com.fooderp.backend.procurement.service.ProcurementService;
import com.fooderp.backend.sales.dto.SalesOrderDto;
import com.fooderp.backend.sales.service.SalesService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class AnalyticsService {

    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProcurementService procurementService;
    private final InventoryService inventoryService;
    private final SalesService salesService;

    public AnalyticsService(
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            CustomerRepository customerRepository,
            WarehouseRepository warehouseRepository,
            ProcurementService procurementService,
            InventoryService inventoryService,
            SalesService salesService) {
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.customerRepository = customerRepository;
        this.warehouseRepository = warehouseRepository;
        this.procurementService = procurementService;
        this.inventoryService = inventoryService;
        this.salesService = salesService;
    }

    public DashboardMetricsDto getDashboardMetrics() {
        long totalProducts = productRepository.count();
        long totalSuppliers = supplierRepository.count();
        long totalCustomers = customerRepository.count();
        long totalWarehouses = warehouseRepository.count();

        List<InventoryStockDto> stocks = inventoryService.getAllStocks();
        List<InventoryStockDto> lowStockAlerts = inventoryService.getLowStockAlerts();

        BigDecimal totalValuation = stocks.stream()
                .map(s -> {
                    return productRepository.findById(s.productId())
                            .map(p -> p.getPurchasePrice().multiply(s.quantity()))
                            .orElse(BigDecimal.ZERO);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<PurchaseOrderDto> poList = procurementService.getAllPurchaseOrders();
        long activePOs = poList.stream()
                .filter(p -> !"RECEIVED".equalsIgnoreCase(p.status()) && !"CANCELLED".equalsIgnoreCase(p.status()))
                .count();

        BigDecimal procurementSpend = poList.stream()
                .map(PurchaseOrderDto::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<SalesOrderDto> soList = salesService.getAllSalesOrders();
        long completedSOs = soList.stream()
                .filter(s -> !"CANCELLED".equalsIgnoreCase(s.status()))
                .count();

        BigDecimal salesRevenue = soList.stream()
                .map(SalesOrderDto::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DashboardMetricsDto(
                totalProducts,
                totalSuppliers,
                totalCustomers,
                totalWarehouses,
                lowStockAlerts.size(),
                activePOs,
                completedSOs,
                totalValuation,
                procurementSpend,
                salesRevenue,
                lowStockAlerts.stream().limit(5).toList(),
                poList.stream().limit(5).toList(),
                soList.stream().limit(5).toList(),
                inventoryService.getAllTransactions().stream().limit(8).toList()
        );
    }
}
