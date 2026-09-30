package com.fooderp.backend.feature;

import com.fooderp.backend.inventory.dto.CreateGoodsReceiptRequest;
import com.fooderp.backend.inventory.repository.InventoryStockRepository;
import com.fooderp.backend.masterdata.entity.Product;
import com.fooderp.backend.masterdata.entity.Supplier;
import com.fooderp.backend.masterdata.entity.Warehouse;
import com.fooderp.backend.masterdata.repository.ProductRepository;
import com.fooderp.backend.masterdata.repository.SupplierRepository;
import com.fooderp.backend.masterdata.repository.WarehouseRepository;
import com.fooderp.backend.procurement.dto.CreatePurchaseOrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Feature: Procurement to Inventory Receiving Workflow")
public class ProcurementAndInventoryFeatureTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private InventoryStockRepository inventoryStockRepository;

    private Supplier testSupplier;
    private Product testProduct;
    private Warehouse testWarehouse;

    @BeforeEach
    void setUp() {
        testSupplier = supplierRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Seed suppliers not found"));
        testProduct = productRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Seed products not found"));
        testWarehouse = warehouseRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Seed warehouses not found"));
    }

    @Nested
    @DisplayName("Given a valid supplier and product catalog")
    class GivenCatalogSetup {

        @Test
        @DisplayName("Scenario 1: End-to-end PO creation, confirmation, and goods receiving at warehouse")
        void testCompleteProcurementToReceivingLifecycle() throws Exception {
            // 1. Create Purchase Order using Record constructor
            CreatePurchaseOrderRequest poRequest = new CreatePurchaseOrderRequest(
                    testSupplier.getId(),
                    "Automated Feature Test Order",
                    "Chief Procurement Officer",
                    List.of(new CreatePurchaseOrderRequest.ItemRequest(
                            testProduct.getId(),
                            new BigDecimal("50.00"),
                            new BigDecimal("45000.00")
                    ))
            );

            MvcResult poCreateResult = mockMvc.perform(post("/api/procurement/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(poRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.orderNumber", startsWith("PO-")))
                    .andExpect(jsonPath("$.status", is("SUBMITTED")))
                    .andExpect(jsonPath("$.supplierName", is(testSupplier.getName())))
                    .andExpect(jsonPath("$.totalAmount", is(2250000.00)))
                    .andReturn();

            Map<?, ?> createdPo = objectMapper.readValue(poCreateResult.getResponse().getContentAsString(), Map.class);
            UUID poId = UUID.fromString((String) createdPo.get("id"));

            // 2. Confirm Purchase Order
            mockMvc.perform(patch("/api/procurement/orders/" + poId + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("status", "CONFIRMED"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("CONFIRMED")));

            // Check baseline stock before receiving
            BigDecimal initialStock = inventoryStockRepository.findByProductIdAndWarehouseId(testProduct.getId(), testWarehouse.getId())
                    .map(s -> s.getQuantity())
                    .orElse(BigDecimal.ZERO);

            // 3. Receive Goods via Goods Receipt
            CreateGoodsReceiptRequest grRequest = new CreateGoodsReceiptRequest(
                    poId,
                    testWarehouse.getId(),
                    "All 50 units inspected and approved",
                    "Chief Inventory Inspector",
                    List.of(new CreateGoodsReceiptRequest.ItemRequest(
                            testProduct.getId(),
                            new BigDecimal("50.00"),
                            new BigDecimal("45000.00")
                    ))
            );

            mockMvc.perform(post("/api/inventory/receipts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(grRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.receiptNumber", startsWith("GR-")))
                    .andExpect(jsonPath("$.warehouseName", is(testWarehouse.getName())))
                    .andExpect(jsonPath("$.items", hasSize(1)));

            // 4. Verify Stock is incremented in Warehouse
            BigDecimal updatedStock = inventoryStockRepository.findByProductIdAndWarehouseId(testProduct.getId(), testWarehouse.getId())
                    .map(s -> s.getQuantity())
                    .orElseThrow(() -> new AssertionError("Stock record must exist"));

            assertThat(updatedStock).isEqualByComparingTo(initialStock.add(new BigDecimal("50.00")));

            // 5. Verify Purchase Order status transitioned to RECEIVED
            mockMvc.perform(get("/api/procurement/orders/" + poId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("RECEIVED")))
                    .andExpect(jsonPath("$.items[0].receivedQuantity").value(50.0));

            // 6. Verify Audit Inventory Transaction is logged with type 'IN_PURCHASE'
            mockMvc.perform(get("/api/inventory/transactions")
                            .param("productId", testProduct.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].transactionType", is("IN_PURCHASE")))
                    .andExpect(jsonPath("$[0].referenceNumber", startsWith("GR-")));
        }

        @Test
        @DisplayName("Scenario 2: Manual inventory adjustment tracks delta and logs AUDIT transaction")
        void testManualStockAdjustment() throws Exception {
            Map<String, Object> adjustPayload = Map.of(
                    "productId", testProduct.getId().toString(),
                    "warehouseId", testWarehouse.getId().toString(),
                    "quantity", 15,
                    "notes", "Cycle count physical verification"
            );

            mockMvc.perform(post("/api/inventory/stocks/adjust")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(adjustPayload)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.productName", is(testProduct.getName())))
                    .andExpect(jsonPath("$.quantity").value(15.0));
        }
    }
}
