# Etapa 1: Compilación con Maven y OpenJDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
# Copiamos el archivo pom.xml y descargamos las dependencias (cacheo de capas)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiamos el resto del código y compilamos la aplicación
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: Creación de la imagen final
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copiamos el jar generado en la etapa de compilación
COPY --from=build /app/target/inditex-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
