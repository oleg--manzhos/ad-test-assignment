## Bug #1: `basketReadyToPay` is `false` when basket contains Mango

### Summary
When a basket contains **Mango  or Bread** products and is calculated via  
`GET /api/baskets/{basketId}/calculate`, the response returns:

```json
"basketReadyToPay": false
```
### Steps to Reproduce

1. Create a new basket  
   `POST /api/baskets`

2. Add Mango (Bread) product to the basket  
   `POST /api/baskets/{basketId}/items`

   Request body:
   ```json
   {
     "productId": "4",
     "quantity": 1
   }
   ```
### Expected Result

- The basket calculation completes successfully.
- All item prices, discounts, and totals are calculated correctly.
- The basket contains only valid products with valid quantities.
- The total amount is greater than zero and includes delivery fee if applicable.
- The basket is considered ready for checkout.

```json
"basketReadyToPay": true
```
### Actual Result

- The basket calculation completes successfully.
- All item prices, discounts, and totals are calculated correctly.
- No validation or calculation errors are returned by the API.
- Despite the basket being valid and payable, the following value is returned:

```json
"basketReadyToPay": false
```
## Bug #2: Incorrect total price calculation for Chicken when quantity = 3

### Summary
When **3 Chicken items** are added to the basket, the calculated final price does not match the expected discount rule.

According to the Business Requirements, the discount should be applied **only to every 3rd item**, resulting in a lower final price.
However, the API calculates the price without applying the expected discount.

#### Business Requirement
**Chicken**  
*Buy 2+, Every 3rd item is 25% off.*

Expected discount formula:
discount = floor(quantity / 3) * unitPrice × 0.25
For `quantity = 3`:
- One item should receive a 25% discount
- Discount amount: `12 * 0.25 = 3`
- Line total: `3 * 12 = 36`
- **Expected final price: `36 − 3 = 33`**

### Steps to Reproduce

1. Create a new basket  
   `POST /api/baskets`

2. Add Chicken to the basket  
   `POST /api/baskets/{basketId}/items`

   Request body:
   ```json
   {
     "productId": "3",
     "quantity": 3
   }
   ```
3. Calculate the basket
   `GET /api/baskets/{basketId}/calculate`

### Actual Result
API response for Chicken item:
```json
{
  "productName": "Chicken",
  "unitPrice": 12,
  "quantity": 3,
  "lineTotal": 36,
  "discount": 0,
  "finalPrice": 36
}
```
No discount is applied
Final price equals the full line total

### Expected Result
```json
{
  "productName": "Chicken",
  "lineTotal": 36,
  "discount": 3,
  "finalPrice": 33
}
```
A 25% discount should be applied to the 3rd Chicken item
Final price should be 33

## Bug #3: Non-integer product quantity is accepted and affects price calculation

### Summary
The API allows adding a product to the basket with a **non-integer quantity**.
As a result, the basket calculation multiplies the product price by a fractional value, producing an invalid total price.

### Business Expectation
Product quantities in a shopping basket are expected to be **whole numbers**.
Fractional quantities (e.g. `1.5`, `2.25`) should not be allowed for standard retail items.

The API should:
- reject non-integer quantities, or
- validate and round quantities according to defined business rules.

### Steps to Reproduce

1. Create a new basket  
   `POST /api/baskets`

2. Add a product to the basket with a non-integer quantity  
   `POST /api/baskets/{basketId}/items`

   Request body:
   ```json
   {
     "productId": "2",
     "quantity": 1.5
   }
   ```
3. Calculate the basket
   `GET /api/baskets/{basketId}/calculate`

### Actual Result

The API accepts the request and returns the following calculation:
```json
{
  "unitPrice": 1,
  "quantity": 1.5,
  "lineTotal": 1.5,
  "finalPrice": 1.5
}
```
The non-integer quantity is accepted without validation
The price is multiplied by a fractional value

### Expected Result

The API should reject non-integer quantities with a validation error (e.g. 400 Bad Request) or a clearly defined rule is applied before calculation.

Example expected response:
```json
{
  "error": "A product quantity must be a whole number"
}
```

## Bug #4: Unable to add maximum allowed quantity of Bread (5 items)

### Summary
According to the Business Requirements, **Bread** has a purchase restriction that allows adding **up to 5 items per basket**.
However, the API does not allow adding **exactly 5 Bread items**, modifying the request by adding 4 items to the basket.

### Business Requirement
- **Bread**
- *Max 5 items per basket*

### Expected behavior:
- Quantities **from 1 to 5** (inclusive) should be allowed
- Quantities greater than 5 should be rejected

### Steps to Reproduce

1. Create a new basket  
   `POST /api/baskets`

2. Add Bread to the basket with the maximum allowed quantity  
   `POST /api/baskets/{basketId}/items`

   Request body:
   ```json
   {
     "productId": "1",
     "quantity": 5
   }
   ```
Observe the API response

### Actual Result
The API addds 4 items to the basket
The basket does not contain the expected quantity of Bread

### Expected Result
The request should succeed

Bread should be added to the basket with:
```json
{
  "productId": "1",
  "quantity": 5
}
```
The basket should be in a valid state

### Notes
The restriction appears to be implemented as quantity < 5 instead of quantity ≤ 5
The issue is specific to Bread and does not affect other products

## Bug #5: Delivery fee is charged for invalid basket states (zero or excessive quantity)

### Summary
The API charges a **€5 delivery fee** even when the basket is in an **invalid state**:
- product quantity is `0`,
- product quantity exceeds the allowed limit (e.g. `6` items for Bread).

Delivery fees should only be applied to **valid, payable baskets**.

### Steps to Reproduce

#### Case 1: Zero quantity

1. Create a new basket  
   `POST /api/baskets`

2. Add a product with zero quantity  
   `POST /api/baskets/{basketId}/items`

   ```json
   {
     "productId": "2",
     "quantity": 0
   }
   ```
3. Calculate the basket
   `GET /api/baskets/{basketId}/calculate`

#### Case 2: Quantity exceeds allowed limit (Bread)
1. Create a new basket
   `POST /api/baskets`

2. Add Bread with quantity greater than allowed
   `POST /api/baskets/{basketId}/items`
```json
{
  "productId": "1",
  "quantity": 6
}
```
3. Calculate the basket
   `GET /api/baskets/{basketId}/calculate`

### Actual Result
The basket calculation completes successfully
A €5 delivery fee is applied
The basket is treated as partially valid despite invalid quantities

Example response excerpt:
```json
{
  "deliveryFee": 5,
  "total": 5
}
```

### Expected Result
No delivery fee should be applied

Example expected behavior:
```json
{
  "deliveryFee": 0,
  "basketReadyToPay": false
}
```

## Bug #5: Incorrect price calculation for Chicken when quantity = 2

### Summary
When **2 Chicken items** are added to the basket, the calculated price does not match the expected total based on the unit price.

Instead of charging the full price for two items, the API returns an incorrect final amount.

### Business Expectation
Chicken has a unit price of **€12** and no special pricing rules defined for exactly two items.

For `quantity = 2`:
- Unit price: €12
- Line total: `2 × 12 = 24`
- **Expected final price: €24**

### Steps to Reproduce

1. Create a new basket  
   `POST /api/baskets`

2. Add Chicken to the basket  
   `POST /api/baskets/{basketId}/items`

   Request body:
   ```json
   {
     "productId": "3",
     "quantity": 2
   }
   ```
3. Calculate the basket
   `GET /api/baskets/{basketId}/calculate`

### Actual Result
The API returns the following calculation for the Chicken item:
```json
{
  "id": 3,
  "productName": "Chicken",
  "unitPrice": 12,
  "quantity": 2,
  "lineTotal": 24,
  "discount": 6,
  "finalPrice": 18
}
```
A discount is applied even though none is expected
Final price is €18 instead of €24
### Expected Result
```json
{
  "productName": "Chicken",
  "lineTotal": 24,
  "discount": 0,
  "finalPrice": 24
}
```
No discount should be applied

## Bug #6: String quantity is accepted and results in null pricing values

### Summary
The API accepts a **string literal** as the `quantity` value when adding a product to the basket.
As a result, the basket calculation completes successfully but returns **`null` values** for prices and totals.

This leads to an inconsistent and invalid basket state.

### Business Expectation
The `quantity` field must be a **positive integer**.

If a non-numeric value is provided:
- the request should be rejected with a validation error, or
- the calculation should fail explicitly.

The API should never return a successfully calculated basket with `null` monetary values.

### Steps to Reproduce

1. Create a new basket  
   `POST /api/baskets`

2. Add a product with a **string quantity**  
   `POST /api/baskets/{basketId}/items`

   Request body:
   ```json
   {
     "productId": "4",
     "quantity": "a"
   }
   ```
3. Calculate the basket
   `GET /api/baskets/{basketId}/calculate`

### Actual Result
The API responds with a successful status and the following data:
```json
{
  "quantity": "a",
  "lineTotal": null,
  "finalPrice": null,
  "subtotal": null,
  "total": null,
  "deliveryFee": 5,
  "basketReadyToPay": false
}
```
Invalid quantity value is accepted

### Expected Result
The request should be rejected with a validation error (e.g. 400 Bad Request)