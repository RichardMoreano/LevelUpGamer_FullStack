#!/bin/bash

echo "🚀 Iniciando Backend - Ultra Light Mode..."

# Configurar variables de memoria muy bajas
export JAVA_TOOL_OPTIONS="-Xms64m -Xmx128m -XX:+UseSerialGC -XX:MaxMetaspaceSize=64m"
export MAVEN_OPTS="-Xms32m -Xmx64m"

echo "💾 Configuración de memoria:"
echo "   - Java Heap: 64m - 128m"
echo "   - Maven: 32m - 64m"
echo "   - GC: SerialGC (mínimo)"

# Verificar si existe un JAR ya compilado
if [ -f "target/levelup-gamer-0.0.1-SNAPSHOT.jar" ]; then
    echo "📂 JAR encontrado, ejecutando directamente..."
    java $JAVA_TOOL_OPTIONS -jar target/levelup-gamer-0.0.1-SNAPSHOT.jar
else
    echo "📦 Compilando con Maven..."
    
    # Intentar compilar con memoria mínima
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        echo "✅ Compilación exitosa, iniciando..."
        java $JAVA_TOOL_OPTIONS -jar target/levelup-gamer-0.0.1-SNAPSHOT.jar
    else
        echo "❌ Error en compilación. Intentando solo ejecutar si existe JAR..."
        # Buscar cualquier JAR que pueda existir
        JAR_FILE=$(find target -name "*.jar" 2>/dev/null | head -1)
        if [ -n "$JAR_FILE" ]; then
            echo "📂 Ejecutando JAR encontrado: $JAR_FILE"
            java $JAVA_TOOL_OPTIONS -jar "$JAR_FILE"
        else
            echo "❌ No se puede iniciar el backend - falta de memoria del sistema"
            echo ""
            echo "🔧 SOLUCIONES POSIBLES:"
            echo "1. Cerrar aplicaciones para liberar RAM"
            echo "2. Reiniciar el sistema"
            echo "3. Aumentar archivo de paginación de Windows"
            echo ""
            exit 1
        fi
    fi
fi