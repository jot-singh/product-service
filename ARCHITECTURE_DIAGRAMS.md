# Product Service Architecture Diagrams

## Table of Contents

1. [System Architecture](#system-architecture)
2. [Component Architecture](#component-architecture)
3. [Data Flow Diagrams](#data-flow-diagrams)
4. [Deployment Architecture](#deployment-architecture)
5. [Entity Relationships](#entity-relationships)
6. [Sequence Diagrams](#sequence-diagrams)
7. [Class Diagrams](#class-diagrams)

---

## System Architecture

### High-Level Architecture

```mermaid
graph TB
    subgraph "Client Layer"
        A[Web Client]
        B[Mobile Client]
        C[API Client]
    end

    subgraph "API Gateway"
        D[Load Balancer]
    end

    subgraph "Microservices"
        E[User Service<br/>Port: 8444]
        F[Product Service<br/>Port: 8080]
    end

    subgraph "Data Layer"
        G[(MySQL<br/>Products DB)]
        H[(MySQL<br/>Users DB)]
    end

    subgraph "Cache Layer"
        I[Redis<br/>Port: 6379]
    end

    subgraph "Search Layer"
        J[Elasticsearch<br/>Port: 9200]
    end

    subgraph "External Services"
        K[Stripe Payment<br/>Gateway]
    end

    A --> D
    B --> D
    C --> D
    D --> E
    D --> F
    F --> E
    F --> G
    F --> I
    F --> J
    F --> K
    E --> H
```

---

### Layered Architecture

```mermaid
graph TB
    subgraph "Presentation Layer"
        A1[ProductController]
        A2[CategoryController]
        A3[PaymentController]
        A4[SearchController]
    end

    subgraph "Service Layer"
        B1[ProductService]
        B2[CategoryService]
        B3[PaymentService]
        B4[SearchService]
        B5[CacheService]
        B6[RateLimitService]
    end

    subgraph "Data Access Layer"
        C1[ProductRepository]
        C2[CategoryRepository]
        C3[PriceRepository]
        C4[RedisTemplate]
        C5[ElasticsearchClient]
    end

    subgraph "Data Layer"
        D1[(MySQL)]
        D2[(Redis)]
        D3[(Elasticsearch)]
    end

    A1 --> B1
    A2 --> B2
    A3 --> B3
    A4 --> B4
    
    B1 --> C1
    B1 --> B5
    B2 --> C2
    B3 --> C3
    B4 --> C5
    B5 --> C4
    
    C1 --> D1
    C2 --> D1
    C3 --> D1
    C4 --> D2
    C5 --> D3
```

---

## Component Architecture

### Core Components

```mermaid
graph LR
    subgraph "Controllers"
        A[ProductController]
        B[CategoryController]
        C[PaymentController]
    end

    subgraph "Services"
        D[ProductService]
        E[CategoryService]
        F[PaymentService]
    end

    subgraph "Repositories"
        G[LocalProductRepository]
        H[CategoryRepository]
        I[PriceRepository]
    end

    subgraph "Models"
        J[Product]
        K[Category]
        L[Price]
    end

    A --> D
    B --> E
    C --> F
    D --> G
    E --> H
    F --> I
    G --> J
    H --> K
    I --> L
    J -.-> K
    J -.-> L
```

---

### Security Architecture

```mermaid
graph TB
    A[Client Request] --> B{Security Filter Chain}
    B --> C[JWT Authentication Filter]
    C --> D{Token Valid?}
    D -->|Yes| E[Extract Claims]
    D -->|No| F[401 Unauthorized]
    E --> G{Has Required Scope?}
    G -->|Yes| H[Rate Limit Check]
    G -->|No| I[403 Forbidden]
    H --> J{Within Limit?}
    J -->|Yes| K[Controller]
    J -->|No| L[429 Too Many Requests]
    K --> M[Service Layer]
    M --> N[Response]
```

---

## Data Flow Diagrams

### Product Creation Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as ProductController
    participant PS as ProductService
    participant CS as CacheService
    participant PR as ProductRepository
    participant DB as MySQL
    participant RD as Redis
    participant ES as Elasticsearch

    C->>PC: POST /api/products
    PC->>PS: createProduct(dto)
    PS->>PR: save(product)
    PR->>DB: INSERT INTO products
    DB-->>PR: Product entity
    PR-->>PS: Saved product
    PS->>CS: evictCache("products")
    CS->>RD: DEL products:*
    PS->>ES: indexProduct(product)
    ES-->>PS: Indexed
    PS-->>PC: ProductResponseDto
    PC-->>C: 201 Created
```

---

### Product Retrieval with Caching

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as ProductController
    participant RL as RateLimitService
    participant CS as CacheService
    participant PS as ProductService
    participant RD as Redis
    participant PR as ProductRepository
    participant DB as MySQL

    C->>PC: GET /api/products/{id}
    PC->>RL: checkRateLimit(clientIP)
    RL->>RD: tryConsume(bucket)
    alt Within Limit
        RD-->>RL: Success
        RL-->>PC: Allowed
        PC->>CS: getFromCache(id)
        CS->>RD: GET products:{id}
        alt Cache Hit
            RD-->>CS: Cached product
            CS-->>PC: Product
            PC-->>C: 200 OK
        else Cache Miss
            RD-->>CS: null
            CS->>PS: getProduct(id)
            PS->>PR: findById(id)
            PR->>DB: SELECT * FROM products
            DB-->>PR: Product data
            PR-->>PS: Product entity
            PS->>CS: saveToCache(product)
            CS->>RD: SET products:{id}
            PS-->>PC: Product
            PC-->>C: 200 OK
        end
    else Rate Limit Exceeded
        RD-->>RL: Failed
        RL-->>PC: Rate limit exceeded
        PC-->>C: 429 Too Many Requests
    end
```

---

### Payment Processing Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as PaymentController
    participant PS as PaymentService
    participant PR as ProductRepository
    participant DB as MySQL
    participant ST as Stripe API

    C->>PC: POST /api/payments
    PC->>PS: createPayment(dto)
    PS->>PR: findById(productId)
    PR->>DB: SELECT * FROM products
    DB-->>PR: Product data
    PR-->>PS: Product entity
    PS->>ST: Create Payment Session
    ST-->>PS: Session URL
    PS-->>PC: PaymentResponseDto
    PC-->>C: 200 OK + Payment URL
```

---

### Search Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant SC as SearchController
    participant SS as SearchService
    participant ES as Elasticsearch

    C->>SC: GET /api/search/products?query=laptop
    SC->>SS: searchProducts(query, filters)
    SS->>ES: Search request
    ES-->>SS: Search results
    SS-->>SC: ProductSearchResponse
    SC-->>C: 200 OK + Results
```

---

## Deployment Architecture

### Docker Compose Deployment

```mermaid
graph TB
    subgraph "Docker Network"
        A[Nginx<br/>Reverse Proxy<br/>Port: 80]
        
        subgraph "Application Services"
            B[User Service<br/>Port: 8444]
            C[Product Service<br/>Port: 8080]
        end
        
        subgraph "Data Services"
            D[MySQL<br/>Port: 3306]
            E[Redis<br/>Port: 6379]
            F[Elasticsearch<br/>Port: 9200]
        end
    end

    A -->|/api/users| B
    A -->|/api/products| C
    B --> D
    C --> D
    C --> E
    C --> F
```

---

### Kubernetes Deployment

```mermaid
graph TB
    subgraph "Kubernetes Cluster"
        A[Ingress Controller]
        
        subgraph "Application Namespace"
            B[User Service Pod<br/>Replicas: 3]
            C[Product Service Pod<br/>Replicas: 3]
        end
        
        subgraph "Data Namespace"
            D[MySQL StatefulSet<br/>Replicas: 3]
            E[Redis StatefulSet<br/>Replicas: 3]
            F[Elasticsearch StatefulSet<br/>Replicas: 3]
        end
        
        subgraph "Storage"
            G[PersistentVolume<br/>MySQL]
            H[PersistentVolume<br/>Elasticsearch]
        end
    end

    A --> B
    A --> C
    B --> D
    C --> D
    C --> E
    C --> F
    D --> G
    F --> H
```

---

## Entity Relationships

### Database Schema

```mermaid
erDiagram
    PRODUCT ||--o{ CATEGORY : belongs_to
    PRODUCT ||--o{ PRICE : has
    CATEGORY ||--o{ PRODUCT : contains

    PRODUCT {
        UUID id PK
        string name
        string title
        string description
        string image
        UUID category_id FK
        UUID price_id FK
        string createdBy
        datetime createdOn
        string modifiedBy
        datetime modifiedOn
        boolean isDeleted
    }

    CATEGORY {
        UUID id PK
        string name
        string description
        string createdBy
        datetime createdOn
        string modifiedBy
        datetime modifiedOn
        boolean isDeleted
    }

    PRICE {
        UUID id PK
        double price
        string currency
        string createdBy
        datetime createdOn
        string modifiedBy
        datetime modifiedOn
        boolean isDeleted
    }
```

---

## Sequence Diagrams

### OAuth2 Authentication

```mermaid
sequenceDiagram
    participant C as Client
    participant PS as Product Service
    participant US as User Service
    participant DB as User DB

    C->>US: POST /auth/login
    US->>DB: Validate credentials
    DB-->>US: User data
    US->>US: Generate JWT
    US-->>C: JWT Token
    C->>PS: GET /api/products<br/>(with JWT)
    PS->>PS: Validate JWT
    PS->>US: Verify Token
    US-->>PS: Token valid
    PS->>PS: Check scopes
    PS-->>C: Products data
```

---

### Error Handling Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant PC as ProductController
    participant PS as ProductService
    participant PR as ProductRepository
    participant EH as ExceptionHandler

    C->>PC: GET /api/products/invalid-id
    PC->>PS: getProduct(invalid-id)
    PS->>PR: findById(invalid-id)
    PR-->>PS: Optional.empty()
    PS->>PS: Throw ProductNotFoundException
    PS-->>EH: ProductNotFoundException
    EH->>EH: Handle exception
    EH-->>C: 404 Not Found<br/>Error Response
```

---

## Class Diagrams

### Domain Model

```mermaid
classDiagram
    class Product {
        -UUID id
        -String name
        -String title
        -String description
        -String image
        -Category category
        -Price price
        +getId()
        +getName()
        +getCategory()
    }

    class Category {
        -UUID id
        -String name
        -String description
        -List~Product~ products
        +getId()
        +getName()
        +getProducts()
    }

    class Price {
        -UUID id
        -Double price
        -String currency
        +getId()
        +getPrice()
        +getCurrency()
    }

    class V0 {
        <<abstract>>
        -String createdBy
        -LocalDateTime createdOn
        -String modifiedBy
        -LocalDateTime modifiedOn
        -Boolean isDeleted
    }

    V0 <|-- Product
    V0 <|-- Category
    V0 <|-- Price
    Product "1" --> "1" Category : belongs to
    Product "1" --> "1" Price : has
```

---

### Service Layer

```mermaid
classDiagram
    class ProductService {
        <<interface>>
        +getAllProducts() List~ProductResponseDto~
        +getProductById(String) ProductResponseDto
        +createProduct(ProductRequestDto) ProductResponseDto
        +updateProduct(String, ProductRequestDto) ProductResponseDto
        +deleteProduct(String) void
    }

    class ProductServiceImpl {
        -LocalProductRepository repository
        -CategoryRepository categoryRepository
        -CacheInvalidationService cacheService
        +getAllProducts() List~ProductResponseDto~
        +getProductById(String) ProductResponseDto
        +createProduct(ProductRequestDto) ProductResponseDto
    }

    class CachedLocalProductService {
        -ProductService productService
        -RedisTemplate redisTemplate
        @Cacheable getAllProducts()
        @Cacheable getProductById()
        @CacheEvict createProduct()
    }

    ProductService <|.. ProductServiceImpl
    ProductService <|.. CachedLocalProductService
    CachedLocalProductService --> ProductServiceImpl : delegates to
```

---

### Controller Layer

```mermaid
classDiagram
    class ProductController {
        -ProductService productService
        +getAllProducts() ResponseEntity
        +getProductById(String) ResponseEntity
        +createProduct(ProductRequestDto) ResponseEntity
        +updateProduct(String, ProductRequestDto) ResponseEntity
        +deleteProduct(String) ResponseEntity
    }

    class CategoryController {
        -CategoryService categoryService
        +getAllCategories() ResponseEntity
        +getCategoryById(String) ResponseEntity
        +createCategory(CategoryRequestDto) ResponseEntity
    }

    class PaymentController {
        -PaymentService paymentService
        +createPayment(PaymentRequestDto) ResponseEntity
    }

    ProductController --> ProductService
    CategoryController --> CategoryService
    PaymentController --> PaymentService
```

---

## Infrastructure Patterns

### Caching Strategy

```mermaid
graph TB
    A[Request] --> B{Check Cache}
    B -->|Hit| C[Return Cached Data]
    B -->|Miss| D[Query Database]
    D --> E[Store in Cache]
    E --> F[Return Data]
    
    G[Write Operation] --> H[Update Database]
    H --> I[Invalidate Cache]
    I --> J[Success Response]
```

---

### Rate Limiting Strategy

```mermaid
graph TB
    A[Client Request] --> B{Check Bucket}
    B -->|Tokens Available| C[Consume Token]
    C --> D[Process Request]
    D --> E[Refill Bucket]
    E --> F[Return Response]
    
    B -->|No Tokens| G[429 Rate Limit]
    G --> H[Return Retry-After]
```

---

## Monitoring & Observability

### Logging Architecture

```mermaid
graph LR
    A[Application] --> B[Logback]
    B --> C[Console]
    B --> D[File]
    B --> E[ELK Stack]
    E --> F[Elasticsearch]
    F --> G[Kibana]
```

---

### Metrics Collection

```mermaid
graph TB
    A[Product Service] --> B[Spring Actuator]
    B --> C[Prometheus]
    C --> D[Grafana]
    
    B --> E[Health Checks]
    B --> F[JVM Metrics]
    B --> G[HTTP Metrics]
    B --> H[Database Metrics]
```

---

## Additional Resources

- **Technical Documentation**: [DOCUMENTATION.md](DOCUMENTATION.md)
- **Quick Start Guide**: [QUICK_START.md](QUICK_START.md)
- **API Specification**: [API_SPECIFICATION.md](API_SPECIFICATION.md)
- **Troubleshooting**: [TROUBLESHOOTING.md](TROUBLESHOOTING.md)
- **Main README**: [README.md](README.md)
