#!/bin/bash

echo "🚀 Iniciando Backend - Método Directo..."

# Compilar primero
echo "📦 Compilando..."
./mvnw clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "❌ Error al compilar"
    exit 1
fi

echo "🌟 Ejecutando JAR directamente con memoria limitada..."

# Buscar el JAR compilado
JAR_FILE=$(find target -name "*.jar" -not -name "*sources*" | head -1)

if [ -z "$JAR_FILE" ]; then
    echo "❌ No se encontró el archivo JAR"
    exit 1
fi

echo "📂 Ejecutando: $JAR_FILE"

# Ejecutar con memoria reducida
java -Xms128m -Xmx256m -XX:+UseSerialGC -jar "$JAR_FILE"