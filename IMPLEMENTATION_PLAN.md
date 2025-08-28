# Product Service: Security Enhancement Implementation Plan

## Project Overview
This document outlines the plan to integrate the `productservice` with the `user-service` for robust authentication and authorization. The goal is to configure `productservice` as an OAuth2 Resource Server and Client, enabling it to protect its APIs and securely communicate with other services.

## 🎯 Target Architecture
- **OAuth2 Resource Server**: The `productservice` will protect its endpoints by validating JWTs issued by the `user-service`.
- **OAuth2 Client**: The `productservice` will authenticate itself with the `user-service` using the client credentials flow to consume internal APIs.

---

## 📋 Implementation Phases

### Phase 1: Dependency Setup (✅ COMPLETED)
Ensure the `pom.xml` in `productservice` has the necessary Spring Security dependencies.

- [x] **Add Dependencies**: Add `spring-boot-starter-security`, `spring-boot-starter-oauth2-resource-server`, and `spring-boot-starter-oauth2-client` to `pom.xml`.

---

### Phase 2: Resource Server Configuration (⏳ PENDING)
Configure `productservice` to validate incoming JWTs from clients (e.g., a frontend application).

- [x] **Configure `application.properties`**: Set the `spring.security.oauth2.resourceserver.jwt.issuer-uri` to point to the `user-service` (e.g., `http://localhost:8444`).
- [x] **Create Security Configuration**: Implement a `SecurityFilterChain` bean that:
    - Secures all relevant API endpoints (e.g., `/products/**`).
    - Configures the application as an OAuth2 resource server.
    - Disables CSRF and sets session management to `STATELESS`.

---

### Phase 3: Role-Based Access Control (RBAC) (✅ COMPLETED)
Implement fine-grained access control on API endpoints.

- [x] **Secure Endpoints with Annotations**: Use `@PreAuthorize` on controller methods to restrict access based on roles or authorities.
    - Example: Restrict product creation to users with the `ADMIN` role (`@PreAuthorize("hasRole('ADMIN')")`).
    - Example: Restrict product updates to users with a specific scope (`@PreAuthorize("hasAuthority('SCOPE_products.write')")`).

---

### Phase 4: Service-to-Service Communication (Client Credentials) (✅ COMPLETED)
Enable `productservice` to securely communicate with `user-service` for backend operations.

- [x] **Add Client Configuration**: Add the OAuth2 client credentials for `productservice` to `application.properties`.
- [x] **Create a Configured `WebClient`**: Create a `WebClient` bean that is automatically configured to handle the OAuth2 client credentials flow. This `WebClient` can then be used in your services to make authenticated calls to the `user-service`.
