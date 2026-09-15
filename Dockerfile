# Dockerfile for Spring Boot (multi‑stage)
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app
COPY . .
# Install Maven (if not already present in base image)
RUN apk add --no-cache maven && \
    mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
