
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .

RUN mvn clean package -DskipTests


FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/target/ms-aluguel-0.0.1-SNAPSHOT.jar app.jar


ENV SERVER_PORT=8080


ENTRYPOINT ["java", "-Xmx256m", "-Xss512k", "-jar", "app.jar"]