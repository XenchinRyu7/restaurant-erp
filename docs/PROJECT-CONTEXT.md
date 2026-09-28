# Project Context

## 1. Project Identity

**Project Name:** Food ERP
**Project Type:** Enterprise Business Management System
**Repository Type:** Full-stack Monorepo
**Backend Architecture:** Modular Monolith

---

# 2. Project Purpose

Food ERP is a web-based enterprise management system intended to centralize and integrate business operations for a company operating in the food processing, food supply, and distribution domain.

The system is intended to reduce fragmented data management and unnecessary duplicate data entry between departments.

The system should provide a shared source of operational data across relevant business functions.

---

# 3. Business Context

The target business operates in the food-related supply chain and may handle activities such as:

```text
Raw Materials
      ↓
Processing / Preparation
      ↓
Products / Materials
      ↓
Distribution
      ↓
Restaurant / Business Customers
```

Potential raw materials may include food ingredients such as spices, fish, poultry, and other food-related materials.

However, the exact business process has **not yet been fully validated**.

The following must therefore be treated as assumptions until confirmed by stakeholders:

* exact production process;
* exact product structure;
* manufacturing complexity;
* inventory rules;
* warehouse structure;
* customer ordering process;
* delivery process;
* accounting workflow;
* HR workflow;
* payroll calculation;
* tax requirements;
* batch and expiration requirements.

The system must not hard-code unvalidated assumptions.

---

# 4. Primary Problem

The initial business problem is believed to involve fragmented operational processes and systems.

Potential consequences include:

* duplicate data entry;
* disconnected operational information;
* manual reconciliation;
* inconsistent inventory information;
* difficulty tracking transactions;
* fragmented reporting;
* inefficient administrative processes.

The actual pain points must be validated during the discovery phase.

---

# 5. Product Vision

Create a single integrated platform where business transactions flow between relevant departments while maintaining a consistent source of data.

Conceptually:

```text
                    ERP
                     │
        ┌────────────┼────────────┐
        ↓            ↓            ↓
   Procurement    Inventory     Sales
        │            │            │
        └────────────┼────────────┘
                     ↓
                 Operations
                     │
        ┌────────────┼────────────┐
        ↓            ↓            ↓
   Production   Distribution      HR
        │            │            │
        └────────────┼────────────┘
                     ↓
                  Finance
```

Not every module is required to be implemented.

---

# 6. Target Users

Potential users include:

* Management
* Administrator
* Purchasing
* Warehouse
* Production
* Sales
* Distribution
* Finance / Accounting
* HR
* Employees

Final roles and permissions must be based on the actual organization structure.

---

# 7. Product Philosophy

The project follows these principles:

### Business First

Technical implementation must support validated business requirements.

### Modular

Business capabilities should have clear module boundaries.

### Simple by Default

Avoid unnecessary complexity unless a concrete requirement justifies it.

### Configurable

Business rules that are likely to vary should not be unnecessarily hard-coded.

### Traceable

Important transactions and changes should be auditable.

### Testable

Important business rules must be covered by automated tests.

### Incremental

The product will be developed iteratively rather than implementing the entire ERP at once.

---

# 8. Technology Stack

## Frontend

* React
* TypeScript
* Vite
* React Router
* TanStack Query
* TanStack Table
* React Hook Form
* Zod
* shadcn/ui

The frontend is intended to be a lightweight authenticated SPA.

SEO and public search indexing are not primary requirements because the main application is an authenticated internal business system.

---

## Backend

* Java
* Spring Boot
* Spring Security
* Spring Data JPA
* Hibernate
* Bean Validation
* Flyway
* OpenAPI

The backend uses a modular monolith architecture.

---

## Database

* PostgreSQL

Database schema changes must be managed through migrations.

---

## Infrastructure

Initial infrastructure:

* Docker
* GitHub Actions
* Testcontainers
* Spring Boot Actuator

Additional infrastructure such as Redis, message brokers, or Kubernetes must only be introduced when justified by an actual requirement.

---

# 9. Repository Structure

The project uses a single Git repository containing separate frontend and backend applications.

```text
food-erp/
│
├── frontend/
│
├── backend/
│
├── docs/
│
├── infra/
│
├── .github/
│
├── README.md
└── .gitignore
```

This is a **monorepo**.

The use of a monorepo does not determine the backend architecture.

The backend is specifically designed as a **modular monolith**.

---

# 10. Backend Module Concept

Potential backend modules:

```text
backend/
└── src/main/java/com/company/erp/

    ├── auth/
    ├── masterdata/
    ├── procurement/
    ├── inventory/
    ├── warehouse/
    ├── production/
    ├── sales/
    ├── distribution/
    ├── accounting/
    ├── hr/
    ├── payroll/
    └── reporting/
```

Modules should be added and expanded according to validated requirements.

---

# 11. Frontend Concept

The frontend is a React SPA that communicates with the backend through REST APIs.

Conceptually:

```text
Browser
   ↓
React + Vite
   ↓
REST API
   ↓
Spring Boot
   ↓
PostgreSQL
```

The frontend should not directly access the database.

Business rules belong to the backend.

---

# 12. Core Architectural Constraints

The following constraints apply unless an Architecture Decision Record explicitly changes them.

### C1 — No Premature Microservices

The backend must remain a modular monolith unless there is a documented technical or business reason to introduce separate services.

### C2 — No Unnecessary Infrastructure

Do not introduce Kafka, RabbitMQ, Redis, Kubernetes, or similar infrastructure merely to make the project appear more enterprise.

### C3 — Database Migrations

Database schema changes must be versioned through Flyway migrations.

### C4 — API Boundary

Frontend communication with backend must occur through defined API contracts.

### C5 — Business Logic

Business logic should not be placed directly inside controllers.

### C6 — Authentication & Authorization

Protected functionality must be controlled through authentication and authorization.

### C7 — Auditability

Important state-changing operations should be traceable.

### C8 — Tests

Important business rules must have automated test coverage.

---

# 13. AI-Assisted Development

AI coding agents are intentionally part of the development workflow.

AI may be used to:

* generate boilerplate;
* implement defined features;
* generate tests;
* refactor code;
* explain errors;
* generate documentation;
* assist debugging;
* review implementation.

However:

> AI is an implementation assistant, not the owner of architecture or business requirements.

Architectural decisions, domain rules, security decisions, database boundaries, and significant design changes must be reviewed by the project owner.

AI must not assume undocumented business rules as facts.

---

# 14. Development Workflow

The general development flow is:

```text
PRD
 ↓
Business Process
 ↓
Domain Model
 ↓
Architecture
 ↓
ERD
 ↓
API Contract
 ↓
Sprint Planning
 ↓
Implementation
 ↓
Automated Tests
 ↓
Code Review
 ↓
Integration / QA
 ↓
Deployment
```

Development should occur incrementally by business domain.

---

# 15. Current Project Status

**Current Phase: Initial Architecture / Discovery**

Completed:

* Initial product concept
* Initial PRD
* Technology stack selection
* Repository initialization
* Modular monolith decision

Not yet finalized:

* Detailed business workflows
* Domain model
* ERD
* API contracts
* Final user roles
* Final module scope
* Accounting rules
* Production rules
* Payroll rules

---

# 16. Requirement Confidence Levels

Requirements should be categorized as:

### Confirmed

Validated by an appropriate stakeholder or source.

### Assumed

Reasonable initial assumption but not yet validated.

### Candidate

Potential feature being considered.

### TBD

Insufficient information to make a decision.

Example:

```text
Batch Tracking
Status: Candidate

Production BOM
Status: Assumed

Payroll
Status: Candidate

Exact Payroll Formula
Status: TBD
```

This prevents assumptions from silently becoming system requirements.

---

# 17. Decision-Making Principle

When information is missing:

1. Do not invent business rules.
2. Prefer configurable designs where appropriate.
3. Record assumptions explicitly.
4. Validate important assumptions before implementation.
5. Prefer reversible technical decisions when uncertainty is high.

---

# 18. Definition of Done

A feature is not considered complete merely because the code compiles.

Depending on feature scope, completion should include:

* implementation;
* validation;
* automated tests;
* database migration if applicable;
* API documentation if applicable;
* authorization checks;
* error handling;
* relevant documentation;
* code review;
* successful build.

---

# 19. Long-Term Direction

The system should initially optimize for:

```text
Correctness
    ↓
Maintainability
    ↓
Business Integration
    ↓
Reliability
    ↓
Performance Optimization
```

Premature scaling complexity should be avoided.

The architecture should remain capable of evolving if actual product usage eventually requires:

* asynchronous processing;
* caching;
* external integrations;
* separate services;
* additional deployment units.

Such changes should be introduced based on evidence rather than assumptions.
