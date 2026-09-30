package com.fooderp.backend.feature;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Feature: Master Data Catalog & Executive Analytics KPI Dashboard")
public class MasterDataAndAnalyticsFeatureTest {

    @Autowired
    private MockMvc mockMvc;

    @Nested
    @DisplayName("Given seeded enterprise ERP data")
    class GivenSeededData {

        @Test
        @DisplayName("Scenario 1: Master Data Catalog returns comprehensive entities with relations")
        void testMasterDataEndpoints() throws Exception {
            // Verify Products
            mockMvc.perform(get("/api/master-data/products"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(5))))
                    .andExpect(jsonPath("$[0].name", notNullValue()))
                    .andExpect(jsonPath("$[0].categoryName", notNullValue()));

            // Verify Suppliers
            mockMvc.perform(get("/api/master-data/suppliers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                    .andExpect(jsonPath("$[0].email", containsString("@")));

            // Verify Customers (Restaurant Branches)
            mockMvc.perform(get("/api/master-data/customers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(3))))
                    .andExpect(jsonPath("$[0].name", notNullValue()));

            // Verify Warehouses (Cold Storage & Central)
            mockMvc.perform(get("/api/master-data/warehouses"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
        }

        @Test
        @DisplayName("Scenario 2: Executive Dashboard accurately reports aggregated financial and stock metrics")
        void testExecutiveAnalyticsDashboard() throws Exception {
            mockMvc.perform(get("/api/analytics/dashboard"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalProducts", greaterThanOrEqualTo(5)))
                    .andExpect(jsonPath("$.totalSuppliers", greaterThanOrEqualTo(3)))
                    .andExpect(jsonPath("$.totalCustomers", greaterThanOrEqualTo(3)))
                    .andExpect(jsonPath("$.totalWarehouses", greaterThanOrEqualTo(2)))
                    .andExpect(jsonPath("$.totalStockValuation", greaterThan(0.0)))
                    .andExpect(jsonPath("$.recentTransactions", not(empty())))
                    .andExpect(jsonPath("$.lowStockAlerts", notNullValue()));
        }

        @Test
        @DisplayName("Scenario 3: Low stock safety alerts identify products breaching minimum threshold")
        void testLowStockAlerts() throws Exception {
            mockMvc.perform(get("/api/inventory/stocks/low"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", isA(java.util.List.class)))
                    .andExpect(jsonPath("$[0].isLowStock", is(true)));
        }
    }
}
