# Build stage
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

COPY . .

RUN gradle build -x test

# Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 3092

ENTRYPOINT ["java", "-jar", "app.jar"]