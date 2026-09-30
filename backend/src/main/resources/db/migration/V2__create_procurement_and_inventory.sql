ALTER TABLE product ADD COLUMN IF NOT EXISTS purchase_price NUMERIC(15, 2) DEFAULT 0.00;
ALTER TABLE product ADD COLUMN IF NOT EXISTS selling_price NUMERIC(15, 2) DEFAULT 0.00;
ALTER TABLE product ADD COLUMN IF NOT EXISTS min_stock INTEGER DEFAULT 0;

CREATE TABLE purchase_order (
    id UUID PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    supplier_id UUID NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_amount NUMERIC(15, 2) DEFAULT 0.00,
    notes TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_po_supplier
        FOREIGN KEY (supplier_id)
        REFERENCES supplier(id)
);

CREATE TABLE purchase_order_item (
    id UUID PRIMARY KEY,
    purchase_order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity NUMERIC(15, 2) NOT NULL,
    unit_price NUMERIC(15, 2) NOT NULL,
    received_quantity NUMERIC(15, 2) DEFAULT 0.00,
    subtotal NUMERIC(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_poi_order
        FOREIGN KEY (purchase_order_id)
        REFERENCES purchase_order(id) ON DELETE CASCADE,
    CONSTRAINT fk_poi_product
        FOREIGN KEY (product_id)
        REFERENCES product(id)
);

CREATE TABLE goods_receipt (
    id UUID PRIMARY KEY,
    receipt_number VARCHAR(50) NOT NULL UNIQUE,
    purchase_order_id UUID,
    warehouse_id UUID NOT NULL,
    receipt_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    received_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_gr_po
        FOREIGN KEY (purchase_order_id)
        REFERENCES purchase_order(id),
    CONSTRAINT fk_gr_warehouse
        FOREIGN KEY (warehouse_id)
        REFERENCES warehouse(id)
);

CREATE TABLE goods_receipt_item (
    id UUID PRIMARY KEY,
    goods_receipt_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity NUMERIC(15, 2) NOT NULL,
    unit_cost NUMERIC(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_gri_receipt
        FOREIGN KEY (goods_receipt_id)
        REFERENCES goods_receipt(id) ON DELETE CASCADE,
    CONSTRAINT fk_gri_product
        FOREIGN KEY (product_id)
        REFERENCES product(id)
);

CREATE TABLE inventory_stock (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    quantity NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    last_updated_at TIMESTAMP NOT NULL,

    CONSTRAINT uq_product_warehouse UNIQUE (product_id, warehouse_id),
    CONSTRAINT fk_stock_product FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT fk_stock_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
);

CREATE TABLE inventory_transaction (
    id UUID PRIMARY KEY,
    product_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    quantity NUMERIC(15, 2) NOT NULL,
    reference_number VARCHAR(50),
    notes TEXT,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_tx_product FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT fk_tx_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
);

CREATE TABLE sales_order (
    id UUID PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    customer_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    order_date TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_amount NUMERIC(15, 2) DEFAULT 0.00,
    notes TEXT,
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_so_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    CONSTRAINT fk_so_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouse(id)
);

CREATE TABLE sales_order_item (
    id UUID PRIMARY KEY,
    sales_order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity NUMERIC(15, 2) NOT NULL,
    unit_price NUMERIC(15, 2) NOT NULL,
    subtotal NUMERIC(15, 2) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_soi_order FOREIGN KEY (sales_order_id) REFERENCES sales_order(id) ON DELETE CASCADE,
    CONSTRAINT fk_soi_product FOREIGN KEY (product_id) REFERENCES product(id)
);
