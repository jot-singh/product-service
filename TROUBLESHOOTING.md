# Product Service Troubleshooting Guide

## Table of Contents

1. [Database Issues](#database-issues)
2. [Connection Problems](#connection-problems)
3. [Authentication Errors](#authentication-errors)
4. [Performance Issues](#performance-issues)
5. [Build & Compilation Errors](#build--compilation-errors)
6. [Docker Issues](#docker-issues)
7. [Redis/Caching Issues](#rediscaching-issues)
8. [Elasticsearch Issues](#elasticsearch-issues)
9. [Rate Limiting Issues](#rate-limiting-issues)
10. [Common Error Messages](#common-error-messages)

---

## Database Issues

### Problem: Connection Refused

**Error**:
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
The last packet sent successfully to the server was 0 milliseconds ago.
```

**Solutions**:

1. **Check if MySQL is running**:
   ```bash
   # macOS
   brew services list | grep mysql
   
   # Linux
   sudo systemctl status mysql
   
   # Check port
   lsof -i :3306
   ```

2. **Start MySQL**:
   ```bash
   # macOS
   brew services start mysql
   
   # Linux
   sudo systemctl start mysql
   
   # Docker
   docker compose up -d mysql
   ```

3. **Verify credentials in application.properties**:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/productdb
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

4. **Test connection manually**:
   ```bash
   mysql -u root -p -h localhost -P 3306
   ```

---

### Problem: Database Does Not Exist

**Error**:
```
java.sql.SQLSyntaxErrorException: Unknown database 'productdb'
```

**Solution**:
```bash
# Login to MySQL
mysql -u root -p

# Create database
CREATE DATABASE productdb;

# Verify
SHOW DATABASES;
```

---

### Problem: Access Denied

**Error**:
```
java.sql.SQLException: Access denied for user 'root'@'localhost'
```

**Solutions**:

1. **Reset MySQL password**:
   ```bash
   # macOS/Linux
   mysqladmin -u root password 'newpassword'
   
   # Or login as root
   mysql -u root
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'newpassword';
   FLUSH PRIVILEGES;
   ```

2. **Grant privileges**:
   ```sql
   GRANT ALL PRIVILEGES ON productdb.* TO 'root'@'localhost';
   FLUSH PRIVILEGES;
   ```

---

### Problem: Schema Migration Errors

**Error**:
```
Schema-validation: missing table [products]
```

**Solution**:

1. **Enable auto-DDL**:
   ```properties
   spring.jpa.hibernate.ddl-auto=update
   ```

2. **Or create tables manually**:
   ```sql
   SOURCE /path/to/schema.sql;
   ```

3. **Clean and rebuild**:
   ```bash
   ./mvnw clean install
   ./mvnw spring-boot:run
   ```

---

## Connection Problems

### Problem: Port Already in Use

**Error**:
```
Web server failed to start. Port 8080 was already in use.
```

**Solutions**:

1. **Find and kill the process**:
   ```bash
   # macOS/Linux
   lsof -ti:8080 | xargs kill -9
   
   # Or find PID first
   lsof -i :8080
   kill -9 <PID>
   ```

2. **Change port in application.properties**:
   ```properties
   server.port=8081
   ```

3. **Use a different profile**:
   ```bash
   ./mvnw spring-boot:run -Dserver.port=8081
   ```

---

### Problem: User Service Not Reachable

**Error**:
```
org.springframework.web.client.ResourceAccessException: I/O error on GET request for "http://localhost:8444"
Connection refused
```

**Solutions**:

1. **Start User Service first**:
   ```bash
   cd ../user-service
   ./mvnw spring-boot:run
   ```

2. **Verify User Service is running**:
   ```bash
   curl http://localhost:8444/actuator/health
   ```

3. **Check issuer URI in application.properties**:
   ```properties
   spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8444
   ```

4. **Update hosts file if using custom domain**:
   ```bash
   sudo echo "127.0.0.1 user-service" >> /etc/hosts
   ```

---

## Authentication Errors

### Problem: Invalid JWT Token

**Error**:
```
401 Unauthorized
{
  "error": "invalid_token",
  "error_description": "An error occurred while attempting to decode the Jwt"
}
```

**Solutions**:

1. **Get a new token from User Service**:
   ```bash
   curl -X POST http://localhost:8444/auth/login \
     -H "Content-Type: application/json" \
     -d '{
       "username": "user@example.com",
       "password": "password123"
     }'
   ```

2. **Verify token format**:
   ```bash
   # Token should be in format: Bearer <token>
   Authorization: Bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...
   ```

3. **Check token expiration**:
   - JWT tokens expire after a certain time
   - Request a new token if expired

4. **Verify issuer URI matches**:
   ```properties
   # In productservice application.properties
   spring.security.oauth2.resourceserver.jwt.issuer-uri=http://localhost:8444
   
   # Should match User Service issuer
   ```

---

### Problem: Forbidden Access

**Error**:
```
403 Forbidden
{
  "error": "insufficient_scope",
  "error_description": "The request requires higher privileges than provided"
}
```

**Solutions**:

1. **Check user roles/scopes**:
   - Ensure user has required permissions
   - Some endpoints require ADMIN role

2. **Request token with correct scopes**:
   ```bash
   # When logging in, ensure user has appropriate role
   # Check User Service for role configuration
   ```

---

## Performance Issues

### Problem: Slow Response Times

**Symptoms**:
- API responses taking >2 seconds
- Database queries slow

**Solutions**:

1. **Enable Redis caching**:
   ```properties
   redis.enabled=true
   spring.data.redis.host=localhost
   spring.data.redis.port=6379
   ```

2. **Check database indexes**:
   ```sql
   SHOW INDEX FROM products;
   
   # Add indexes if needed
   CREATE INDEX idx_product_name ON products(name);
   CREATE INDEX idx_category_id ON products(category_id);
   ```

3. **Enable connection pooling**:
   ```properties
   spring.datasource.hikari.maximum-pool-size=10
   spring.datasource.hikari.minimum-idle=5
   ```

4. **Monitor slow queries**:
   ```properties
   spring.jpa.properties.hibernate.generate_statistics=true
   logging.level.org.hibernate.stat=DEBUG
   ```

---

### Problem: High Memory Usage

**Solutions**:

1. **Increase JVM heap size**:
   ```bash
   java -Xms512m -Xmx2048m -jar productservice.jar
   ```

2. **Configure pagination for large datasets**:
   ```bash
   curl "http://localhost:8080/api/products?page=0&size=20"
   ```

3. **Clear local caches periodically**:
   ```bash
   curl -X POST http://localhost:8080/actuator/caches/clear
   ```

---

## Build & Compilation Errors

### Problem: Maven Build Fails

**Error**:
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin
```

**Solutions**:

1. **Clean and rebuild**:
   ```bash
   ./mvnw clean install -DskipTests
   ```

2. **Update dependencies**:
   ```bash
   ./mvnw dependency:resolve
   ./mvnw dependency:purge-local-repository
   ```

3. **Check Java version**:
   ```bash
   java -version
   # Should be Java 17 or higher
   ```

4. **Force update of snapshots**:
   ```bash
   ./mvnw clean install -U
   ```

---

### Problem: Duplicate Class Errors

**Error**:
```
error: duplicate class: com.dag.productservice.service.ProductService
```

**Solutions**:

1. **Clean target directory**:
   ```bash
   rm -rf target/
   ./mvnw clean compile
   ```

2. **Check for duplicate files**:
   ```bash
   find src/ -name "ProductService.java" -type f
   ```

3. **Remove duplicate packages**:
   - Check for both `service/` and `services/` directories
   - Consolidate into one package

---

### Problem: Test Failures

**Error**:
```
Tests run: 44, Failures: 2, Errors: 1
```

**Solutions**:

1. **Run tests with detailed output**:
   ```bash
   ./mvnw test -X
   ```

2. **Run specific test**:
   ```bash
   ./mvnw test -Dtest=ProductServiceTest
   ```

3. **Skip tests temporarily**:
   ```bash
   ./mvnw package -DskipTests
   ```

4. **Check test configuration**:
   ```properties
   # In src/test/resources/application.properties
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.jpa.hibernate.ddl-auto=create-drop
   ```

---

## Docker Issues

### Problem: Docker Build Fails

**Error**:
```
ERROR [stage-1 3/3] COPY target/*.jar app.jar
```

**Solutions**:

1. **Build JAR first**:
   ```bash
   ./mvnw clean package -DskipTests
   docker build -t productservice .
   ```

2. **Check Dockerfile location**:
   ```bash
   # Dockerfile should be in productservice/ root
   ls -la Dockerfile
   ```

3. **Use multi-stage build**:
   ```dockerfile
   FROM maven:3.8-openjdk-17 AS build
   WORKDIR /app
   COPY pom.xml .
   COPY src ./src
   RUN mvn clean package -DskipTests
   
   FROM openjdk:17-jdk-slim
   COPY --from=build /app/target/*.jar app.jar
   EXPOSE 8080
   ENTRYPOINT ["java", "-jar", "app.jar"]
   ```

---

### Problem: Container Cannot Connect to Host Database

**Error**:
```
com.mysql.cj.jdbc.exceptions.CommunicationsException: Communications link failure
```

**Solutions**:

1. **Use host.docker.internal**:
   ```bash
   docker run -p 8080:8080 \
     -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/productdb \
     productservice
   ```

2. **Or use Docker Compose with network**:
   ```yaml
   services:
     productservice:
       depends_on:
         - mysql
       environment:
         SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/productdb
   ```

3. **Check container network**:
   ```bash
   docker network ls
   docker network inspect <network_name>
   ```

---

## Redis/Caching Issues

### Problem: Redis Connection Failed

**Error**:
```
io.lettuce.core.RedisConnectionException: Unable to connect to localhost:6379
```

**Solutions**:

1. **Start Redis**:
   ```bash
   # macOS
   brew services start redis
   
   # Linux
   sudo systemctl start redis
   
   # Docker
   docker run -d -p 6379:6379 redis:7.2
   ```

2. **Verify Redis is running**:
   ```bash
   redis-cli ping
   # Should return: PONG
   ```

3. **Disable caching temporarily**:
   ```properties
   redis.enabled=false
   ```

---

### Problem: Cache Not Working

**Symptoms**:
- No performance improvement
- Database still being queried

**Solutions**:

1. **Verify cache configuration**:
   ```properties
   spring.cache.type=redis
   spring.data.redis.host=localhost
   spring.data.redis.port=6379
   redis.enabled=true
   ```

2. **Check cache hits**:
   ```bash
   redis-cli
   MONITOR
   # Make API calls and watch for cache operations
   ```

3. **Clear cache manually**:
   ```bash
   redis-cli FLUSHALL
   ```

---

## Elasticsearch Issues

### Problem: Elasticsearch Not Starting

**Error**:
```
org.elasticsearch.client.RestClient: Connection refused
```

**Solutions**:

1. **Start Elasticsearch**:
   ```bash
   docker run -d -p 9200:9200 \
     -e "discovery.type=single-node" \
     -e "xpack.security.enabled=false" \
     docker.elastic.co/elasticsearch/elasticsearch:8.8.2
   ```

2. **Verify Elasticsearch is running**:
   ```bash
   curl http://localhost:9200
   ```

3. **Disable search temporarily**:
   ```properties
   elasticsearch.enabled=false
   ```

---

### Problem: Index Not Found

**Error**:
```
index_not_found_exception: no such index [products]
```

**Solutions**:

1. **Create index manually**:
   ```bash
   curl -X PUT http://localhost:9200/products \
     -H "Content-Type: application/json" \
     -d '{
       "mappings": {
         "properties": {
           "title": {"type": "text"},
           "description": {"type": "text"},
           "price": {"type": "double"}
         }
       }
     }'
   ```

2. **Re-index all products**:
   ```bash
   curl -X POST http://localhost:8080/api/search/reindex
   ```

---

## Rate Limiting Issues

### Problem: Rate Limit Exceeded

**Error**:
```
429 Too Many Requests
{
  "message": "Rate limit exceeded. Please try again later.",
  "retryAfterSeconds": 60
}
```

**Solutions**:

1. **Wait for rate limit reset** (shown in `Retry-After` header)

2. **Increase rate limits** (development only):
   ```properties
   rate.limit.capacity=200
   rate.limit.tokens=20
   ```

3. **Disable rate limiting** (development only):
   ```properties
   rate.limit.enabled=false
   ```

4. **Use different IP or user account**

---

## Common Error Messages

### Error: "Could not autowire. No beans of type found"

**Solution**:
```java
// Ensure @Service, @Repository, or @Component annotation is present
@Service
public class ProductService {
    // ...
}

// Ensure component scanning is enabled
@SpringBootApplication
@ComponentScan(basePackages = "com.dag.productservice")
public class Application {
    // ...
}
```

---

### Error: "Failed to load ApplicationContext"

**Solutions**:

1. **Check for circular dependencies**:
   ```bash
   ./mvnw spring-boot:run -X
   # Look for circular dependency messages
   ```

2. **Verify all required properties are set**:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/productdb
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Check for syntax errors in configuration files**

---

### Error: "Whitelabel Error Page"

**Solutions**:

1. **Check controller mapping**:
   ```java
   @RestController
   @RequestMapping("/api/products")  // Ensure path is correct
   public class ProductController {
       // ...
   }
   ```

2. **Verify endpoint exists**:
   ```bash
   curl http://localhost:8080/actuator/mappings
   ```

3. **Check for typos in URL**

---

## Diagnostic Commands

### Check Application Health
```bash
curl http://localhost:8080/actuator/health
```

### View All Endpoints
```bash
curl http://localhost:8080/actuator/mappings | jq
```

### Check Environment Variables
```bash
curl http://localhost:8080/actuator/env | jq
```

### View Logs
```bash
# Real-time logs
./mvnw spring-boot:run

# Or if running as JAR
java -jar target/productservice-*.jar

# Docker logs
docker logs productservice -f
```

### Database Connection Test
```bash
curl http://localhost:8080/actuator/health/db
```

---

## Getting Help

If you're still experiencing issues:

1. **Check logs carefully** for stack traces and error messages
2. **Review documentation**: [DOCUMENTATION.md](DOCUMENTATION.md)
3. **Try the Quick Start**: [QUICK_START.md](QUICK_START.md)
4. **Check API specs**: [API_SPECIFICATION.md](API_SPECIFICATION.md)
5. **Review architecture**: [ARCHITECTURE_DIAGRAMS.md](ARCHITECTURE_DIAGRAMS.md)
6. **Search for similar issues** in the issue tracker
7. **Ask for help** with detailed error logs and steps to reproduce

---

## Debug Mode

Enable debug logging for troubleshooting:

```properties
# Application debug
logging.level.com.dag.productservice=DEBUG

# Spring debug
logging.level.org.springframework=DEBUG

# SQL debug
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Security debug
logging.level.org.springframework.security=DEBUG
```

Run with debug:
```bash
./mvnw spring-boot:run -Ddebug
```
