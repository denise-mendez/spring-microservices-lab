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
├── 05-microservices-split/
├── 06-docker-compose/
├── 07-s3-file-storage/
├── 08-config-server/
├── 09-eureka-discovery/
├── 10-api-gateway/
├── 11-correlation-id/
├── 12-kubernetes-deployment/
├── 13-kubernetes-scaling/
└── 14-rolling-update/
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

### 03 - Oauth2 Resource Server

**Status:** Backlog / Optional

Explore externally issued JWT validation using Spring Security OAuth2 Resource Server and an external identity provider.


## 04 - Open API

**Status:** Implemented

This lab extends the secured Story API by adding an explicit OpenAPI contract and interactive Swagger documentation.

The generated contract describes:

```text
Endpoints
+
Request Schemas
+
Response Schemas
+
HTTP Status Codes
+
Error Responses
+
JWT Bearer Authentication
```

#### Focus

- Springdoc OpenAPI integration
- Swagger UI
- API metadata
- Authentication endpoint documentation
- Story endpoint documentation
- Request/response schemas
- Error response schemas
- HTTP response codes
- JWT Bearer security scheme
- Protected endpoint security requirements

_**Documentation Endpoints**_
```text
GET /v3/api-docs
GET /v3/api-docs.yaml
GET /swagger-ui.html
```

#### Documented API

_**Authentication**_

```text
POST /api/auth/register
POST /api/auth/login
```

_**Stories**_

```text
POST   /api/stories
GET    /api/stories
GET    /api/stories/{id}
GET    /api/stories?status=PUBLISHED
GET    /api/stories?status=DRAFT
PUT    /api/stories/{id}
DELETE /api/stories/{id}
```

_**Security**_

The OpenAPI specification defines JWT Bearer authentication:

```text
type   = http
scheme = bearer
format = JWT
```

Public operations:

```text
POST /api/auth/register
POST /api/auth/login
GET  /api/stories/**
```

Protected operations:

```text
POST   /api/stories
PUT    /api/stories/{id}
DELETE /api/stories/{id}
```

Protected operations reference:

```text
bearerAuth
```

#### Response Documentation

The API documents relevant responses including:

```text
200 OK
201 Created
204 No Content
400 Bad Request
401 Unauthorized
403 Forbidden
404 Not Found
409 Conflict
```

Application errors use the existing `ErrorResponse` model:

```json
{
  "status": 404,
  "error": "STORY_NOT_FOUND",
  "message": "Story with id 999 was not found"
}
```



#### Main Learning Outcome

This lab evolves the Story API from:

```text
Working REST API
```

into:

```text
Working REST API
       +
Machine-readable OpenAPI contract
       +
Interactive Swagger documentation
```


# 05 - Microservices Split

This lab decomposes the Story application into independently deployable Spring Boot services while preserving the authentication, authorization, ownership, and API behavior implemented in previous labs.

## Key Takeaway
Each service is now an independent Spring Boot application with its own:

- Maven build
- Application configuration
- Persistence boundary
- API contract
- OpenAPI/Swagger documentation

## Services

### Auth Service

```text
Port: 8081
```

Responsibilities:

- Account registration
- Password encoding with BCrypt
- Credential authentication
- Role management
- JWT generation
- Identity propagation

Additional endpoints: (To augment functionality)

```text
PATCH /api/auth/accounts/{id}/role
```

### User Service

```text
Port: 8082
```

Responsibilities:

- User domain ownership
- User persistence (To be implemented)
- User retrieval (To be implemented)

### Story Service

```text
Port: 8083
```

Responsibilities:

- Story creation
- Story retrieval
- Story updates and deletion
- Published/Draft visibility
- Story ownership
- Role-based authorization


## Main Learning Outcome

Cross-service relationships are represented through identifiers and authenticated claims rather than shared JPA entities.

### 06 - Docker Composer

**Status:** Backlog

Reason: local resource constrains.

### 07 - S3 File Storage

**Status:** Backlog / Optional

AWS integration for external file storage


### 08 Config Server

This lab introduces centralized configuration management for the Story microservices using Spring Cloud Config.

The Auth, User, and Story services retrieve their environment-specific configuration from a Config Server instead of keeping all configuration locally.

## Architecture

```text
story-lab-config-repo
        │
        │ Git
        ▼
config-server
    :8888
        │
        ├── auth-service  :8081
        ├── user-service  :8082
        └── story-service :8083
```

## External Configuration Repository

Configuration is stored in a separate Git repository:

```text
story-lab-config-repo/
├── application.properties
├── auth-service.properties
├── user-service.properties
└── story-service.properties
```

`application.properties` contains configuration shared across services.
`auth-service.properties`, `user-service.properties` and `story-service.properties` contain service-specific configuration such as:

```text
server port
datasource configuration
Hibernate configuration
JWT
```

## Config Client

Each microservice keeps only the configuration required to identify itself and locate Config Server.

## Configuration Loading Flow

```text
Microservice starts
       ↓
spring.application.name
       ↓
Config Server :8888
       ↓
Git configuration repository
       ↓
application.properties
       +
<service-name>.properties
       ↓
Spring Environment
       ↓
Application starts
```

## Config Server Endpoints

Configuration can be verified directly through:

```text
GET http://localhost:8888/auth-service/default

GET http://localhost:8888/user-service/default

GET http://localhost:8888/story-service/default
```

Each response exposes the property sources resolved for that application and profile.

## Startup Order

Config Server must be available before starting clients because the services use:

```properties
spring.config.import=configserver:[GitHub_Repository]
```

Recommended startup order:

```text
1. config-server

2. auth-service
3. user-service
4. story-service
```

The existing API behavior remains unchanged.
## Key Learning Outcome

Before this lab:

```text
auth-service
└── application.properties

user-service
└── application.properties

story-service
└── application.properties
```

After this lab:

```text
                    story-lab-config-repo
                          │
                          ▼
                    config-server
                          │
              ┌───────────┼───────────┐
              ▼           ▼           ▼
            Auth         User        Story
```