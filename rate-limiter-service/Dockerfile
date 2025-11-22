# rate-limiter-service/Dockerfile

# STAGE 1: Build
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY . .
RUN ./gradlew clean build -x test

# STAGE 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy jar from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]