package com.fooderp.backend.feature;

import com.fooderp.backend.inventory.repository.InventoryStockRepository;
import com.fooderp.backend.masterdata.entity.Customer;
import com.fooderp.backend.masterdata.entity.Product;
import com.fooderp.backend.masterdata.entity.Warehouse;
import com.fooderp.backend.masterdata.repository.CustomerRepository;
import com.fooderp.backend.masterdata.repository.ProductRepository;
import com.fooderp.backend.masterdata.repository.WarehouseRepository;
import com.fooderp.backend.sales.dto.CreateSalesOrderRequest;
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
@DisplayName("Feature: Restaurant Branch Sales & Inventory Fulfillment Workflow")
public class SalesAndFulfillmentFeatureTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private InventoryStockRepository inventoryStockRepository;

    private Customer testCustomer;
    private Product testProduct;
    private Warehouse testWarehouse;

    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Seed customers not found"));
        testProduct = productRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Seed products not found"));
        testWarehouse = warehouseRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new IllegalStateException("Seed warehouses not found"));
    }

    @Nested
    @DisplayName("Given active branch customers and in-stock items")
    class GivenActiveSalesCatalog {

        @Test
        @DisplayName("Scenario 1: End-to-end sales dispatch: order placement, confirmation, and stock deduction upon delivery")
        void testCompleteSalesAndFulfillmentLifecycle() throws Exception {
            // Check baseline stock before sales order delivery
            BigDecimal baselineStock = inventoryStockRepository.findByProductIdAndWarehouseId(testProduct.getId(), testWarehouse.getId())
                    .map(s -> s.getQuantity())
                    .orElse(BigDecimal.ZERO);

            // 1. Place Sales Order for restaurant branch
            BigDecimal orderQty = new BigDecimal("8.00");
            BigDecimal unitPrice = new BigDecimal("125000.00");

            CreateSalesOrderRequest salesReq = new CreateSalesOrderRequest(
                    testCustomer.getId(),
                    testWarehouse.getId(),
                    "Weekly branch replenishment dispatch",
                    "Store Dispatcher",
                    List.of(new CreateSalesOrderRequest.ItemRequest(
                            testProduct.getId(),
                            orderQty,
                            unitPrice
                    ))
            );

            MvcResult createResult = mockMvc.perform(post("/api/sales/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(salesReq)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.orderNumber", startsWith("SO-")))
                    .andExpect(jsonPath("$.status", is("CONFIRMED")))
                    .andExpect(jsonPath("$.customerName", is(testCustomer.getName())))
                    .andExpect(jsonPath("$.totalAmount", is(1000000.00)))
                    .andReturn();

            Map<?, ?> createdSo = objectMapper.readValue(createResult.getResponse().getContentAsString(), Map.class);
            UUID soId = UUID.fromString((String) createdSo.get("id"));

            // 2. Transition Sales Order to PROCESSING
            mockMvc.perform(patch("/api/sales/orders/" + soId + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("status", "PROCESSING"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("PROCESSING")));

            // 3. Mark Sales Order as DELIVERED (triggers automatic inventory deduction)
            mockMvc.perform(patch("/api/sales/orders/" + soId + "/status")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(Map.of("status", "DELIVERED"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status", is("DELIVERED")));

            // 4. Verify Stock is deducted from Warehouse
            BigDecimal remainingStock = inventoryStockRepository.findByProductIdAndWarehouseId(testProduct.getId(), testWarehouse.getId())
                    .map(s -> s.getQuantity())
                    .orElseThrow(() -> new AssertionError("Stock record must exist"));

            assertThat(remainingStock).isEqualByComparingTo(baselineStock.subtract(orderQty));

            // 5. Verify Audit Inventory Transaction is logged with type 'OUT_SALES'
            mockMvc.perform(get("/api/inventory/transactions")
                            .param("productId", testProduct.getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].transactionType", is("OUT_SALES")))
                    .andExpect(jsonPath("$[0].referenceNumber", startsWith("SO-")));
        }
    }
}
