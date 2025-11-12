@echo off
REM Script para iniciar el backend Spring Boot de Level-Up Gamer en Windows

echo 🚀 Iniciando Level-Up Gamer Backend...

REM Verificar Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java no está instalado. Por favor instala Java 17 o superior.
    pause
    exit /b 1
)

REM Verificar Maven
mvn -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Maven no está instalado. Por favor instala Maven.
    pause
    exit /b 1
)

echo 📦 Compilando aplicación...
mvn clean compile

if errorlevel 1 (
    echo ❌ Error en compilación. Revisa los logs anteriores.
    pause
    exit /b 1
)

echo 🗄️  Verificando base de datos...
echo    - Host: localhost:3306
echo    - Database: levelup_gamer_db
echo    - Usuario: levelup_user

echo 🌟 Iniciando aplicación Spring Boot...
echo    - Puerto: 8080
echo    - Perfil: dev
echo    - Swagger UI: http://localhost:8080/swagger-ui.html

mvn spring-boot:run

pause