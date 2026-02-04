# E2E API Tests — Basket / Products / Delivery (Playwright + TypeScript)

This repository contains automated **E2E API tests** for the shopping basket service.

The goal of this test suite is to validate the **most critical business flows first**:
1) **Product pricing and promotions** (discounts, restrictions, quantity rules)
2) **Delivery fee calculation** (thresholds and order-of-operations)

Why this is critical:
- **Product pricing + discounts** directly impact money and customer trust
- **Delivery fee rules** impact checkout totals and are highly prone to boundary bugs (e.g., €99 vs €100)
- Basket calculation is the core of checkout readiness and payment eligibility

## Tech Stack

- **RestAssured**
- **Java**
- **TestNG**

## Prerequisites

- Java 21 recommended
- Running API service (locally or remote environment)
- `BASE_URL` pointing to the service root URL (e.g. `http://localhost:3000`)

## Installation

```bash
mvn clean install -DskipTests=true
````

## Run Tests

### Run all tests

```bash
mvn clean test
```

### Run a specific file

```bash
mvn clean test -Dgroups=limitations
```

### Show HTML report

To generate a report locally:
```bash
mvn clean test
mvn allure:serve
```

## Test Scope (What Was Covered First)

### Highest Priority: Business-Critical Coverage

#### 1) Basket Calculation (`GET /api/baskets/:basketId/calculate`)

This endpoint is the core pricing engine. The following rules are covered first:

* Correct **lineTotal**, **discount**, **finalPrice** per item
* Correct basket totals:

    * `total = subtotal - totalDiscount + deliveryFee`
* Correct delivery fee:

    * orders **under €100**: €5
    * orders **€100 or above**: free delivery
    * Boundary checks (€99 vs €100) and order-of-operations (discounts affecting delivery threshold)

#### 2) Promotions / Product Business Rules

* Mango **Buy 1 Get 1 Free** logic
* Chicken discount logic (validated against real API behavior)
* Bread purchase restriction (max allowed quantity)

> Pricing + delivery is the most critical functional area because it affects
> the final amount the customer pays and is the most sensitive to defects.

### Highest Priority Rationale

This suite focuses on what breaks production checkout the fastest:

* wrong totals,
* broken discounts,
* wrong delivery thresholds,
* invalid basket readiness.

That’s why **product business rules + delivery calculation** are covered first.
Everything else (list products, basket CRUD, edge error handling) is important,
but secondary compared to money-related correctness.

## Teardown / Cleanup

Each test creates its own basket and deletes it after test execution to avoid:

* polluted test data
* flaky tests
* cross-test dependencies

Tests use `after test` teardown.

## Known Issues / Observed Defects

The list of discovered issues is described in FINDINGS.md (as requested by task description)