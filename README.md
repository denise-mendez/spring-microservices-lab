# spring-microservices-lab

A hands-on portfolio repository for practicing modern Java, Spring Boot, microservices, cloud-native development, security, observability, and deployment concepts.

The repository is organized as a series of labs. Each one focuses on a specific concept and contains its own application and configuration.

## Objectives

The goals of this repository are to:

- Build practical experience with Spring Boot and microservices
- Practice clean REST API design
- Apply layered application architecture
- Implement authentication and authorization
- Document APIs using OpenAPI
- Containerize applications with Docker
- Work with cloud services
- Explore service discovery and centralized configuration
- Implement API gateways
- Add request tracing and correlation IDs
- Deploy applications to Kubernetes
- Practice Kubernetes scaling and rolling updates
- Apply automated testing at different application layers
- Maintain clean Git history and technical documentation


## Technologies

Technologies explored throughout the labs include:

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- OAuth 2.0
- OpenAPI
- Maven
- JUnit
- Mockito
- MockMvc
- H2
- Docker
- Docker Compose
- AWS
- Netflix Eureka
- Spring Cloud Config
- Spring Cloud Gateway
- Kubernetes

The exact technologies depend on the scope of each lab.

## Repository Structure

```text
spring-microservices-lab/
│
├── 01-rest-api-jpa/
├── 02-jwt-security/
├── 03-oauth2-resource-server/
├── 04-openapi/
├── 05-docker-compose/
├── 06-s3-file-storage/
├── 07-eureka-discovery/
├── 08-config-server/
├── 09-api-gateway/
├── 10-correlation-id/
├── 11-kubernetes-deployment/
├── 12-kubernetes-scaling/
└── 13-rolling-update/
```

Each lab is designed as an independent exercise focused on a particular concept.

## Lab Roadmap

### 01 - REST API with JPA

**Status:** Implemented

Focus:

- RESTful API design
- Spring MVC
- Layered architecture
- DTOs
- Request validation
- Spring Data JPA
- H2
- Derived repository queries
- Global exception handling
- HTTP status codes
- JUnit
- Mockito
- MockMvc
- JPA testing

Implemented operations:

```text
POST   /api/stories
GET    /api/stories
GET    /api/stories/{id}
GET    /api/stories?status=?
PUT    /api/stories/{id}
DELETE /api/stories/{id}
```

# 02 - JWT Security

**Status:** Implemented

This lab extends the Story REST API from `01-rest-api-jpa` by adding stateless authentication and authorization with Spring Security and JWT.

The main security flow is:

```text
Client
   │
   │ username + password
   ▼
AuthenticationManager
   │
   ▼
JWT
   │
   │ Authorization: Bearer <JWT>
   ▼
JwtAuthenticationFilter
   │
   ▼
SecurityContext
   │
   ▼
Role Authorization
   │
   ▼
Story Ownership / Visibility
```

## Focus

### Authentication

- User registration
- BCrypt password encoding
- Username/password authentication
- JWT generation
- JWT signature and expiration validation
- Bearer token authentication
- Stateless session configuration
- JWT authentication filter
- Spring Security `SecurityContext`

Authentication endpoints:

```text
POST /api/auth/register
POST /api/auth/login
```

Public registration automatically creates a:

```text
USER
```

The client cannot self-assign `AUTHOR` or `ADMIN`.

### Authorization

Three roles are supported:

- `USER` can consume published content.
- `AUTHOR` can create Stories and manage their own Stories.
- `ADMIN` can manage any Story.

### Story Ownership

Every Story belongs to an author.

```text
Story
└── author → User
```

The author is derived from the authenticated user rather than accepted from the request.

This prevents a client from creating a Story on behalf of another user.

### Story Visibility

Published Stories are public.

Draft Stories are protected based on role and ownership.

## Visibility Matrix

```text
                              Anonymous   USER   AUTHOR   ADMIN

Published Story                  Yes       Yes     Yes      Yes

Own Draft                        N/A       No      Yes      Yes

Another Author's Draft           No        No      No       Yes
```

## Write Authorization Matrix

```text
                              Anonymous   USER   AUTHOR   ADMIN

POST Story                       No        No      Yes     Yes

PUT own Story                    No        No      Yes     Yes

PUT another Author's Story       No        No      No      Yes

DELETE own Story                 No        No      Yes     Yes

DELETE another Author's Story    No        No      No      Yes
```

## JWT Configuration

JWT configuration is externalized:

```properties
security.jwt.secret=${JWT_SECRET}
security.jwt.expiration=3600000
```

The actual signing secret is provided through the runtime environment and is not committed to Git.