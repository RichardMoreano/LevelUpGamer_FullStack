#!/bin/bash

echo "🎯 BACKEND ULTRA-OPTIMIZADO - Última oportunidad..."

# Configuración de memoria máxima para Metaspace
export JAVA_TOOL_OPTIONS="-Xms96m -Xmx192m -XX:MaxMetaspaceSize=512m -XX:+UseSerialGC -XX:CompressedClassSpaceSize=64m"

echo "💾 Configuración EXTREMA para Spring Boot:"
echo "   - Heap: 96m - 192m"
echo "   - Metaspace: 512m (máximo para Spring/JPA/Hibernate)"
echo "   - CompressedClassSpace: 64m"
echo "   - GC: SerialGC (mínimo overhead)"

# Verificar memoria del sistema
echo "🔍 Verificando memoria del sistema..."
echo "Memoria libre aproximada:"
free -h 2>/dev/null || echo "Comando 'free' no disponible en Windows"

echo ""
echo "🚀 Ejecutando con configuración extrema..."

if [ -f "target/levelup-gamer-0.0.1-SNAPSHOT.jar" ]; then
    echo "📂 Ejecutando JAR existente..."
    java $JAVA_TOOL_OPTIONS -jar target/levelup-gamer-0.0.1-SNAPSHOT.jar
else
    echo "❌ JAR no encontrado"
    echo "🔧 Necesitas compilar primero con: ./ultra-light-start.sh"
    
    # Intentar mostrar estado de memoria
    echo ""
    echo "📊 DIAGNÓSTICO DEL SISTEMA:"
    echo "1. Cierra navegadores y aplicaciones pesadas"
    echo "2. Reinicia el sistema si es necesario"
    echo "3. El backend Spring Boot requiere ~600MB RAM mínimo"
    echo ""
    
    exit 1
fi