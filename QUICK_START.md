# Product Service Quick Start Guide 🚀

## Prerequisites

Before you begin, ensure you have the following installed:

- ☕ **Java 17** or higher
- 🏗️ **Maven 3.6+**
- 🗄️ **MySQL 8.0+** (or use Docker)
- 🔍 **Elasticsearch 8.8.2** (optional, for search features)
- 📦 **Redis 7.2** (optional, for caching)
- 🌐 **curl** or **Postman** for testing
- 🐚 **Terminal/Command Prompt**

---

## 1. Project Setup

### Clone and Navigate
```bash
cd /path/to/your/projects
git clone <repository-url>
cd productservice
```

### Verify Project Structure
```bash
ls -la
```
Expected output:
```
drwxr-xr-x  productservice/
├── pom.xml
├── src/
│   ├── main/
│   │   ├── java/
│   │   └── resources/
│   └── test/
├── target/
├── Dockerfile
├── Readme.md
└── DOCUMENTATION.md
```

---

## 2. Database Setup

### Option A: Using Docker Compose (Recommended)

Navigate to the project root and start all services:

```bash
cd ..
docker compose up -d
```

This starts:
- MySQL (port 3306)
- Elasticsearch (port 9200)
- Redis (port 6379)

### Option B: Manual MySQL Setup

```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE productdb;

# Create user (optional)
CREATE USER 'productuser'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON productdb.* TO 'productuser'@'localhost';
FLUSH PRIVILEGES;
```

---

## 3. Configuration

### Basic Configuration (Minimal)

Edit `src/main/resources/application.properties`:

```properties
# Server Configuration
spring.application.name=productservice
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/productdb
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update

# Disable Optional Features (if not using)
elasticsearch.enabled=false
redis.enabled=false
```

### Full Configuration (All Features)

```properties
# Server Configuration
spring.application.name=productservice
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/productdb
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Redis Configuration (Caching)
spring.data.redis.host=localhost
spring.data.redis.port=6379
redis.enabled=true
cache.ttl=3600

# Elasticsearch Configuration (Search)
spring.elasticsearch.uris=http://localhost:9200
elasticsearch.enabled=true

# OAuth2 Configuration (User Service Integration)
spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8444

# Stripe Payment Configuration
stripe.api.key=sk_test_your_stripe_key_here

# Rate Limiting
rate.limit.enabled=true
rate.limit.capacity=100
rate.limit.tokens=10
```

---

## 4. Build and Run

### Build the Application
```bash
./mvnw clean compile
```

### Run Tests
```bash
./mvnw test
```

Expected output:
```
Tests run: 44, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Run the Application
```bash
./mvnw spring-boot:run
```

### Verify Service is Running
```bash
curl http://localhost:8080/actuator/health
```

Expected response:
```json
{
  "status": "UP"
}
```

---

## 5. Quick API Testing

### Create a Category

```bash
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Electronics",
    "description": "Electronic devices and accessories"
  }'
```

### Create a Product

```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Laptop",
    "description": "High-performance laptop",
    "price": 999.99,
    "image": "https://example.com/laptop.jpg",
    "category": "Electronics"
  }'
```

### Get All Products

```bash
curl http://localhost:8080/api/products
```

### Get Single Product

```bash
curl http://localhost:8080/api/products/{productId}
```

### Search Products (if Elasticsearch enabled)

```bash
curl "http://localhost:8080/api/search/products?query=laptop"
```

---

## 6. Access API Documentation

Once the service is running, access Swagger UI:

```
http://localhost:8080/swagger-ui.html
```

Or view the OpenAPI specification:

```
http://localhost:8080/v3/api-docs
```

---

## 7. Docker Deployment

### Build Docker Image

```bash
docker build -t productservice:latest .
```

### Run with Docker

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/productdb \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  productservice:latest
```

### Using Docker Compose (Complete Stack)

From the project root:

```bash
docker compose up --build
```

This starts:
- productservice (port 8080)
- user-service (port 8444)
- MySQL
- Elasticsearch
- Redis

---

## 8. Verify Installation

### Health Check
```bash
curl http://localhost:8080/actuator/health
```

### Database Connection
```bash
curl http://localhost:8080/actuator/health/db
```

### Create Test Data
```bash
# Create category
curl -X POST http://localhost:8080/api/categories \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Category", "description": "Test Description"}'

# Verify creation
curl http://localhost:8080/api/categories
```

---

## 9. Environment Profiles

### Development Profile

Create `application-dev.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/productdb_dev
spring.jpa.show-sql=true
logging.level.com.dag.productservice=DEBUG
```

Run with:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Production Profile

Create `application-prod.properties`:

```properties
spring.datasource.url=jdbc:mysql://prod-server:3306/productdb
spring.jpa.show-sql=false
logging.level.com.dag.productservice=INFO
elasticsearch.enabled=true
redis.enabled=true
```

Run with:
```bash
java -jar target/productservice-*.jar --spring.profiles.active=prod
```

---

## 10. Common Issues

### Port Already in Use
```bash
# Find process using port 8080
lsof -i :8080

# Kill the process
kill -9 <PID>
```

### Database Connection Failed
- Verify MySQL is running: `mysql -u root -p`
- Check credentials in `application.properties`
- Ensure database exists: `SHOW DATABASES;`

### Maven Build Errors
```bash
# Clean and rebuild
./mvnw clean install -DskipTests

# Update dependencies
./mvnw dependency:resolve
```

---

## 11. Next Steps

1. ✅ **Read Documentation**: Check [DOCUMENTATION.md](DOCUMENTATION.md) for comprehensive technical details
2. ✅ **API Specification**: Review [API_SPECIFICATION.md](API_SPECIFICATION.md) for all endpoints
3. ✅ **Architecture**: Understand the system in [ARCHITECTURE_DIAGRAMS.md](ARCHITECTURE_DIAGRAMS.md)
4. ✅ **Troubleshooting**: Common issues in [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
5. ✅ **Integration**: Connect with [user-service](../user-service/README.md) for authentication

---

## 12. Quick Reference Commands

```bash
# Build
./mvnw clean compile

# Test
./mvnw test

# Run
./mvnw spring-boot:run

# Package
./mvnw package

# Skip tests
./mvnw package -DskipTests

# Run with profile
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Docker build
docker build -t productservice .

# Docker run
docker run -p 8080:8080 productservice
```

---

## Support

For issues and questions:
- 📖 Check [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
- 📚 Read [DOCUMENTATION.md](DOCUMENTATION.md)
- 🐛 Report bugs in the issue tracker

Happy coding! 🎉
