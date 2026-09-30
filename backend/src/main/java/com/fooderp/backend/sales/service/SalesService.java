package com.fooderp.backend.sales.service;

import com.fooderp.backend.inventory.service.InventoryService;
import com.fooderp.backend.masterdata.entity.Customer;
import com.fooderp.backend.masterdata.entity.Product;
import com.fooderp.backend.masterdata.entity.Warehouse;
import com.fooderp.backend.masterdata.repository.CustomerRepository;
import com.fooderp.backend.masterdata.repository.ProductRepository;
import com.fooderp.backend.masterdata.repository.WarehouseRepository;
import com.fooderp.backend.sales.dto.CreateSalesOrderRequest;
import com.fooderp.backend.sales.dto.SalesOrderDto;
import com.fooderp.backend.sales.dto.SalesOrderItemDto;
import com.fooderp.backend.sales.entity.SalesOrder;
import com.fooderp.backend.sales.entity.SalesOrderItem;
import com.fooderp.backend.sales.repository.SalesOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class SalesService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;
    private final ProductRepository productRepository;
    private final InventoryService inventoryService;

    public SalesService(
            SalesOrderRepository salesOrderRepository,
            CustomerRepository customerRepository,
            WarehouseRepository warehouseRepository,
            ProductRepository productRepository,
            InventoryService inventoryService) {
        this.salesOrderRepository = salesOrderRepository;
        this.customerRepository = customerRepository;
        this.warehouseRepository = warehouseRepository;
        this.productRepository = productRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional(readOnly = true)
    public List<SalesOrderDto> getAllSalesOrders() {
        return salesOrderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SalesOrderDto getSalesOrderById(UUID id) {
        return salesOrderRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Sales order not found with id: " + id));
    }

    public SalesOrderDto createSalesOrder(CreateSalesOrderRequest request) {
        Customer customer = customerRepository.findById(request.customerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.customerId()));
        Warehouse warehouse = warehouseRepository.findById(request.warehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + request.warehouseId()));

        String orderNumber = "SO-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        SalesOrder order = new SalesOrder();
        order.setId(UUID.randomUUID());
        order.setOrderNumber(orderNumber);
        order.setCustomer(customer);
        order.setWarehouse(warehouse);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("CONFIRMED");
        order.setNotes(request.notes());
        order.setCreatedBy(request.createdBy() != null ? request.createdBy() : "Sales Officer");

        BigDecimal total = BigDecimal.ZERO;

        for (CreateSalesOrderRequest.ItemRequest itemReq : request.items()) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemReq.productId()));

            BigDecimal unitPrice = itemReq.unitPrice() != null ? itemReq.unitPrice() : product.getSellingPrice();
            SalesOrderItem item = new SalesOrderItem(
                    UUID.randomUUID(),
                    order,
                    product,
                    itemReq.quantity(),
                    unitPrice
            );
            order.addItem(item);
            total = total.add(item.getSubtotal());

            // Deduct inventory stock immediately on confirmed sales order
            inventoryService.deductStock(
                    product.getId(),
                    warehouse.getId(),
                    itemReq.quantity(),
                    orderNumber,
                    "Dispatched for " + customer.getName()
            );
        }

        order.setTotalAmount(total);
        return toDto(salesOrderRepository.save(order));
    }

    public SalesOrderDto updateStatus(UUID id, String status) {
        SalesOrder order = salesOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sales order not found with id: " + id));
        order.setStatus(status.toUpperCase());
        return toDto(salesOrderRepository.save(order));
    }

    public void deleteSalesOrder(UUID id) {
        salesOrderRepository.deleteById(id);
    }

    public SalesOrderDto toDto(SalesOrder so) {
        List<SalesOrderItemDto> items = so.getItems().stream().map(item -> new SalesOrderItemDto(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getCode(),
                item.getProduct().getName(),
                item.getProduct().getUnit(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getSubtotal(),
                item.getCreatedAt()
        )).collect(Collectors.toList());

        return new SalesOrderDto(
                so.getId(),
                so.getOrderNumber(),
                so.getCustomer().getId(),
                so.getCustomer().getCode(),
                so.getCustomer().getName(),
                so.getWarehouse().getId(),
                so.getWarehouse().getCode(),
                so.getWarehouse().getName(),
                so.getOrderDate(),
                so.getStatus(),
                so.getTotalAmount(),
                so.getNotes(),
                so.getCreatedBy(),
                items,
                so.getCreatedAt(),
                so.getUpdatedAt()
        );
    }
}
