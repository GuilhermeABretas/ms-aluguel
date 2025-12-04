# --- Estágio 1: Build (Construção) ---
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY . .
# Roda o clean install pulando os testes para agilizar o deploy (já testamos no GitLab)
RUN mvn clean package -DskipTests

# --- Estágio 2: Runtime (Execução) ---
FROM openjdk:17-jdk-slim
WORKDIR /app
# Copia o JAR gerado no estágio anterior para a imagem final
COPY --from=build /app/target/ms-aluguel-0.0.1-SNAPSHOT.jar app.jar

# Configura a porta (O Render injeta a variável PORT, mas deixamos 8080 como padrão)
ENV SERVER_PORT=8080

# Comando para iniciar a aplicação
# Aqui já aplicamos a limitação de memória para não estourar o plano grátis do Render
ENTRYPOINT ["java", "-Xmx256m", "-Xss512k", "-jar", "app.jar"]