#!/bin/bash

echo "🚀 Iniciando Level-Up Gamer Backend (Modo Light)..."
echo "📦 Compilando aplicación..."

# Compilar la aplicación
./mvnw clean compile

if [ $? -ne 0 ]; then
    echo "❌ Error al compilar la aplicación"
    exit 1
fi

echo "🗄️  Verificando base de datos..."
echo "   - Host: localhost (H2 en memoria)"
echo "   - Database: levelup_gamer_db"

echo "🌟 Iniciando aplicación Spring Boot (Memoria reducida)..."
echo "   - Puerto: 8080"
echo "   - Perfil: dev"
echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"

# Iniciar con configuración de memoria reducida
export MAVEN_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xms256m -Xmx512m -XX:+UseG1GC"