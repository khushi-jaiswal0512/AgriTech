# AgriTech Marketplace Platform 🌾

A comprehensive, scalable microservices-based marketplace platform designed to connect farmers directly with consumers and retailers. The ecosystem consists of a powerful Java Spring Boot backend and a cross-platform mobile application built in Flutter.

## 🏗 Architecture Overview

The system is built on a modern **Microservices Architecture**:
- **Backend Framework**: Java 21 & Spring Boot 3.4.x
- **Frontend App**: Flutter & Dart (Clean Architecture, BLoC)
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Configuration Management**: Spring Cloud Config
- **Databases**: MySQL 8.0 (Persistent) & Redis 7.0 (Caching / Rate Limiting)
- **Event Streaming**: Apache Kafka (KRaft mode) for asynchronous inter-service communication
- **Resilience**: Resilience4j (Circuit Breakers & Retries)
- **Authentication**: JWT-based Role Access Control (RBAC)

## 📂 Project Structure

- **`agritech-platform/`** - Contains the 15 Spring Boot microservices, `docker-compose.yml`, and `Dockerfile` for backend deployment.
  - `admin-service`, `auth-service`, `cart-service`, `chat-service`, `notification-service`, `order-service`, `product-service`, `rating-service`, `user-service`, `wishlist-service` (Domain Services).
  - `api-gateway`, `config-server`, `eureka-server` (Platform Services).
  - `config-repo` (Centralized configurations).
- **`agritech_app/`** - The cross-platform Flutter application built with Clean Architecture.
- **`.github/workflows/`** - CI/CD pipelines (GitHub Actions).

## 🚀 Getting Started

### 1. Run the Backend (Docker)
The entire backend ecosystem (including MySQL, Redis, Kafka, and all 13 Spring Boot services) is containerized and orchestrated via Docker Compose.

```bash
cd agritech-platform
docker-compose up -d --build
```

**Services Exposed Locally:**
- **API Gateway**: `http://localhost:8080` (Route all API requests here)
- **Eureka Dashboard**: `http://localhost:8761`
- **Config Server**: `http://localhost:8888`

### 2. Run the Mobile App (Flutter)
Ensure you have the Flutter SDK installed.

```bash
cd agritech_app
flutter pub get
flutter run
```

## 🛡️ CI/CD & Deployment
The repository includes a GitHub Action (`ci.yml`) that automatically builds Maven projects and Docker images for all backend modules upon pushing to the `main` branch. 

## 📝 License
This project is licensed under the MIT License.
