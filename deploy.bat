@echo off
REM 🚀 Script de Deploy Automático para Windows - Level-Up Gamer

echo 🚀 INICIANDO DEPLOY DE LEVEL-UP GAMER
echo ======================================

REM Verificar que estamos en la raíz del proyecto
if not exist "admin-react\package.json" (
    echo ❌ Error: Este script debe ejecutarse desde la raíz del proyecto
    pause
    exit /b 1
)

if not exist "backend-spring\pom.xml" (
    echo ❌ Error: No se encuentra el backend Spring Boot
    pause
    exit /b 1
)

echo 📂 Verificando repositorio Git...
if not exist ".git" (
    echo ⚠️  Inicializando repositorio Git...
    git init
    git add .
    git commit -m "Initial commit - Level Up Gamer Full Stack"
    echo ✅ Repositorio Git inicializado
) else (
    echo ✅ Repositorio Git encontrado
)

REM Verificar si hay cambios sin commitear
git status --porcelain > temp.txt
set /p changes=<temp.txt
del temp.txt

if not "%changes%"=="" (
    echo ⚠️  Hay cambios sin commitear. Haciendo commit automático...
    git add .
    git commit -m "Auto commit before deploy - %date% %time%"
    echo ✅ Cambios commiteados
)

REM Push a GitHub
echo 📤 Haciendo push a GitHub...
git push origin main 2>nul
if errorlevel 1 (
    echo ⚠️  No hay remote configurado o error en push. Configura tu repositorio:
    echo git remote add origin https://github.com/v1ct0r-ops/proyecto_Semestral_full_stack_2.git
    echo git push -u origin main
) else (
    echo ✅ Código subido a GitHub
)

REM Deploy del Frontend
echo 🎯 Preparando deploy del Frontend...
cd admin-react

REM Verificar Node.js
node --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Node.js no está instalado. Por favor instala Node.js 18+
    pause
    exit /b 1
)

echo 📦 Instalando dependencias del frontend...
call npm install

if errorlevel 1 (
    echo ❌ Error instalando dependencias
    pause
    exit /b 1
)

echo 🔨 Construyendo aplicación React...
call npm run build

if errorlevel 1 (
    echo ❌ Error en build del frontend
    pause
    exit /b 1
)

echo 🚀 Deploying a GitHub Pages...
call npm run deploy

if errorlevel 1 (
    echo ❌ Error en deploy a GitHub Pages
    echo 💡 Tip: Verifica que gh-pages esté instalado y configurado
    pause
    exit /b 1
)

cd ..

echo.
echo 🎉 DEPLOY COMPLETADO
echo ==================
echo.
echo 📋 INFORMACIÓN DEL DEPLOY:
echo 🌐 Frontend URL: https://v1ct0r-ops.github.io/proyecto_Semestral_full_stack_2
echo 📱 Repositorio: https://github.com/v1ct0r-ops/proyecto_Semestral_full_stack_2
echo.
echo ⚠️  PRÓXIMOS PASOS MANUALES:
echo 1. Ve a Railway.app y conecta tu repositorio GitHub
echo 2. Configura las variables de entorno en Railway
echo 3. Actualiza la URL del backend en src/config/api.js
echo.
echo 🔗 ENLACES ÚTILES:
echo • Railway: https://railway.app
echo • GitHub Pages: https://github.com/v1ct0r-ops/proyecto_Semestral_full_stack_2/settings/pages
echo.

echo ✅ Deploy script completado exitosamente!
pause