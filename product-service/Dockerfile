# --- Stage 1: Build with Maven ---
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY product-service ./product-service
RUN mvn -f product-service/pom.xml clean package -DskipTests

# --- Stage 2: Run ---
FROM eclipse-temurin:17-jre-jammy
EXPOSE ${PORT:-8085}
WORKDIR /app
COPY --from=builder /app/product-service/target/product-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]