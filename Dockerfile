
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests


FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app


COPY --from=build /app/target/ms-aluguel-0.0.1-SNAPSHOT.jar app.jar


ENV SERVER_PORT=8080


ENTRYPOINT ["java", "-Xmx256m", "-Xss512k", "-jar", "app.jar"]