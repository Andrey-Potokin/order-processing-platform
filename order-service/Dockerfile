# --- Stage 1: Build with Maven ---
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY order-service ./order-service
RUN mvn -f order-service/pom.xml clean package -DskipTests --no-transfer-progress

# --- Stage 2: Run ---
FROM eclipse-temurin:17-jre-jammy
EXPOSE ${PORT:-8084}
WORKDIR /app
COPY --from=builder /app/order-service/target/order-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]