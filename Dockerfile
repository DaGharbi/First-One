# --- STAGE 1: Build everything ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Copy the Parent POM
COPY pom.xml .

# 2. Copy all service folders
COPY api-gateway ./api-gateway
COPY auth-service ./auth-service
COPY config-server ./config-server
COPY eureka-server ./eureka-server
COPY notification-service ./notification-service
COPY user-service ./user-service

# 3. Build the entire project
RUN mvn clean package -DskipTests