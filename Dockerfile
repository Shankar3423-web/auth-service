# First stage: build the application
FROM maven:3.9.6-eclipse-temurin-21 AS build
COPY . /app
WORKDIR /app
RUN mvn clean package -DskipTests

# Second stage: create a slim image
FROM eclipse-temurin:21-jdk-alpine
COPY --from=build /app/target/auth-service.jar /app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]