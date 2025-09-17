# Usar imagem do OpenJDK
FROM eclipse-temurin:17-jdk

# Criar diretório da app
WORKDIR /app

# Copiar o jar gerado pelo Maven
COPY target/springrestapi-0.0.1-SNAPSHOT.jar app.jar

# Rodar a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
