package com.fooderp.backend.masterdata.service;

import com.fooderp.backend.masterdata.dto.*;
import com.fooderp.backend.masterdata.entity.*;
import com.fooderp.backend.masterdata.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class MasterDataService {

    private final ProductCategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;

    public MasterDataService(
            ProductCategoryRepository categoryRepository,
            ProductRepository productRepository,
            SupplierRepository supplierRepository,
            CustomerRepository customerRepository,
            WarehouseRepository warehouseRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.supplierRepository = supplierRepository;
        this.customerRepository = customerRepository;
        this.warehouseRepository = warehouseRepository;
    }

    // ================= PRODUCT CATEGORY =================
    @Transactional(readOnly = true)
    public List<ProductCategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductCategoryDto getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .map(this::toCategoryDto)
                .orElseThrow(() -> new RuntimeException("Product category not found with id: " + id));
    }

    public ProductCategoryDto createCategory(ProductCategoryDto dto) {
        ProductCategory entity = new ProductCategory(
                dto.id(),
                dto.code(),
                dto.name(),
                dto.description(),
                dto.status()
        );
        return toCategoryDto(categoryRepository.save(entity));
    }

    public ProductCategoryDto updateCategory(UUID id, ProductCategoryDto dto) {
        ProductCategory entity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product category not found with id: " + id));
        entity.setCode(dto.code());
        entity.setName(dto.name());
        entity.setDescription(dto.description());
        if (dto.status() != null) {
            entity.setStatus(dto.status());
        }
        return toCategoryDto(categoryRepository.save(entity));
    }

    public void deleteCategory(UUID id) {
        categoryRepository.deleteById(id);
    }

    // ================= PRODUCT =================
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::toProductDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID id) {
        return productRepository.findById(id)
                .map(this::toProductDto)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public ProductDto createProduct(ProductDto dto) {
        ProductCategory category = categoryRepository.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.categoryId()));

        Product entity = new Product(
                dto.id(),
                dto.code(),
                dto.name(),
                category,
                dto.unit(),
                dto.purchasePrice(),
                dto.sellingPrice(),
                dto.minStock(),
                dto.description(),
                dto.status()
        );
        return toProductDto(productRepository.save(entity));
    }

    public ProductDto updateProduct(UUID id, ProductDto dto) {
        Product entity = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        if (dto.categoryId() != null && !dto.categoryId().equals(entity.getCategory().getId())) {
            ProductCategory category = categoryRepository.findById(dto.categoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.categoryId()));
            entity.setCategory(category);
        }

        entity.setCode(dto.code());
        entity.setName(dto.name());
        entity.setUnit(dto.unit());
        entity.setPurchasePrice(dto.purchasePrice());
        entity.setSellingPrice(dto.sellingPrice());
        entity.setMinStock(dto.minStock());
        entity.setDescription(dto.description());
        if (dto.status() != null) {
            entity.setStatus(dto.status());
        }
        return toProductDto(productRepository.save(entity));
    }

    public void deleteProduct(UUID id) {
        productRepository.deleteById(id);
    }

    // ================= SUPPLIER =================
    @Transactional(readOnly = true)
    public List<SupplierDto> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::toSupplierDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SupplierDto getSupplierById(UUID id) {
        return supplierRepository.findById(id)
                .map(this::toSupplierDto)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    }

    public SupplierDto createSupplier(SupplierDto dto) {
        Supplier entity = new Supplier(
                dto.id(),
                dto.code(),
                dto.name(),
                dto.email(),
                dto.phone(),
                dto.address(),
                dto.status()
        );
        return toSupplierDto(supplierRepository.save(entity));
    }

    public SupplierDto updateSupplier(UUID id, SupplierDto dto) {
        Supplier entity = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
        entity.setCode(dto.code());
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPhone(dto.phone());
        entity.setAddress(dto.address());
        if (dto.status() != null) {
            entity.setStatus(dto.status());
        }
        return toSupplierDto(supplierRepository.save(entity));
    }

    public void deleteSupplier(UUID id) {
        supplierRepository.deleteById(id);
    }

    // ================= CUSTOMER =================
    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toCustomerDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CustomerDto getCustomerById(UUID id) {
        return customerRepository.findById(id)
                .map(this::toCustomerDto)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
    }

    public CustomerDto createCustomer(CustomerDto dto) {
        Customer entity = new Customer(
                dto.id(),
                dto.code(),
                dto.name(),
                dto.email(),
                dto.phone(),
                dto.address(),
                dto.status()
        );
        return toCustomerDto(customerRepository.save(entity));
    }

    public CustomerDto updateCustomer(UUID id, CustomerDto dto) {
        Customer entity = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + id));
        entity.setCode(dto.code());
        entity.setName(dto.name());
        entity.setEmail(dto.email());
        entity.setPhone(dto.phone());
        entity.setAddress(dto.address());
        if (dto.status() != null) {
            entity.setStatus(dto.status());
        }
        return toCustomerDto(customerRepository.save(entity));
    }

    public void deleteCustomer(UUID id) {
        customerRepository.deleteById(id);
    }

    // ================= WAREHOUSE =================
    @Transactional(readOnly = true)
    public List<WarehouseDto> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .map(this::toWarehouseDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WarehouseDto getWarehouseById(UUID id) {
        return warehouseRepository.findById(id)
                .map(this::toWarehouseDto)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
    }

    public WarehouseDto createWarehouse(WarehouseDto dto) {
        Warehouse entity = new Warehouse(
                dto.id(),
                dto.code(),
                dto.name(),
                dto.address(),
                dto.status()
        );
        return toWarehouseDto(warehouseRepository.save(entity));
    }

    public WarehouseDto updateWarehouse(UUID id, WarehouseDto dto) {
        Warehouse entity = warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found with id: " + id));
        entity.setCode(dto.code());
        entity.setName(dto.name());
        entity.setAddress(dto.address());
        if (dto.status() != null) {
            entity.setStatus(dto.status());
        }
        return toWarehouseDto(warehouseRepository.save(entity));
    }

    public void deleteWarehouse(UUID id) {
        warehouseRepository.deleteById(id);
    }

    // ================= MAPPERS =================
    private ProductCategoryDto toCategoryDto(ProductCategory entity) {
        return new ProductCategoryDto(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private ProductDto toProductDto(Product entity) {
        return new ProductDto(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getCategory() != null ? entity.getCategory().getId() : null,
                entity.getCategory() != null ? entity.getCategory().getName() : null,
                entity.getUnit(),
                entity.getPurchasePrice(),
                entity.getSellingPrice(),
                entity.getMinStock(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private SupplierDto toSupplierDto(Supplier entity) {
        return new SupplierDto(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getAddress(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private CustomerDto toCustomerDto(Customer entity) {
        return new CustomerDto(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getEmail(),
                entity.getPhone(),
                entity.getAddress(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private WarehouseDto toWarehouseDto(Warehouse entity) {
        return new WarehouseDto(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getAddress(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
