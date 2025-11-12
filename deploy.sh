#!/bin/bash

# 🚀 Script de Deploy Automático - Level-Up Gamer
# Este script hace el deploy completo del proyecto a producción

set -e  # Salir si cualquier comando falla

echo "🚀 INICIANDO DEPLOY DE LEVEL-UP GAMER"
echo "======================================"

# Colores para output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Función para imprimir con colores
print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Verificar que estamos en la raíz del proyecto
if [ ! -f "admin-react/package.json" ] || [ ! -f "backend-spring/pom.xml" ]; then
    print_error "Este script debe ejecutarse desde la raíz del proyecto"
    exit 1
fi

print_status "Verificando repositorio Git..."
if [ ! -d ".git" ]; then
    print_warning "Inicializando repositorio Git..."
    git init
    git add .
    git commit -m "Initial commit - Level Up Gamer Full Stack"
    print_success "Repositorio Git inicializado"
else
    print_success "Repositorio Git encontrado"
fi

# Verificar si hay cambios sin commitear
if [ -n "$(git status --porcelain)" ]; then
    print_warning "Hay cambios sin commitear. Haciendo commit automático..."
    git add .
    git commit -m "Auto commit before deploy - $(date)"
    print_success "Cambios commiteados"
fi

# Push a GitHub (si el remote existe)
if git remote get-url origin &> /dev/null; then
    print_status "Haciendo push a GitHub..."
    git push origin main
    print_success "Código subido a GitHub"
else
    print_warning "No hay remote configurado. Configura tu repositorio GitHub:"
    echo "git remote add origin https://github.com/v1ct0r-ops/proyecto_Semestral_full_stack_2.git"
    echo "git push -u origin main"
fi

# Deploy del Frontend
print_status "Preparando deploy del Frontend..."
cd admin-react

# Verificar si gh-pages está instalado
if ! npm list gh-pages &> /dev/null; then
    print_status "Instalando gh-pages..."
    npm install --save-dev gh-pages
    print_success "gh-pages instalado"
fi

print_status "Instalando dependencias del frontend..."
npm install

print_status "Construyendo aplicación React..."
npm run build

print_status "Deploying a GitHub Pages..."
npm run deploy

cd ..
print_success "Frontend deployado a GitHub Pages"

# Información del deploy
print_success "🎉 DEPLOY COMPLETADO"
echo ""
echo "📋 INFORMACIÓN DEL DEPLOY:"
echo "========================="
echo "🌐 Frontend URL: https://v1ct0r-ops.github.io/proyecto_Semestral_full_stack_2"
echo "📱 Repositorio: https://github.com/v1ct0r-ops/proyecto_Semestral_full_stack_2"
echo ""
echo "⚠️  PRÓXIMOS PASOS MANUALES:"
echo "1. Ve a Railway.app y conecta tu repositorio GitHub"
echo "2. Configura las variables de entorno en Railway"
echo "3. Actualiza la URL del backend en src/config/api.js"
echo ""
echo "🔗 ENLACES ÚTILES:"
echo "• Railway: https://railway.app"
echo "• GitHub Pages Settings: https://github.com/v1ct0r-ops/proyecto_Semestral_full_stack_2/settings/pages"
echo "• GitHub Repository: https://github.com/v1ct0r-ops/proyecto_Semestral_full_stack_2"

print_success "Deploy script completado exitosamente!"