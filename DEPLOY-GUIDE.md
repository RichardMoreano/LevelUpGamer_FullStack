# 🚀 Deploy Level-Up Gamer a Producción Online

## � **OPCIÓN RECOMENDADA: Railway + GitHub Pages**

Esta es la configuración más fácil y económica para hospedar tu proyecto completo online:

### **🎯 Resultado Final:**
- **Frontend**: `https://RichardMoreano.github.io/LevelUpGamer_FullStack`
- **Backend API**: `https://levelup-gamer-backend.up.railway.app/api/v1`
- **Base de datos**: PostgreSQL automática en Railway
- **Costo**: ¡GRATIS! (Railway $5 gratis/mes, GitHub Pages gratis)

---

## 📋 **PASO A PASO COMPLETO**

### **1️⃣ PREPARAR EL BACKEND PARA RAILWAY**

#### Crear archivo de configuración para Railway:

```json
{
  "build": {
    "builder": "NIXPACKS"
  },
  "deploy": {
    "startCommand": "java -jar target/levelup-gamer-backend-1.0.0.jar",
    "restartPolicyType": "ON_FAILURE"
  }
}
```

#### Configurar application-prod.yml para producción:

```yaml
spring:
  profiles:
    active: prod
  datasource:
    url: ${DATABASE_URL:jdbc:postgresql://localhost:5432/levelup_gamer_db}
    username: ${DATABASE_USERNAME:levelup_user}
    password: ${DATABASE_PASSWORD:levelup_password}
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    show-sql: false
  
  security:
    jwt:
      secret-key: ${JWT_SECRET:myVerySecureJWTSecretKeyForProduction2024LevelUpGamer}
      expiration: 86400000
      refresh-expiration: 604800000

server:
  port: ${PORT:8080}

app:
  cors:
    allowed-origins: ${CORS_ORIGINS:https://v1ct0r-ops.github.io}
```

### **2️⃣ CONFIGURAR FRONTEND PARA GITHUB PAGES**

#### Modificar package.json del admin-react:

```json
{
  "name": "levelup-gamer-frontend",
  "homepage": "https://v1ct0r-ops.github.io/proyecto_Semestral_full_stack_2",
  "scripts": {
    "dev": "vite",
    "build": "vite build --base=/proyecto_Semestral_full_stack_2/",
    "preview": "vite preview",
    "predeploy": "npm run build",
    "deploy": "gh-pages -d dist"
  },
  "devDependencies": {
    "gh-pages": "^6.0.0"
  }
}
```

#### Configurar vite.config.js:

```javascript
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  base: process.env.NODE_ENV === 'production' ? '/proyecto_Semestral_full_stack_2/' : '/',
  build: {
    outDir: 'dist',
    assetsDir: 'assets'
  },
  server: {
    port: 5173,
    host: true
  }
})
```

#### Crear archivo de configuración API:

```javascript
// src/config/api.js
const isProd = import.meta.env.PROD;
const API_BASE_URL = isProd 
  ? 'https://tu-proyecto.up.railway.app/api/v1'
  : 'http://localhost:8080/api/v1';

export { API_BASE_URL };

// Ejemplo de uso en servicios:
export const authService = {
  login: async (credentials) => {
    const response = await fetch(`${API_BASE_URL}/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(credentials)
    });
    return response.json();
  }
};
```

### **3️⃣ DEPLOY PASO A PASO**

#### A. Subir código a GitHub:
```bash
# 1. Inicializar Git (si no está hecho)
git init
git add .
git commit -m "Initial commit - Level Up Gamer Full Stack"

# 2. Crear repositorio en GitHub y conectar
git remote add origin https://github.com/v1ct0r-ops/proyecto_Semestral_full_stack_2.git
git push -u origin main
```

#### B. Deploy Backend en Railway:
1. **Ir a Railway.app** → Crear cuenta gratuita
2. **Conectar GitHub** → Autorizar acceso
3. **New Project** → Deploy from GitHub repo
4. **Seleccionar** tu repositorio `proyecto_Semestral_full_stack_2`
5. **Configurar variables de entorno**:
   ```
   SPRING_PROFILES_ACTIVE=prod
   JWT_SECRET=tuSecretoJWTMuySeguro2024
   CORS_ORIGINS=https://v1ct0r-ops.github.io
   ```
6. **Railway detectará automáticamente** Spring Boot y PostgreSQL
7. **Deploy automático** - ¡Listo en 2-3 minutos!

#### C. Deploy Frontend en GitHub Pages:
```bash
# 1. Instalar gh-pages
cd admin-react
npm install --save-dev gh-pages

# 2. Build y deploy
npm run build
npm run deploy

# 3. Configurar GitHub Pages
# Ve a: GitHub repo → Settings → Pages
# Source: Deploy from a branch
# Branch: gh-pages / root
```

---

## 🌐 **ALTERNATIVAS DE HOSTING**

### **Opción 2: Vercel (Todo en uno)**
```bash
# 1. Instalar Vercel CLI
npm i -g vercel

# 2. Deploy desde la raíz del proyecto
vercel

# 3. Configurar en vercel.json (ya creado arriba)
```

### **Opción 3: Netlify + Render**
- **Frontend**: Drag & drop dist/ en Netlify
- **Backend**: Conectar repo en Render.com

### **Opción 4: Heroku (Requiere tarjeta)**
```bash
# Heroku CLI
heroku create levelup-gamer-backend
heroku addons:create heroku-postgresql:hobby-dev
git push heroku main
```

---

## 🔧 **CONFIGURACIONES ADICIONALES**

### **Variables de Entorno para Producción:**
```bash
# Railway Backend
DATABASE_URL=postgresql://...  (automático)
JWT_SECRET=tuSecretoMuySeguro
SPRING_PROFILES_ACTIVE=prod
CORS_ORIGINS=https://v1ct0r-ops.github.io

# Frontend (Vite)
VITE_API_URL=https://tu-backend.up.railway.app/api/v1
VITE_APP_NAME=Level Up Gamer
```

### **Configurar CORS en SecurityConfig:**
```java
@Configuration
public class SecurityConfig {
    
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList(allowedOrigins));
        // ... resto de configuración
    }
}
```

---

## � **TESTING DESPUÉS DEL DEPLOY**

### **1. Verificar Backend:**
```bash
# Health check
curl https://tu-proyecto.up.railway.app/actuator/health

# Test login
curl -X POST https://tu-proyecto.up.railway.app/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@levelup.cl","password":"admin123"}'
```

### **2. Verificar Frontend:**
- Abrir: `https://v1ct0r-ops.github.io/proyecto_Semestral_full_stack_2`
- Probar login con usuarios de prueba
- Verificar llamadas API en Developer Tools

### **3. Monitoreo:**
- **Railway Dashboard**: Logs en tiempo real
- **GitHub Actions**: Build automático
- **Swagger Docs**: `https://tu-backend.railway.app/swagger-ui.html`

---

## � **PRÓXIMOS PASOS DESPUÉS DEL DEPLOY**