# Entity Relationship Diagram

**Project:** Food ERP
**Version:** 0.1
**Status:** Initial Design / Hypothesis-Based

---

# 1. Overview

The initial database model follows the domain model defined in `DOMAIN-MODEL.md`.

The database uses PostgreSQL.

Schema changes must be managed through Flyway migrations.

Production configuration:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

Hibernate must not automatically modify the production schema.

---

# 2. Core Entity Relationship

```text
supplier
    │
    │ 1:N
    ▼
purchase_order
    │
    │ 1:N
    ▼
purchase_order_item
    │
    │ N:1
    ▼
product
```

Receiving:

```text
purchase_order
    │
    │ 1:N
    ▼
goods_receipt
    │
    │ 1:N
    ▼
goods_receipt_item
    │
    │ N:1
    ▼
product
```

Inventory:

```text
product
    │
    │ 1:N
    ▼
inventory_transaction
    │
    └── warehouse
```

---

# 3. Master Data

## product_category

```text
product_category
----------------
id PK
code UNIQUE
name
description
status
created_at
updated_at
```

## product

```text
product
----------------
id PK
code UNIQUE
name
category_id FK
unit
description
status
created_at
updated_at
```

Relationship:

```text
product_category 1 ─── N product
```

---

# 4. Supplier

```text
supplier
----------------
id PK
code UNIQUE
name
email
phone
address
status
created_at
updated_at
```

---

# 5. Customer

```text
customer
----------------
id PK
code UNIQUE
name
email
phone
address
status
created_at
updated_at
```

---

# 6. Warehouse

```text
warehouse
----------------
id PK
code UNIQUE
name
address
status
created_at
updated_at
```

Potential future entity:

```text
warehouse_location
----------------
id PK
warehouse_id FK
code
name
status
```

This is intentionally optional until storage requirements are validated.

---

# 7. Procurement

## purchase_order

```text
purchase_order
----------------
id PK
order_number UNIQUE
supplier_id FK
order_date
status
notes
created_by
created_at
updated_at
```

Relationship:

```text
supplier 1 ─── N purchase_order
```

---

## purchase_order_item

```text
purchase_order_item
----------------
id PK
purchase_order_id FK
product_id FK
quantity
unit_price
received_quantity
created_at
updated_at
```

Relationships:

```text
purchase_order 1 ─── N purchase_order_item

product 1 ─── N purchase_order_item
```

---

# 8. Goods Receipt

## goods_receipt

```text
goods_receipt
----------------
id PK
receipt_number UNIQUE
purchase_order_id FK
warehouse_id FK
receipt_date
status
notes
received_by
created_at
updated_at
```

---

## goods_receipt_item

```text
goods_receipt_item
----------------
id PK
goods_receipt_id FK
product_id FK
quantity
unit_cost
created_at
updated_at
```

Relationships:

```text
goods_receipt 1 ─── N goods_receipt_item

product 1 ─── N goods_receipt_item
```

---

# 9. Inventory

## inventory_transaction

```text
inventory_transaction
----------------
id PK
product_id FK
warehouse_i_
```
