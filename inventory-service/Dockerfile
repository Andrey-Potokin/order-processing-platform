# --- Stage 1: Build with Maven ---
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY inventory-service ./inventory-service
RUN mvn -f inventory-service/pom.xml clean package -DskipTests

# --- Stage 2: Run ---
FROM eclipse-temurin:17-jre-jammy
EXPOSE ${PORT:-8082}
WORKDIR /app
COPY --from=builder /app/inventory-service/target/inventory-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]