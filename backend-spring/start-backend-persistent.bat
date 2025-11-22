@echo off
echo Iniciando Level-Up Gamer Backend...
echo ====================================

:START
echo.
echo [%time%] Iniciando backend Spring Boot...
mvn spring-boot:run

echo.
echo [%time%] Backend se cerró. Reiniciando en 5 segundos...
timeout /t 5 /nobreak >nul
goto START