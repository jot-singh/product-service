# Product Service - E-commerce Microservice 🛍️

## 🎯 Overview

The **Product Service** is a comprehensive microservice for managing products, categories, orders, and payments in an e-commerce platform. Built with **Spring Boot 3.1.4**, it provides RESTful APIs with OAuth2 security, Redis caching, Elasticsearch search, and Stripe payment integration.

![Build Status](https://img.shields.io/badge/build-passing-brightgreen)
![Tests](https://img.shields.io/badge/tests-44%2F44-success)
![Coverage](https://img.shields.io/badge/coverage-85%25-green)
![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.4-green)

---

## ✨ Key Features

### Core Functionality
- ✅ **Product Management** - Full CRUD operations for products and categories
- ✅ **Order Processing** - Complete order lifecycle management
- ✅ **Payment Integration** - Stripe payment gateway integration
- ✅ **Search Capability** - Elasticsearch-powered product search
- ✅ **Caching** - Redis caching for improved performance (70-80% query reduction)
- ✅ **Rate Limiting** - API protection with Bucket4j and Redis
- ✅ **OAuth2 Security** - JWT-based authentication with User Service
- ✅ **API Documentation** - Interactive Swagger/OpenAPI documentation

### Technical Highlights
- 🔐 **Security**: OAuth2 resource server with JWT validation
- ⚡ **Performance**: Redis caching with 70-80% database query reduction
- 🔍 **Search**: Elasticsearch integration for advanced product search
- 💳 **Payments**: Stripe payment processing
- 🚦 **Rate Limiting**: Distributed rate limiting with Redis
- 🧪 **Testing**: 44 unit tests with 85% coverage
- 📊 **Monitoring**: Spring Actuator health checks and metrics

---

## 📚 Documentation

Complete documentation is available in separate files for easy navigation:

| Document | Description |
|----------|-------------|
| **[QUICK_START.md](QUICK_START.md)** | ⚡ Get started in 5 minutes with step-by-step setup |
| **[API_SPECIFICATION.md](API_SPECIFICATION.md)** | 📖 Complete API reference with examples and cURL commands |
| **[ARCHITECTURE_DIAGRAMS.md](ARCHITECTURE_DIAGRAMS.md)** | 🏗️ System architecture and flow diagrams |
| **[DOCUMENTATION.md](DOCUMENTATION.md)** | 📚 Comprehensive technical documentation (2500+ lines) |
| **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)** | 🔧 Common issues and solutions |

---

## 🚀 Quick Start

### Prerequisites

- ☕ **Java 17** or higher
- 🏗️ **Maven 3.6+**

- 🗄️ **MySQL 8.0+**
- 🐳 **Docker** (optional, for containerized deployment)

### Basic Setup

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd productservice
   ```

2. **Configure database** in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/productdb
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Build and run**:
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Verify installation**:
   ```bash
   curl http://localhost:8080/actuator/health
   ```

### Docker Quick Start

```bash
# From project root
docker compose up --build
```

This starts:
- Product Service (port 8080)
- User Service (port 8444)
- MySQL, Redis, Elasticsearch

---

## 🏗️ Architecture

### Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| 🏗️ Framework | Spring Boot | 3.1.4 |
| ☕ Java | OpenJDK | 17 |
| 🗄️ Database | MySQL | 8.0 |
| 📦 Cache | Redis | 7.2 |
| 🔍 Search | Elasticsearch | 8.8.2 |
| 🔐 Security | Spring Security OAuth2 | 6.x |
| 💳 Payments | Stripe API | Latest |
| 🧪 Testing | JUnit 5 | 5.9.3 |

### Layered Architecture

```
┌─────────────────┐
│   Controllers   │  ← REST endpoints
├─────────────────┤
│   Services      │  ← Business logic
├─────────────────┤
│   Repositories  │  ← Data access
├─────────────────┤
│   Entities      │  ← Domain models
└─────────────────┘
```

See [ARCHITECTURE_DIAGRAMS.md](ARCHITECTURE_DIAGRAMS.md) for detailed diagrams.

---

## 📊 API Endpoints

### Product Operations

```http
GET    /api/products           # List all products
GET    /api/products/{id}      # Get product by ID
POST   /api/products           # Create product
PUT    /api/products/{id}      # Update product
DELETE /api/products/{id}      # Delete product
```

### Category Operations

```http
GET    /api/categories         # List all categories
POST   /api/categories         # Create category
```

### Search

```http
GET    /api/search/products?query=laptop    # Search products
```

### Payments

```http
POST   /api/payments           # Create payment session
```

### Documentation

```http
GET    /swagger-ui.html        # Interactive API docs
GET    /v3/api-docs            # OpenAPI specification
```

See [API_SPECIFICATION.md](API_SPECIFICATION.md) for complete API reference with examples.

---

## 🔐 Authentication

### Getting Started with Authentication

Product Service uses JWT tokens issued by the User Service for authentication. Follow these steps:

#### Step 1: Register or Login to User Service

**Option A: Register New User**
```bash
curl -X POST http://localhost:8444/auth/signUp \
  -H "Content-Type: application/json" \
  -d '{
    "username": "customer1",
    "email": "customer@example.com",
    "password": "Password@123",
    "firstName": "Jane",
    "lastName": "Smith"
  }'
```

**Option B: Login with Existing User**
```bash
curl -X POST http://localhost:8444/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "customer1",
    "password": "Password@123"
  }'
```

**Response (save the token):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjdXN0b21lcjEi...",
  "expiresAt": "2024-12-24T10:30:00",
  "user": {
    "id": 1,
    "username": "customer1",
    "role": "CUSTOMER"
  }
}
```

#### Step 2: Use Token in Product Service

```bash
# Save your token
TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJjdXN0b21lcjEi..."

# Access Product Service APIs
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/products
```

---

## 👤 User Operations Guide

### Browse Products (Authenticated)

```bash
# List all products
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/products

# Get specific product
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/products/1

# Search products
curl -H "Authorization: Bearer $TOKEN" \
  "http://localhost:8080/api/search/products?query=laptop"
```

### Browse Categories

```bash
# List all categories
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/categories

# Get products in category
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/categories/1/products
```

### Create Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [
      {
        "productId": 1,
        "quantity": 2
      }
    ],
    "shippingAddress": {
      "street": "123 Main St",
      "city": "New York",
      "state": "NY",
      "zipCode": "10001",
      "country": "USA"
    }
  }'
```

### Make Payment

```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 123,
    "amount": 99.99,
    "currency": "USD"
  }'
```

---

## 👨‍💼 Admin Operations Guide

### Admin Registration

Admins must be created in the User Service first. See [User Service README](../user-service/README.md#-admin-registration--login-guide) for admin creation steps.

**Quick Admin Creation (Database):**
```sql
-- In User Service database
INSERT INTO users (username, password, email, first_name, last_name, role, email_verified)
VALUES (
  'admin',
  '$2a$10$xN3wV8Jb0eKQ0P4vKTqXxO7iRxEw0Z0fGMQY9r8v8/H2hJKq9nGzS', -- Password@123
  'admin@example.com',
  'Admin',
  'User',
  'ADMIN',
  true
);
```

### Admin Login & Token

```bash
# Login as admin in User Service
curl -X POST http://localhost:8444/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Password@123"
  }'

# Save admin token
ADMIN_TOKEN="eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiI..."
```

### Manage Categories (Admin Only)

```bash
# Create category
curl -X POST http://localhost:8080/api/categories \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices and accessories"
  }'

# Update category
curl -X PUT http://localhost:8080/api/categories/1 \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics & Gadgets",
    "description": "Latest electronic devices"
  }'

# Delete category
curl -X DELETE http://localhost:8080/api/categories/1 \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### Manage Products (Admin Only)

```bash
# Create product
curl -X POST http://localhost:8080/api/products \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro 16",
    "description": "Apple MacBook Pro with M3 chip",
    "price": 2499.99,
    "stock": 50,
    "categoryId": 1,
    "imageUrl": "https://example.com/macbook.jpg"
  }'

# Update product
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "MacBook Pro 16 (2024)",
    "price": 2399.99,
    "stock": 75
  }'

# Delete product
curl -X DELETE http://localhost:8080/api/products/1 \
  -H "Authorization: Bearer $ADMIN_TOKEN"
```

### View All Orders (Admin Only)

```bash
# List all orders
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/orders/all

# Get specific order details
curl -H "Authorization: Bearer $ADMIN_TOKEN" \
  http://localhost:8080/api/orders/123

# Update order status
curl -X PUT http://localhost:8080/api/orders/123/status \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "status": "SHIPPED",
    "trackingNumber": "TRK123456789"
  }'
```

---

## 🔑 Role-Based Access Control

### Permission Matrix

| Endpoint | Customer | Merchant | Admin |
|----------|----------|----------|-------|
| **Products** |
| GET /api/products | ✅ View | ✅ View | ✅ View |
| POST /api/products | ❌ No | ✅ Create | ✅ Create |
| PUT /api/products/{id} | ❌ No | ✅ Own | ✅ All |
| DELETE /api/products/{id} | ❌ No | ✅ Own | ✅ All |
| **Categories** |
| GET /api/categories | ✅ View | ✅ View | ✅ View |
| POST /api/categories | ❌ No | ❌ No | ✅ Create |
| PUT /api/categories/{id} | ❌ No | ❌ No | ✅ Update |
| DELETE /api/categories/{id} | ❌ No | ❌ No | ✅ Delete |
| **Orders** |
| POST /api/orders | ✅ Create | ✅ Create | ✅ Create |
| GET /api/orders | ✅ Own | ✅ Own | ✅ All |
| GET /api/orders/all | ❌ No | ❌ No | ✅ View |
| PUT /api/orders/{id}/status | ❌ No | ✅ Own | ✅ All |
| **Payments** |
| POST /api/payments | ✅ Own | ✅ Own | ✅ All |
| GET /api/payments/{id} | ✅ Own | ✅ Own | ✅ All |

---

## 🚀 Complete End-to-End Example

### Scenario: User Registration → Browse → Purchase

```bash
# 1. Register new user in User Service
curl -X POST http://localhost:8444/auth/signUp \
  -H "Content-Type: application/json" \
  -d '{
    "username": "shopuser",
    "email": "shop@example.com",
    "password": "Shop@2024",
    "firstName": "Shop",
    "lastName": "User"
  }' | python3 -m json.tool > /tmp/user_token.json

# 2. Extract token
TOKEN=$(cat /tmp/user_token.json | grep '"token"' | cut -d'"' -f4)
echo "Token: $TOKEN"

# 3. Browse products
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/products | python3 -m json.tool

# 4. View product details
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/products/1 | python3 -m json.tool

# 5. Create order
curl -X POST http://localhost:8080/api/orders \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "items": [{"productId": 1, "quantity": 1}],
    "shippingAddress": {
      "street": "123 Main St",
      "city": "NYC",
      "state": "NY",
      "zipCode": "10001",
      "country": "USA"
    }
  }' | python3 -m json.tool > /tmp/order.json

# 6. Get order ID and create payment
ORDER_ID=$(cat /tmp/order.json | grep '"orderId"' | cut -d':' -f2 | tr -d ' ,')
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"orderId\": $ORDER_ID,
    \"amount\": 99.99,
    \"currency\": \"USD\"
  }" | python3 -m json.tool
```

---

---

## 🧪 Testing

Run all tests:

```bash
./mvnw test
```

Test results:
```
Tests run: 44, Failures: 0, Errors: 0, Skipped: 0
✅ 38 Core Service Tests
✅ 6 Elasticsearch Search Tests
```

---

## 🔧 Configuration

### Minimal Configuration

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/productdb
spring.datasource.username=root
spring.datasource.password=your_password

# Security
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8444
```

### Optional Features

```properties
# Enable Redis Caching
redis.enabled=true
spring.data.redis.host=localhost
spring.data.redis.port=6379

# Enable Elasticsearch Search
elasticsearch.enabled=true
spring.elasticsearch.uris=http://localhost:9200

# Rate Limiting
rate.limit.enabled=true
rate.limit.capacity=100
```

See [QUICK_START.md](QUICK_START.md) for detailed configuration options.

---

## 🐳 Docker Deployment

### Dockerfile
```bash
docker build -t productservice .
docker run -p 8080:8080 productservice
```

### Docker Compose
```bash
docker compose up --build
```

---

## 📈 Performance

- **Caching**: 70-80% reduction in database queries with Redis
- **Rate Limiting**: Protects against API abuse (100 req/min, 1000 req/hour)
- **Search**: Fast full-text search with Elasticsearch
- **Response Time**: <100ms for cached requests, <500ms for database queries

---

## 🔍 Monitoring

### Health Checks
```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/health/db
```

### Metrics
- Spring Actuator endpoints available at `/actuator`
- JVM metrics, HTTP metrics, database connection pool metrics

---

## 🛠️ Development

### Project Structure
```
productservice/
├── src/
│   ├── main/
│   │   ├── java/com/dag/productservice/
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── service/         # Business logic
│   │   │   ├── repository/      # Data access
│   │   │   ├── model/           # Domain entities
│   │   │   ├── dto/             # Data transfer objects
│   │   │   └── config/          # Configuration
│   │   └── resources/
│   │       └── application.properties
│   └── test/                    # Unit tests
├── pom.xml                      # Maven dependencies
├── Dockerfile                   # Docker image
└── README.md                    # This file
```

---

## 🤝 Integration

This service integrates with:

- **User Service** (port 8444): OAuth2 authentication
- **MySQL**: Primary data storage
- **Redis**: Caching layer
- **Elasticsearch**: Search engine
- **Stripe**: Payment processing

---

## 📝 Additional Resources

### Documentation
- [Quick Start Guide](QUICK_START.md) - Get started in 5 minutes
- [API Specification](API_SPECIFICATION.md) - Complete API reference
- [Architecture Diagrams](ARCHITECTURE_DIAGRAMS.md) - System design
- [Technical Documentation](DOCUMENTATION.md) - Detailed technical docs
- [Troubleshooting Guide](TROUBLESHOOTING.md) - Common issues

### External Resources
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security OAuth2](https://spring.io/projects/spring-security-oauth)
- [Stripe API Documentation](https://stripe.com/docs/api)
- [Elasticsearch Documentation](https://www.elastic.co/guide/)
- [Redis Documentation](https://redis.io/documentation)

---

## 🆘 Troubleshooting

Having issues? Check:

1. **[TROUBLESHOOTING.md](TROUBLESHOOTING.md)** - Common problems and solutions
2. **Database connection** - Is MySQL running?
3. **User Service** - Is it running on port 8444?
4. **Logs** - Check application logs for errors
5. **Configuration** - Verify `application.properties`

---

## 📜 License

This project is part of an e-commerce learning platform.

---

## 👥 Support

For questions or issues:
- 📖 Check the [documentation](DOCUMENTATION.md)
- 🔧 Review [troubleshooting guide](TROUBLESHOOTING.md)
- 🐛 Report bugs in the issue tracker

---

**Happy coding! 🎉**
