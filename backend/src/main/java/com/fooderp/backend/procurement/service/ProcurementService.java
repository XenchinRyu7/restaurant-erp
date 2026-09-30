package com.fooderp.backend.procurement.service;

import com.fooderp.backend.masterdata.entity.Product;
import com.fooderp.backend.masterdata.entity.Supplier;
import com.fooderp.backend.masterdata.repository.ProductRepository;
import com.fooderp.backend.masterdata.repository.SupplierRepository;
import com.fooderp.backend.procurement.dto.CreatePurchaseOrderRequest;
import com.fooderp.backend.procurement.dto.PurchaseOrderDto;
import com.fooderp.backend.procurement.dto.PurchaseOrderItemDto;
import com.fooderp.backend.procurement.entity.PurchaseOrder;
import com.fooderp.backend.procurement.entity.PurchaseOrderItem;
import com.fooderp.backend.procurement.repository.PurchaseOrderRepository;
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
public class ProcurementService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;

    public ProcurementService(
            PurchaseOrderRepository purchaseOrderRepository,
            SupplierRepository supplierRepository,
            ProductRepository productRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.supplierRepository = supplierRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<PurchaseOrderDto> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PurchaseOrderDto getPurchaseOrderById(UUID id) {
        return purchaseOrderRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + id));
    }

    public PurchaseOrderDto createPurchaseOrder(CreatePurchaseOrderRequest request) {
        Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + request.supplierId()));

        String orderNumber = "PO-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        PurchaseOrder po = new PurchaseOrder();
        po.setId(UUID.randomUUID());
        po.setOrderNumber(orderNumber);
        po.setSupplier(supplier);
        po.setOrderDate(LocalDateTime.now());
        po.setStatus("SUBMITTED");
        po.setNotes(request.notes());
        po.setCreatedBy(request.createdBy() != null ? request.createdBy() : "Purchasing Staff");

        BigDecimal total = BigDecimal.ZERO;

        for (CreatePurchaseOrderRequest.ItemRequest itemReq : request.items()) {
            Product product = productRepository.findById(itemReq.productId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id: " + itemReq.productId()));

            BigDecimal unitPrice = itemReq.unitPrice() != null ? itemReq.unitPrice() : product.getPurchasePrice();
            PurchaseOrderItem item = new PurchaseOrderItem(
                    UUID.randomUUID(),
                    po,
                    product,
                    itemReq.quantity(),
                    unitPrice,
                    BigDecimal.ZERO
            );
            po.addItem(item);
            total = total.add(item.getSubtotal());
        }

        po.setTotalAmount(total);
        return toDto(purchaseOrderRepository.save(po));
    }

    public PurchaseOrderDto updateStatus(UUID id, String status) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found with id: " + id));
        po.setStatus(status.toUpperCase());
        return toDto(purchaseOrderRepository.save(po));
    }

    public void deletePurchaseOrder(UUID id) {
        purchaseOrderRepository.deleteById(id);
    }

    public PurchaseOrderDto toDto(PurchaseOrder po) {
        List<PurchaseOrderItemDto> items = po.getItems().stream().map(item -> new PurchaseOrderItemDto(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getCode(),
                item.getProduct().getName(),
                item.getProduct().getUnit(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getReceivedQuantity(),
                item.getSubtotal(),
                item.getCreatedAt()
        )).collect(Collectors.toList());

        return new PurchaseOrderDto(
                po.getId(),
                po.getOrderNumber(),
                po.getSupplier().getId(),
                po.getSupplier().getCode(),
                po.getSupplier().getName(),
                po.getOrderDate(),
                po.getStatus(),
                po.getTotalAmount(),
                po.getNotes(),
                po.getCreatedBy(),
                items,
                po.getCreatedAt(),
                po.getUpdatedAt()
        );
    }
}
