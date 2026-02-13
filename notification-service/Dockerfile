# --- Stage 1: Build with Maven ---
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app
COPY pom.xml .
COPY notification-service ./notification-service
RUN mvn -f notification-service/pom.xml clean package -DskipTests --no-transfer-progress

# --- Stage 2: Run ---
FROM eclipse-temurin:17-jre-jammy
EXPOSE ${PORT:-8083}
WORKDIR /app
COPY --from=builder /app/notification-service/target/notification-service.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]