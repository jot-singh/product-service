# Multi-stage Dockerfile for productservice
FROM maven:3.9.11-amazoncorretto-21 AS builder
WORKDIR /build
COPY pom.xml mvnw .mvn/ ./
COPY .mvn .mvn
COPY src ./src
# Download dependencies and build
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /build/target/productservice-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
