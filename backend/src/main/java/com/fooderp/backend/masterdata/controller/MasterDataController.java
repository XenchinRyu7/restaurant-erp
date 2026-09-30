package com.fooderp.backend.masterdata.controller;

import com.fooderp.backend.masterdata.dto.*;
import com.fooderp.backend.masterdata.service.MasterDataService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/master-data")
@CrossOrigin(origins = "*")
public class MasterDataController {

    private final MasterDataService masterDataService;

    public MasterDataController(MasterDataService masterDataService) {
        this.masterDataService = masterDataService;
    }

    // ================= CATEGORIES =================
    @GetMapping("/categories")
    public ResponseEntity<List<ProductCategoryDto>> getAllCategories() {
        return ResponseEntity.ok(masterDataService.getAllCategories());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ProductCategoryDto> getCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(masterDataService.getCategoryById(id));
    }

    @PostMapping("/categories")
    public ResponseEntity<ProductCategoryDto> createCategory(@RequestBody ProductCategoryDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(masterDataService.createCategory(dto));
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<ProductCategoryDto> updateCategory(@PathVariable UUID id, @RequestBody ProductCategoryDto dto) {
        return ResponseEntity.ok(masterDataService.updateCategory(id, dto));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        masterDataService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ================= PRODUCTS =================
    @GetMapping("/products")
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        return ResponseEntity.ok(masterDataService.getAllProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(masterDataService.getProductById(id));
    }

    @PostMapping("/products")
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(masterDataService.createProduct(dto));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable UUID id, @RequestBody ProductDto dto) {
        return ResponseEntity.ok(masterDataService.updateProduct(id, dto));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        masterDataService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ================= SUPPLIERS =================
    @GetMapping("/suppliers")
    public ResponseEntity<List<SupplierDto>> getAllSuppliers() {
        return ResponseEntity.ok(masterDataService.getAllSuppliers());
    }

    @GetMapping("/suppliers/{id}")
    public ResponseEntity<SupplierDto> getSupplierById(@PathVariable UUID id) {
        return ResponseEntity.ok(masterDataService.getSupplierById(id));
    }

    @PostMapping("/suppliers")
    public ResponseEntity<SupplierDto> createSupplier(@RequestBody SupplierDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(masterDataService.createSupplier(dto));
    }

    @PutMapping("/suppliers/{id}")
    public ResponseEntity<SupplierDto> updateSupplier(@PathVariable UUID id, @RequestBody SupplierDto dto) {
        return ResponseEntity.ok(masterDataService.updateSupplier(id, dto));
    }

    @DeleteMapping("/suppliers/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable UUID id) {
        masterDataService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    // ================= CUSTOMERS =================
    @GetMapping("/customers")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        return ResponseEntity.ok(masterDataService.getAllCustomers());
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(@PathVariable UUID id) {
        return ResponseEntity.ok(masterDataService.getCustomerById(id));
    }

    @PostMapping("/customers")
    public ResponseEntity<CustomerDto> createCustomer(@RequestBody CustomerDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(masterDataService.createCustomer(dto));
    }

    @PutMapping("/customers/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(@PathVariable UUID id, @RequestBody CustomerDto dto) {
        return ResponseEntity.ok(masterDataService.updateCustomer(id, dto));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        masterDataService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    // ================= WAREHOUSES =================
    @GetMapping("/warehouses")
    public ResponseEntity<List<WarehouseDto>> getAllWarehouses() {
        return ResponseEntity.ok(masterDataService.getAllWarehouses());
    }

    @GetMapping("/warehouses/{id}")
    public ResponseEntity<WarehouseDto> getWarehouseById(@PathVariable UUID id) {
        return ResponseEntity.ok(masterDataService.getWarehouseById(id));
    }

    @PostMapping("/warehouses")
    public ResponseEntity<WarehouseDto> createWarehouse(@RequestBody WarehouseDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(masterDataService.createWarehouse(dto));
    }

    @PutMapping("/warehouses/{id}")
    public ResponseEntity<WarehouseDto> updateWarehouse(@PathVariable UUID id, @RequestBody WarehouseDto dto) {
        return ResponseEntity.ok(masterDataService.updateWarehouse(id, dto));
    }

    @DeleteMapping("/warehouses/{id}")
    public ResponseEntity<Void> deleteWarehouse(@PathVariable UUID id) {
        masterDataService.deleteWarehouse(id);
        return ResponseEntity.noContent().build();
    }
}
