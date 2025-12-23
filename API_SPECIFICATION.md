# Product Service API Specification

## Base Information

- **Base URL**: `http://localhost:8080`
- **API Version**: v1
- **Content-Type**: `application/json`
- **Authentication**: Bearer JWT Token

---

## Authentication

All endpoints except health checks require a valid JWT token from the User Service.

### Headers
```http
Authorization: Bearer <jwt_token>
Content-Type: application/json
```

---

## Product Endpoints

### 1. Get All Products

```http
GET /api/products
```

**Description**: Retrieve all products with pagination support

**Query Parameters**:
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)
- `sort` (optional): Sort field (default: name)

**Response** (200 OK):
```json
[
  {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "title": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "image": "https://example.com/laptop.jpg",
    "category": "Electronics"
  }
]
```

**cURL Example**:
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/products
```

---

### 2. Get Single Product

```http
GET /api/products/{id}
```

**Path Parameters**:
- `id` (required): Product UUID

**Response** (200 OK):
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "title": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "image": "https://example.com/laptop.jpg",
  "category": "Electronics"
}
```

**Response** (404 Not Found):
```json
{
  "timestamp": "2025-12-23T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 3fa85f64-5717-4562-b3fc-2c963f66afa6"
}
```

**cURL Example**:
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/products/3fa85f64-5717-4562-b3fc-2c963f66afa6
```

---

### 3. Create Product

```http
POST /api/products
```

**Request Body**:
```json
{
  "title": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "image": "https://example.com/laptop.jpg",
  "category": "Electronics"
}
```

**Response** (201 Created):
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "title": "Laptop",
  "description": "High-performance laptop",
  "price": 999.99,
  "image": "https://example.com/laptop.jpg",
  "category": "Electronics"
}
```

**cURL Example**:
```bash
curl -X POST \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "image": "https://example.com/laptop.jpg",
    "category": "Electronics"
  }' \
  http://localhost:8080/api/products
```

---

### 4. Update Product

```http
PUT /api/products/{id}
```

**Path Parameters**:
- `id` (required): Product UUID

**Request Body**:
```json
{
  "title": "Gaming Laptop",
  "description": "High-performance gaming laptop",
  "price": 1299.99,
  "image": "https://example.com/gaming-laptop.jpg",
  "category": "Electronics"
}
```

**Response** (200 OK):
```json
{
  "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "title": "Gaming Laptop",
  "description": "High-performance gaming laptop",
  "price": 1299.99,
  "image": "https://example.com/gaming-laptop.jpg",
  "category": "Electronics"
}
```

**cURL Example**:
```bash
curl -X PUT \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Gaming Laptop",
    "description": "High-performance gaming laptop",
    "price": 1299.99,
    "image": "https://example.com/gaming-laptop.jpg",
    "category": "Electronics"
  }' \
  http://localhost:8080/api/products/3fa85f64-5717-4562-b3fc-2c963f66afa6
```

---

### 5. Delete Product

```http
DELETE /api/products/{id}
```

**Path Parameters**:
- `id` (required): Product UUID

**Response** (204 No Content)

**cURL Example**:
```bash
curl -X DELETE \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/products/3fa85f64-5717-4562-b3fc-2c963f66afa6
```

---

## Category Endpoints

### 1. Get All Categories

```http
GET /api/categories
```

**Response** (200 OK):
```json
[
  {
    "id": "1a2b3c4d-5e6f-7890-abcd-ef1234567890",
    "name": "Electronics",
    "description": "Electronic devices and accessories"
  }
]
```

**cURL Example**:
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/categories
```

---

### 2. Get Single Category

```http
GET /api/categories/{id}
```

**Path Parameters**:
- `id` (required): Category UUID

**Response** (200 OK):
```json
{
  "id": "1a2b3c4d-5e6f-7890-abcd-ef1234567890",
  "name": "Electronics",
  "description": "Electronic devices and accessories"
}
```

**cURL Example**:
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/categories/1a2b3c4d-5e6f-7890-abcd-ef1234567890
```

---

### 3. Create Category

```http
POST /api/categories
```

**Request Body**:
```json
{
  "name": "Electronics",
  "description": "Electronic devices and accessories"
}
```

**Response** (201 Created):
```json
{
  "id": "1a2b3c4d-5e6f-7890-abcd-ef1234567890",
  "name": "Electronics",
  "description": "Electronic devices and accessories"
}
```

**cURL Example**:
```bash
curl -X POST \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices and accessories"
  }' \
  http://localhost:8080/api/categories
```

---

### 4. Update Category

```http
PUT /api/categories/{id}
```

**Path Parameters**:
- `id` (required): Category UUID

**Request Body**:
```json
{
  "name": "Consumer Electronics",
  "description": "Consumer electronic devices"
}
```

**Response** (200 OK):
```json
{
  "id": "1a2b3c4d-5e6f-7890-abcd-ef1234567890",
  "name": "Consumer Electronics",
  "description": "Consumer electronic devices"
}
```

**cURL Example**:
```bash
curl -X PUT \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Consumer Electronics",
    "description": "Consumer electronic devices"
  }' \
  http://localhost:8080/api/categories/1a2b3c4d-5e6f-7890-abcd-ef1234567890
```

---

### 5. Delete Category

```http
DELETE /api/categories/{id}
```

**Path Parameters**:
- `id` (required): Category UUID

**Response** (204 No Content)

**cURL Example**:
```bash
curl -X DELETE \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/categories/1a2b3c4d-5e6f-7890-abcd-ef1234567890
```

---

## Search Endpoints

### 1. Search Products

```http
GET /api/search/products
```

**Query Parameters**:
- `query` (required): Search term
- `category` (optional): Filter by category
- `minPrice` (optional): Minimum price
- `maxPrice` (optional): Maximum price
- `page` (optional): Page number (default: 0)
- `size` (optional): Page size (default: 20)

**Response** (200 OK):
```json
{
  "content": [
    {
      "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
      "title": "Laptop",
      "description": "High-performance laptop",
      "price": 999.99,
      "image": "https://example.com/laptop.jpg",
      "category": "Electronics"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

**cURL Example**:
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  "http://localhost:8080/api/search/products?query=laptop&minPrice=500&maxPrice=1500"
```

---

### 2. Search Products by Category

```http
GET /api/search/products/category/{category}
```

**Path Parameters**:
- `category` (required): Category name

**Response** (200 OK):
```json
[
  {
    "id": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "title": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "image": "https://example.com/laptop.jpg",
    "category": "Electronics"
  }
]
```

**cURL Example**:
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/search/products/category/Electronics
```

---

## Payment Endpoints

### 1. Create Payment

```http
POST /api/payments
```

**Request Body**:
```json
{
  "productId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "quantity": 1
}
```

**Response** (200 OK):
```json
{
  "paymentUrl": "https://checkout.stripe.com/pay/cs_test_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0",
  "sessionId": "cs_test_a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0",
  "expiresAt": "2025-12-23T11:30:00Z"
}
```

**cURL Example**:
```bash
curl -X POST \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "productId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "quantity": 1
  }' \
  http://localhost:8080/api/payments
```

---

## Order Endpoints

### 1. Create Order

```http
POST /api/orders
```

**Request Body**:
```json
{
  "userId": "user123",
  "productId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "quantity": 2,
  "shippingAddress": {
    "street": "123 Main St",
    "city": "San Francisco",
    "state": "CA",
    "zipCode": "94102",
    "country": "USA"
  }
}
```

**Response** (201 Created):
```json
{
  "orderId": "order-uuid-123",
  "userId": "user123",
  "productId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "quantity": 2,
  "totalAmount": 1999.98,
  "status": "PENDING",
  "createdAt": "2025-12-23T10:30:00Z"
}
```

**cURL Example**:
```bash
curl -X POST \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "productId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
    "quantity": 2,
    "shippingAddress": {
      "street": "123 Main St",
      "city": "San Francisco",
      "state": "CA",
      "zipCode": "94102",
      "country": "USA"
    }
  }' \
  http://localhost:8080/api/orders
```

---

### 2. Get Order by ID

```http
GET /api/orders/{orderId}
```

**Path Parameters**:
- `orderId` (required): Order UUID

**Response** (200 OK):
```json
{
  "orderId": "order-uuid-123",
  "userId": "user123",
  "productId": "3fa85f64-5717-4562-b3fc-2c963f66afa6",
  "quantity": 2,
  "totalAmount": 1999.98,
  "status": "SHIPPED",
  "createdAt": "2025-12-23T10:30:00Z",
  "updatedAt": "2025-12-23T14:00:00Z"
}
```

**cURL Example**:
```bash
curl -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  http://localhost:8080/api/orders/order-uuid-123
```

---

## Health & Monitoring

### 1. Health Check

```http
GET /actuator/health
```

**Response** (200 OK):
```json
{
  "status": "UP"
}
```

**cURL Example**:
```bash
curl http://localhost:8080/actuator/health
```

---

### 2. Database Health

```http
GET /actuator/health/db
```

**Response** (200 OK):
```json
{
  "status": "UP",
  "details": {
    "database": "MySQL",
    "validationQuery": "isValid()"
  }
}
```

---

## Error Responses

### Standard Error Format

All errors follow this format:

```json
{
  "timestamp": "2025-12-23T10:30:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/products"
}
```

### HTTP Status Codes

| Code | Description | When It Occurs |
|------|-------------|----------------|
| 200 | OK | Successful GET/PUT request |
| 201 | Created | Successful POST request |
| 204 | No Content | Successful DELETE request |
| 400 | Bad Request | Invalid request data |
| 401 | Unauthorized | Missing or invalid JWT token |
| 403 | Forbidden | Insufficient permissions |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Resource already exists |
| 429 | Too Many Requests | Rate limit exceeded |
| 500 | Internal Server Error | Server error |

---

## Rate Limiting

Rate limits are applied per IP address:

- **Standard**: 100 requests/minute
- **Burst**: 1000 requests/hour

**Rate Limit Headers**:
```http
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 95
X-RateLimit-Reset: 1703331600
```

**Rate Limit Exceeded** (429):
```json
{
  "timestamp": "2025-12-23T10:30:00Z",
  "status": 429,
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Please try again later.",
  "retryAfterSeconds": 60
}
```

---

## Pagination

List endpoints support pagination:

**Request**:
```http
GET /api/products?page=0&size=20&sort=name,asc
```

**Response**:
```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 100,
  "totalPages": 5,
  "last": false,
  "first": true
}
```

---

## Testing with Postman

### Import Collection

1. Create new collection: "Product Service"
2. Add base URL variable: `{{baseUrl}}` = `http://localhost:8080`
3. Add authorization header: `Authorization: Bearer {{token}}`

### Sample Collection Structure

```
Product Service/
├── Products/
│   ├── Get All Products
│   ├── Get Single Product
│   ├── Create Product
│   ├── Update Product
│   └── Delete Product
├── Categories/
│   ├── Get All Categories
│   └── Create Category
├── Search/
│   └── Search Products
└── Payments/
    └── Create Payment
```

---

## Swagger/OpenAPI Documentation

Interactive API documentation is available at:

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

---

## Additional Resources

- **Main README**: [README.md](README.md)
- **Quick Start Guide**: [QUICK_START.md](QUICK_START.md)
- **Technical Documentation**: [DOCUMENTATION.md](DOCUMENTATION.md)
- **Architecture Diagrams**: [ARCHITECTURE_DIAGRAMS.md](ARCHITECTURE_DIAGRAMS.md)
- **Troubleshooting**: [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
