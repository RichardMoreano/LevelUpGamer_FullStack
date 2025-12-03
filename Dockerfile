# Etapa 1: Build con Maven
FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar archivos de configuracion de Maven
COPY backend-spring/pom.xml ./
COPY backend-spring/src ./src

# Compilar el proyecto
RUN mvn clean package -DskipTests

# Etapa 2: Runtime con Java
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto
EXPOSE 8080

# Comando para iniciar la aplicacion
ENTRYPOINT ["java", "-jar", "app.jar"]
