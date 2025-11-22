# Distributed Reactive Rate Limiter Service

A high-performance, fault-tolerant, and distributed rate-limiting microservice built with **Spring Boot 3 (WebFlux)**, **Redis (Lua Scripting)**, and **Resilience4j**. Designed to handle burst traffic with sub-millisecond latency while ensuring system availability during cascading failures.

---

## 🚀 Key Features

* **⚛️ Atomic Token Bucket Algorithm:** Implemented custom **Lua Scripting** to run purely inside Redis. This ensures atomicity and prevents race conditions in distributed environments, unlike standard "Read-Modify-Write" Java approaches.
* **🛡️ "Fail-Open" Circuit Breaker:** Integrated **Resilience4j** to monitor Redis availability. If the cache layer fails, the system automatically degrades gracefully (allows traffic) rather than crashing the entire API.
* **⚡ Fully Reactive & Non-Blocking:** Built on **Project Reactor (WebFlux)** to handle thousands of concurrent requests with minimal resource usage, avoiding the "Thread-per-Request" model.
* **🎯 Declarative AOP API:** Custom `@RateLimit` annotation using **Spring AOP** and **SpEL** (Spring Expression Language), allowing developers to rate-limit any method by simply adding an annotation.
* **🧪 Integration Testing:** Automated test suite using **TestContainers**, spinning up ephemeral Redis and Postgres instances to verify Lua logic in a real environment.
* **☁️ Cloud Native:** Fully containerized with **Docker** and orchestrated via **Kubernetes** using a custom **Helm Chart**.

---

## 🛠️ Tech Stack

* **Language:** Java 21
* **Framework:** Spring Boot 3.2 (WebFlux, AOP, Actuator)
* **Distributed Cache:** Redis (Standard + Custom Lua Scripts)
* **Database:** PostgreSQL (Configuration Storage)
* **Resilience:** Resilience4j (Circuit Breaker)
* **Testing:** JUnit 5, TestContainers, Mockito
* **Infrastructure:** Docker, Kubernetes (Minikube/Docker Desktop), Helm

---

## 🏗️ Architecture

The system follows a **Layered Architecture** with strict separation of concerns:

1. **Aspect Layer:** Intercepts requests using `@RateLimit`.
2. **Service Layer:** Orchestrates the flow between Config (Postgres) and State (Redis).
3. **Strategy Pattern:** Pluggable algorithms (Token Bucket, Fixed Window) implemented via interfaces.
4. **Infrastructure:**
   * **Redis:** Stores the token counters and executes atomic Lua scripts.
   * **PostgreSQL:** Stores persistent client configuration (limits/windows).

---

## 📂 Project Structure
```
├── rate-limiter-service/   # Core Microservice Logic
│   ├── src/main/java/
│   │   ├── aspect/         # AOP Logic (@RateLimit)
│   │   ├── strategy/       # Rate Limiting Algorithms (TokenBucket)
│   │   └── config/         # Redis & Resilience Config
│   └── src/main/resources/scripts/ # Lua Scripts
├── k8s-chart/              # Helm Charts & K8s Manifests
├── docker-compose.yml      # Local Dev Environment
└── README.md               # You are here
```

---

## 🚀 Getting Started

Follow these instructions to set up the project locally for development or deploy it to a Kubernetes cluster.

### Prerequisites

* Java 21 & Gradle
* Docker Desktop (or Minikube)
* Helm

---

### Option 1: Quick Start (Docker Compose)

Run the entire stack (App + Redis + Postgres) with one command.

#### Build & Start
```bash
docker-compose up --build
```

#### Access the Application

* **API:** http://localhost:8080/test-annotation?user=test_user
* **Prometheus:** http://localhost:9090
* **Grafana:** http://localhost:3000

#### Stop the Stack
```bash
docker-compose down
```

---

### Option 2: Cloud Deployment (Kubernetes + Helm)

Deploy the microservice to a Kubernetes cluster.

#### Build Docker Image
```bash
docker build -t rate-limiter-app:latest ./rate-limiter-service
```

#### Install with Helm
```bash
helm install my-limiter ./k8s-chart
```

#### Verify Deployment
```bash
kubectl get pods
# Expected: 3 Running Pods (App, Redis, Postgres)
```

#### Access the Service (Port Forward)
```bash
kubectl port-forward svc/my-limiter-k8s-chart 8080:8080
```

Now open: http://localhost:8080/test-annotation?user=k8s_user

#### Uninstall
```bash
helm uninstall my-limiter
```

---
