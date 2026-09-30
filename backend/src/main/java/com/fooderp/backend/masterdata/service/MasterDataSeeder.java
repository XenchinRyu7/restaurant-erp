package com.fooderp.backend.masterdata.service;

import com.fooderp.backend.masterdata.entity.*;
import com.fooderp.backend.masterdata.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class MasterDataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(MasterDataSeeder.class);

    private final ProductCategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final CustomerRepository customerRepository;
    private final WarehouseRepository warehouseRepository;

    public MasterDataSeeder(
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

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            log.info("Master data already exists, skipping initial seed.");
            return;
        }

        log.info("Seeding initial Master Data for Food ERP...");

        // 1. Categories
        ProductCategory catMeat = categoryRepository.save(new ProductCategory(UUID.randomUUID(), "CAT-MEAT", "Meat & Poultry", "Daging sapi, ayam, dan olahan hewani", "ACTIVE"));
        ProductCategory catSeafood = categoryRepository.save(new ProductCategory(UUID.randomUUID(), "CAT-SEAFOOD", "Fresh Seafood", "Ikan salmon, udang, cumi segar", "ACTIVE"));
        ProductCategory catVeg = categoryRepository.save(new ProductCategory(UUID.randomUUID(), "CAT-VEG", "Fresh Produce & Herbs", "Sayuran segar, jamur, bumbu dapur segar", "ACTIVE"));
        ProductCategory catDairy = categoryRepository.save(new ProductCategory(UUID.randomUUID(), "CAT-DAIRY", "Dairy & Cheese", "Keju mozzarella, butter, susu segar", "ACTIVE"));
        ProductCategory catDry = categoryRepository.save(new ProductCategory(UUID.randomUUID(), "CAT-DRY", "Dry Ingredients & Grains", "Beras jepang, tepung, saus, minyak", "ACTIVE"));
        ProductCategory catPack = categoryRepository.save(new ProductCategory(UUID.randomUUID(), "CAT-PACK", "Eco Packaging", "Kotak makanan ramah lingkungan, cup minuman", "ACTIVE"));

        // 2. Products
        productRepository.saveAll(List.of(
                new Product(UUID.randomUUID(), "PRD-BEEF-01", "Wagyu Ribeye Slice MB5", catMeat, "KG", new BigDecimal("320000"), new BigDecimal("450000"), 15, "Daging slice premium untuk sukiyaki/grill", "ACTIVE"),
                new Product(UUID.randomUUID(), "PRD-CHICK-01", "Chicken Breast Fillet Boneless", catMeat, "KG", new BigDecimal("48000"), new BigDecimal("65000"), 30, "Fillet dada ayam segar tanpa kulit", "ACTIVE"),
                new Product(UUID.randomUUID(), "PRD-SALM-01", "Fresh Atlantic Salmon Fillet", catSeafood, "KG", new BigDecimal("260000"), new BigDecimal("350000"), 10, "Salmon sashimi grade segar Norwegia", "ACTIVE"),
                new Product(UUID.randomUUID(), "PRD-PRAWN-01", "Tiger Prawn Jumbo", catSeafood, "KG", new BigDecimal("140000"), new BigDecimal("190000"), 12, "Udang windu ukuran 20-25 ekor/kg", "ACTIVE"),
                new Product(UUID.randomUUID(), "PRD-LETTUCE-01", "Hydroponic Romaine Lettuce", catVeg, "KG", new BigDecimal("25000"), new BigDecimal("38000"), 20, "Selada segar hidroponik bebas pestisida", "ACTIVE"),
                new Product(UUID.randomUUID(), "PRD-MUSH-01", "Portobello Mushrooms", catVeg, "KG", new BigDecimal("65000"), new BigDecimal("95000"), 8, "Jamur segar pilihan untuk menu grill & steak", "ACTIVE"),
                new Product(UUID.randomUUID(), "PRD-CHEESE-01", "Fior di Latte Mozzarella", catDairy, "KG", new BigDecimal("115000"), new BigDecimal("160000"), 25, "Keju mozzarella leleh kualitas pizza Neapolitan", "ACTIVE"),
                new Product(UUID.randomUUID(), "PRD-RICE-01", "Koshihikari Japanese Rice 25kg", catDry, "BAG", new BigDecimal("550000"), new BigDecimal("720000"), 10, "Beras pulen Jepang karung 25kg", "ACTIVE"),
                new Product(UUID.randomUUID(), "PRD-TRUFFLE-01", "White Truffle Infused Olive Oil 500ml", catDry, "BOTTLE", new BigDecimal("210000"), new BigDecimal("295000"), 5, "Minyak truffle aroma intens dari Italia", "ACTIVE"),
                new Product(UUID.randomUUID(), "PRD-BOX-01", "Kraft Lunch Box 1000ml (50pcs)", catPack, "PACK", new BigDecimal("85000"), new BigDecimal("115000"), 40, "Kemasan makanan tahan panas & minyak", "ACTIVE")
        ));

        // 3. Suppliers
        supplierRepository.saveAll(List.of(
                new Supplier(UUID.randomUUID(), "SUP-BPN-01", "PT Boga Prima Nusantara", "order@bogaprima.co.id", "+62 21 555-0101", "Jl. Industri Daging Kav. 4, Jakarta Barat", "ACTIVE"),
                new Supplier(UUID.randomUUID(), "SUP-SSJ-02", "CV Samudera Segar Jaya", "sales@samuderasegar.id", "+62 21 555-0202", "Kawasan Pelabuhan Muara Baru Blok D, Jakarta Utara", "ACTIVE"),
                new Supplier(UUID.randomUUID(), "SUP-TMO-03", "Koperasi Tani Makmur Organik", "halo@tanimakmur.org", "+62 22 750-3344", "Jl. Raya Lembang No. 120, Bandung Barat", "ACTIVE"),
                new Supplier(UUID.randomUUID(), "SUP-SKL-04", "PT Sukses Kemasan Lestari", "supply@kemasanlestari.com", "+62 21 890-5566", "Kawasan Industri MM2100, Cikarang", "ACTIVE")
        ));

        // 4. Customers (Restaurant branches & B2B accounts)
        customerRepository.saveAll(List.of(
                new Customer(UUID.randomUUID(), "CUST-SDR-01", "Resto Nusantara - Cabang Sudirman", "sudirman@restonusantara.com", "+62 21 299-1001", "Gedung Pacific Place Lt. 5, SCBD Sudirman, Jakarta Selatan", "ACTIVE"),
                new Customer(UUID.randomUUID(), "CUST-SNP-02", "Resto Nusantara - Cabang Senopati", "senopati@restonusantara.com", "+62 21 720-2002", "Jl. Senopati Raya No. 45, Kebayoran Baru, Jakarta Selatan", "ACTIVE"),
                new Customer(UUID.randomUUID(), "CUST-PIK-03", "Bistro Tokyo Dining - PIK Pantjoran", "manager.pik@tokyodining.id", "+62 21 300-3003", "Pantjoran PIK Arcade Unit 12, Jakarta Utara", "ACTIVE"),
                new Customer(UUID.randomUUID(), "CUST-BDG-04", "Grand Terrace Cafe & Bistro", "procurement@grandterrace.co.id", "+62 22 423-4004", "Jl. Ir. H. Juanda No. 88 (Dago), Bandung", "ACTIVE")
        ));

        // 5. Warehouses
        warehouseRepository.saveAll(List.of(
                new Warehouse(UUID.randomUUID(), "WH-COLD-01", "Central Cold Storage Tanjung Priok", "Jl. Jampea No. 18, Tanjung Priok, Jakarta Utara", "ACTIVE"),
                new Warehouse(UUID.randomUUID(), "WH-DRY-02", "Central Dry Distribution Hub Bekasi", "Kawasan Pergudangan Delta Mas Blok A3, Bekasi", "ACTIVE"),
                new Warehouse(UUID.randomUUID(), "WH-KITCHEN-03", "Central Commissary Kitchen SCBD", "Kawasan SCBD Lot 8, Jakarta Selatan", "ACTIVE")
        ));

        log.info("Master Data seeded successfully with categories, products, suppliers, customers, and warehouses!");
    }
}
