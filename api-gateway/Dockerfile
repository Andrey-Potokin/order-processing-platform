# --- Stage 1: Build with Maven ---
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY api-gateway ./api-gateway
RUN mvn -f api-gateway/pom.xml clean package -DskipTests

# --- Stage 2: Run ---
FROM eclipse-temurin:17-jre-jammy
EXPOSE ${PORT:-8080}
WORKDIR /app
COPY --from=builder /app/api-gateway/target/api-gateway.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]