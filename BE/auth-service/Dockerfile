# --- STAGE 1: Build ---
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# 1. Copy all pom.xml files so Maven can find all modules
COPY BE/pom.xml ./BE/
COPY BE/common-lib/pom.xml ./BE/common-lib/
COPY BE/eureka-server/pom.xml ./BE/eureka-server/
COPY BE/config-server/pom.xml ./BE/config-server/
COPY BE/api-gateway/pom.xml ./BE/api-gateway/
COPY BE/auth-service/pom.xml ./BE/auth-service/
COPY BE/user-service/pom.xml ./BE/user-service/
COPY BE/notification-service/pom.xml ./BE/notification-service/

# 2. Copy the source code for modules we need to build
COPY BE/common-lib/src ./BE/common-lib/src
COPY BE/auth-service/src ./BE/auth-service/src

# 3. Install the Parent
RUN mvn install -N -f BE/pom.xml

# 4. Build common-lib
RUN mvn install -pl common-lib -am -DskipTests -f BE/pom.xml

# 5. Build auth-service
RUN mvn package -pl auth-service -am -DskipTests -f BE/pom.xml

# --- STAGE 2: Run ---
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/BE/auth-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]