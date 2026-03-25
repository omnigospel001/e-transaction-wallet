## Overview

E-Transaction Wallet is a robust backend service designed to handle digital wallet operations with the following capabilities:

- **User Authentication & Authorization** - Secure JWT-based authentication
- **Wallet Management** - Create, update, and manage user wallets
- **Transaction Processing** - Handle financial transactions securely
- **Data Caching** - Redis integration for high performance
- **Input Validation** - Comprehensive data validation
- **Error Handling** - Centralized exception management
- **H2 Console** - In-memory database for development and testing

---

## 🛠 Technologies Used

### Core Framework
- **Java 25** - Latest Java LTS version
- **Spring Boot 4.0.4** - Enterprise application framework
- **Maven** - Build and dependency management

### Database & Storage
- **H2 Database** - In-memory relational database for development
- **Spring Data JPA** - Object-Relational Mapping (ORM)
- **Redis** - High-performance caching layer

### Security
- **Spring Security** - Authentication and authorization
- **JWT (JSON Web Tokens)** - Token-based authentication
- **JJWT 0.11.5** - JWT library for token creation and validation

### Web & API
- **Spring Web MVC** - RESTful API development
- **Validation** - Bean validation for input constraints

### Development Tools
- **Lombok** - Boilerplate code reduction
- **H2 Console** - Database inspection tool

### Testing
- **Spring Boot Test** - Integration testing
- **Spring Security Test** - Security testing utilities
- **Redis Test** - Redis integration testing
- **Validation Test** - Input validation testing
- **Web MVC Test** - API endpoint testing

---

## Architecture

The application follows a **layered architecture** pattern:
