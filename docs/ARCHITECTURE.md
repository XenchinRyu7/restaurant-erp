# Architecture

## 1. Architecture Overview

Food ERP uses a **full-stack monorepo** containing:

* React frontend
* Spring Boot backend
* shared documentation
* infrastructure configuration

The backend follows a **Modular Monolith** architecture.

High-level architecture:

```text
┌──────────────────────────────────────────────┐
│                    USER                      │
└──────────────────────┬───────────────────────┘
                       │
                    HTTPS
                       │
┌──────────────────────▼───────────────────────┐
│                  FRONTEND                    │
│                                              │
│              React + TypeScript              │
│                    Vite                      │
└──────────────────────┬───────────────────────┘
                       │
                    REST API
                       │
┌──────────────────────▼───────────────────────┐
│               SPRING BOOT APP                │
│                                              │
│ ┌────────────┐ ┌────────────┐ ┌───────────┐ │
│ │ Inventory  │ │Procurement │ │   Sales   │ │
│ └────────────┘ └────────────┘ └───────────┘ │
│                                              │
│ ┌────────────┐ ┌────────────┐ ┌───────────┐ │
│ │ Production │ │ Accounting │ │    HR     │ │
│ └────────────┘ └────────────┘ └───────────┘ │
│                                              │
└──────────────────────┬───────────────────────┘
                       │
                    JDBC/JPA
                       │
┌──────────────────────▼───────────────────────┐
│                PostgreSQL                    │
└──────────────────────────────────────────────┘
```

---

# 2. Architectural Style

## Modular Monolith

The backend is deployed as a single Spring Boot application while its business capabilities are organized into distinct modules.

```text
One Application
│
├── Auth
├── Master Data
├── Procurement
├── Inventory
├── Warehouse
├── Production
├── Sales
├── Distribution
├── Accounting
├── HR
├── Payroll
└── Reporting
```

The modules share the same application runtime and initially share the same PostgreSQL database.

The modules must still maintain clear logical boundaries.

---

# 3. Why Modular Monolith?

The system is initially developed by a small development team / solo developer and the actual business requirements are still being discovered.

A modular monolith provides:

* simpler development;
* simpler deployment;
* simpler local development;
* lower infrastructure complexity;
* easier database transactions;
* easier debugging;
* clear domain boundaries;
* future flexibility.

Microservices are intentionally avoided until actual requirements justify their introduction.

---

# 4. Monorepo Structure

```text
food-erp/
│
├── frontend/
│
│   ├── src/
│   ├── public/
│   ├── package.json
│   └── ...
│
├── backend/
│
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   │
│   ├── pom.xml
│   └── ...
│
├── docs/
│   ├── PRD.md
│   ├── PROJECT-CONTEXT.md
│   ├── ARCHITECTURE.md
│   ├── BUSINESS-FLOWS.md
│   ├── DOMAIN-MODEL.md
│   ├── ERD.md
│   ├── API.md
│   └── ADR/
│
├── infra/
│   ├── docker/
│   └── docker-compose.yml
│
├── .github/
│   └── workflows/
│
├── README.md
└── .gitignore
```

---

# 5. Frontend Architecture

## Technology

* React
* TypeScript
* Vite
* React Router
* TanStack Query
* TanStack Table
* React Hook Form
* Zod
* shadcn/ui

The frontend is an authenticated SPA.

SEO is not a primary requirement because the main application is an internal business application.

---

## Frontend Structure

The frontend should be organized around application features/domains rather than only technical file types.

Example:

```text
frontend/src/

├── app/
│   ├── router/
│   ├── providers/
│   └── config/
│
├── features/
│   ├── auth/
│   ├── masterdata/
│   ├── procurement/
│   ├── inventory/
│   ├── sales/
│   ├── production/
│   ├── distribution/
│   ├── accounting/
│   └── hr/
│
├── components/
│   ├── ui/
│   └── shared/
│
├── hooks/
│
├── lib/
│
├── services/
│
├── types/
│
└── main.tsx
```

Feature boundaries should follow backend/domain boundaries where practical.

---

# 6. Backend Architecture

The backend is a Spring Boot application organized by business domain.

Potential structure:

```text
backend/src/main/java/com/company/erp/

├── common/
│
├── auth/
│
├── masterdata/
│
├── procurement/
│
├── inventory/
│
├── warehouse/
│
├── production/
│
├── sales/
│
├── distribution/
│
├── accounting/
│
├── hr/
│
├── payroll/
│
└── reporting/
```

Not every module needs to exist immediately.

Modules should be introduced when their requirements become sufficiently defined.

---

# 7. Module Internal Structure

A module may contain:

```text
inventory/
├── controller/
├── dto/
├── domain/
├── repository/
├── service/
└── mapper/
```

Example:

```text
inventory/
├── controller/
│   └── InventoryController.java
│
├── dto/
│   ├── InventoryResponse.java
│   └── StockAdjustmentRequest.java
│
├── domain/
│   └── InventoryTransaction.java
│
├── repository/
│   └── InventoryTransactionRepository.java
│
├── service/
│   └── InventoryService.java
│
└── mapper/
```

The exact internal structure may evolve as the domain becomes better understood.

---

# 8. Layer Responsibilities

## Controller

Responsible for:

* HTTP concerns;
* request/response mapping;
* authentication context;
* validation boundary.

Controllers should not contain complex business rules.

---

## Application / Service Layer

Responsible for:

* orchestrating use cases;
* transaction boundaries;
* invoking domain logic;
* coordinating repositories and other permitted modules.

Example:

```text
ReceiveGoods
    ↓
Validate Receipt
    ↓
Create Goods Receipt
    ↓
Update Inventory
    ↓
Create Inventory Transaction
```

---

## Domain Layer

Responsible for:

* business concepts;
* domain rules;
* invariants;
* state transitions.

---

## Repository

Responsible for persistence access.

Business logic should not be hidden inside repositories.

---

# 9. Module Boundaries

Modules should interact through explicit interfaces or application-level contracts where appropriate.

Avoid unrestricted access to another module's internal implementation.

Example:

```text
Sales
  ↓
Inventory Application Interface
  ↓
Inventory
```

rather than:

```text
Sales
  ↓
direct access to Inventory internals
```

This keeps module boundaries maintainable.

---

# 10. Database Architecture

Database:

**PostgreSQL**

The initial system uses one PostgreSQL database.

```text
Spring Boot
      │
      ↓
   PostgreSQL
```

Schema design will be derived from the domain model and ERD.

Database changes must use Flyway migrations.

---

# 11. Database Migration

Example:

```text
backend/
└── src/main/resources/
    └── db/
        └── migration/
            ├── V1__initial_schema.sql
            ├── V2__create_products.sql
            ├── V3__create_warehouses.sql
            └── V4__create_inventory_transactions.sql
```

Migration naming must follow Flyway conventions.

Production environments must never depend on automatic Hibernate schema generation.

---

# 12. JPA / Hibernate

Spring Data JPA and Hibernate will be used for persistence.

Hibernate schema auto-update must not be used as the source of truth for production schema management.

Expected configuration:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: validate
```

Database structure is controlled through Flyway.

---

# 13. API Architecture

The frontend communicates with the backend using REST APIs.

API versioning:

```text
/api/v1/...
```

Examples:

```text
GET    /api/v1/products
POST   /api/v1/products

GET    /api/v1/inventory
POST   /api/v1/inventory/adjustments

GET    /api/v1/purchase-orders
POST   /api/v1/purchase-orders
POST   /api/v1/purchase-orders/{id}/approve
POST   /api/v1/purchase-orders/{id}/receive
```

Exact endpoints will be defined as domain requirements become available.

---

# 14. API Design Principles

APIs should:

* use appropriate HTTP methods;
* return consistent response structures;
* validate input;
* return meaningful HTTP status codes;
* expose stable domain contracts;
* avoid exposing persistence entities directly;
* provide structured error responses.

DTOs should generally be used at API boundaries.

---

# 15. Authentication

Authentication will be handled by Spring Security.

Initial authentication concept:

```text
Browser
   ↓
Login
   ↓
Spring Security
   ↓
Authenticated Session / Token
   ↓
Protected API
```

The exact authentication mechanism will be finalized during implementation.

Credentials and secrets must never be committed to Git.

---

# 16. Authorization

Authorization uses Role-Based Access Control.

Example:

```text
ADMIN
MANAGER
PURCHASING
WAREHOUSE
PRODUCTION
SALES
ACCOUNTING
HR
EMPLOYEE
```

Authorization must be enforced on the backend.

Frontend visibility of menus/buttons is not considered sufficient security.

---

# 17. Validation

Request validation should occur at API boundaries using Bean Validation.

Example concepts:

```text
@NotNull
@NotBlank
@Positive
@Size
@Email
```

Business validation must remain separate from simple input validation.

Example:

```text
Input validation:
Quantity must be > 0

Business validation:
Cannot receive more quantity than the permitted purchase quantity
```

---

# 18. Transaction Management

Business operations that modify multiple related records must be executed atomically where required.

Example:

```text
Goods Receipt
    ↓
Create Receipt
    ↓
Increase Inventory
    ↓
Create Inventory Transaction
    ↓
Accounting Entry
```

If the operation fails at a required step, the system must prevent inconsistent state.

Spring transaction management will be used where appropriate.

---

# 19. Inventory Integrity

Inventory should be treated as a critical business domain.

Inventory changes should be traceable through transactions rather than relying only on a manually editable current-stock value.

Potential transaction types:

```text
PURCHASE_RECEIPT
SALES_ISSUE
PRODUCTION_CONSUMPTION
PRODUCTION_OUTPUT
TRANSFER
ADJUSTMENT
RETURN
```

Final transaction types depend on validated business requirements.

---

# 20. Auditability

Important state changes should produce audit information.

Potential fields:

```text
createdAt
createdBy
updatedAt
updatedBy
approvedAt
approvedBy
```

For sensitive operations, an explicit audit log may be required.

---

# 21. Error Handling

The backend should provide a consistent error response format.

Example conceptual response:

```json
{
  "timestamp": "2026-09-28T10:00:00Z",
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Invalid request",
  "errors": [
    {
      "field": "quantity",
      "message": "Must be greater than zero"
    }
  ]
}
```

Internal stack traces must not be exposed to clients.

---

# 22. Testing Strategy

Testing should occur at multiple levels.

## Unit Tests

Used for:

* domain rules;
* calculations;
* validation;
* service logic.

## Integration Tests

Used for:

* Spring context;
* persistence;
* module interactions;
* database behavior.

## API Tests

Used for:

* HTTP contracts;
* authentication;
* authorization;
* validation.

## End-to-End Tests

Used selectively for important user workflows.

Example:

```text
Create Purchase Order
        ↓
Receive Goods
        ↓
Inventory Updated
        ↓
Inventory Transaction Created
```

---

# 23. Testcontainers

Integration tests should use Testcontainers for PostgreSQL where appropriate.

Concept:

```text
Test
 ↓
Spring Boot
 ↓
Testcontainers
 ↓
Real PostgreSQL
```

This reduces discrepancies between test database behavior and production PostgreSQL behavior.

---

# 24. API Documentation

OpenAPI/Swagger will be used to document the REST API.

The documentation should allow developers to understand:

* available endpoints;
* request structures;
* response structures;
* authentication requirements;
* validation errors.

---

# 25. Observability

Initial observability:

* Spring Boot Actuator
* application logging
* structured logging where practical
* health checks

Potential future components:

* OpenTelemetry
* Prometheus
* Grafana

These should only be introduced when useful for the deployment environment.

---

# 26. Infrastructure

Initial local infrastructure:

```text
Docker Compose
│
├── PostgreSQL
└── optional supporting services
```

Application development:

```text
Frontend
   ↓
Backend
   ↓
PostgreSQL
```

Dockerization should support reproducible development and deployment.

---

# 27. CI/CD

GitHub Actions may be used for:

```text
Push / Pull Request
        ↓
Install dependencies
        ↓
Build
        ↓
Run tests
        ↓
Static checks
        ↓
Package
```

Deployment automation can be added after the initial application is stable.

---

# 28. Security Principles

The system should follow basic security principles:

* Never commit secrets.
* Validate all external input.
* Enforce authorization server-side.
* Use secure password hashing.
* Avoid exposing internal implementation details.
* Use HTTPS in deployed environments.
* Apply least privilege.
* Audit sensitive operations.
* Avoid unnecessary data exposure through APIs.

---

# 29. Dependency Policy

Dependencies should be introduced only when they provide a clear benefit.

Before adding a dependency, consider:

1. Is it actually required?
2. Does the framework already provide the capability?
3. Is the dependency actively maintained?
4. Does it introduce unnecessary complexity?
5. Does it create architectural coupling?

AI agents must not add dependencies without explaining the reason.

---

# 30. Architecture Decision Records

Important architectural decisions should be documented under:

```text
docs/ADR/
```

Example:

```text
ADR/
├── 001-modular-monolith.md
├── 002-react-vite-frontend.md
├── 003-postgresql.md
├── 004-flyway-migrations.md
└── ...
```

An ADR should explain:

```text
Context
Decision
Alternatives
Consequences
```

---

# 31. Architecture Constraints

The following are explicit constraints:

### Do not introduce microservices prematurely.

### Do not introduce Kafka/RabbitMQ without an asynchronous processing requirement.

### Do not introduce Redis without a demonstrated caching/session/rate-limiting requirement.

### Do not introduce Kubernetes for development convenience.

### Do not expose database entities directly as public API contracts.

### Do not place business logic inside controllers.

### Do not allow the frontend to bypass the backend.

### Do not use Hibernate auto-update as production database migration.

### Do not let AI agents modify architecture without review.

---

# 32. Future Evolution

The modular monolith should allow future extraction of modules if necessary.

Potential future evolution:

```text
Current

┌───────────────────────────────┐
│      Spring Boot Monolith     │
│                               │
│ Inventory │ Sales │ Finance   │
└───────────────────────────────┘


Possible Future

┌──────────────┐
│ ERP Core     │
└──────┬───────┘
       │
 ┌─────┼───────────┐
 ↓     ↓           ↓
Sales  Inventory   Accounting
Service Service    Service
```

This is **not a current requirement**.

The system should first prove the business model and actual scalability requirements before introducing distributed architecture.

---

# 33. Architecture Success Criteria

The architecture should allow developers to:

* understand individual business modules independently;
* modify one domain without unnecessary impact on others;
* test business logic independently;
* deploy the entire system simply;
* maintain consistent transactions;
* evolve the system without premature infrastructure complexity.

The architecture is considered successful when it makes the product easier to build and maintain rather than merely appearing technically sophisticated.
