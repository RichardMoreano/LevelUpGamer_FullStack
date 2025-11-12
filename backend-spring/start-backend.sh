#!/bin/bash

# Script para iniciar el backend Spring Boot de Level-Up Gamer
echo "🚀 Iniciando Level-Up Gamer Backend..."

# Verificar Java
if ! command -v java &> /dev/null; then
    echo "❌ Java no está instalado. Por favor instala Java 17 o superior."
    exit 1
fi

# Verificar Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven no está instalado. Por favor instala Maven."
    exit 1
fi

# Verificar MySQL
if ! command -v mysql &> /dev/null; then
    echo "⚠️  MySQL no detectado. Asegúrate de que MySQL esté ejecutándose en puerto 3306."
fi

echo "📦 Compilando aplicación..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "❌ Error en compilación. Revisa los logs anteriores."
    exit 1
fi

echo "🗄️  Verificando base de datos..."
echo "   - Host: localhost:3306"
echo "   - Database: levelup_gamer_db"
echo "   - Usuario: levelup_user"

echo "🌟 Iniciando aplicación Spring Boot..."
echo "   - Puerto: 8080"
echo "   - Perfil: dev"
echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"

mvn spring-boot:run