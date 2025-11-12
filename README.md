# 🎮 Level-Up Gamer - E-commerce Full Stack

Plataforma completa de e-commerce para productos gaming desarrollada con **React + Vite** en el frontend y **Spring Boot 3** en el backend, migrada desde LocalStorage a arquitectura full-stack con autenticación JWT y MySQL/PostgreSQL.

## 🌐 **PROYECTO EN LÍNEA**

### 🚀 **URLs de Producción**
- **Frontend (GitHub Pages)**: https://RichardMoreano.github.io/LevelUpGamer_FullStack
- **Backend API (Railway)**: https://levelup-gamer-backend.up.railway.app/api/v1
- **Swagger Docs**: https://levelup-gamer-backend.up.railway.app/swagger-ui.html

### 👥 **Usuarios de Prueba**
| Tipo | Correo | Password | Características |
|------|--------|----------|-----------------|
| **Admin** | admin@levelup.cl | admin123 | Gestión completa |
| **Vendedor** | vendedor@levelup.cl | vendedor123 | Gestión productos/stock |
| **Cliente DUOC** | maria.estudiante@duocuc.cl | duoc123 | 20% descuento automático |
| **Cliente Normal** | pedro.cliente@gmail.com | cliente123 | Sistema puntos LevelUp |

---

## 🏗️ **Arquitectura del Sistema**

```
Level-Up Gamer Full Stack
├── Frontend (React + Vite)          → GitHub Pages
├── Backend (Spring Boot 3)          → Railway.app  
├── Base de Datos (PostgreSQL)       → Railway DB
└── CI/CD (GitHub Actions)           → Deploy automático
```

### **Stack Tecnológico**
- **Frontend**: React 19, Vite 7, React Router, Axios
- **Backend**: Spring Boot 3.2, Spring Security, JWT, JPA/Hibernate
- **Base de Datos**: MySQL (dev) / PostgreSQL (prod)
- **Deploy**: GitHub Pages + Railway
- **CI/CD**: GitHub Actions

---

## 🚀 **Deploy Automático a Producción**

### **Opción 1: Script Automático (Recomendado)**

**Windows:**
```bash
./deploy.bat
```

**Linux/Mac:**
```bash
chmod +x deploy.sh
./deploy.sh
```

### **Opción 2: Deploy Manual**

#### **1. Frontend a GitHub Pages**
```bash
cd admin-react
npm install
npm run build
npm run deploy
```

#### **2. Backend a Railway**
1. Ve a **Railway.app** → Conecta GitHub
2. Importa repositorio `proyecto_Semestral_full_stack_2`
3. Configura variables de entorno:
   ```
   JWT_SECRET=tuSecretoJWTMuySeguro2024
   SPRING_PROFILES_ACTIVE=prod
   CORS_ORIGINS=https://v1ct0r-ops.github.io
   ```
4. Deploy automático activado ✅

---

## 💻 **Desarrollo Local**

### **Requisitos**
- Java 17+
- Maven 3.6+
- Node.js 18+
- MySQL 8.0+ (local)

### **Backend (Puerto 8080)**
```bash
cd backend-spring
./start-backend.bat    # Windows
./start-backend.sh     # Linux/Mac
```

### **Frontend (Puerto 5173)**
```bash
cd admin-react
npm install
npm run dev
```

### **Acceso Local**
- **Frontend**: http://localhost:5173
- **Backend API**: http://localhost:8080/api/v1
- **Swagger**: http://localhost:8080/swagger-ui.html

---

## 🛍️ **Características Implementadas**

### **🔐 Sistema de Autenticación**
- ✅ Login/Register con JWT
- ✅ Roles: Admin, Vendedor, Cliente
- ✅ Protección de rutas por roles
- ✅ Sesión persistente

### **🎯 Lógica de Negocio**
- ✅ **Descuento DUOC**: 20% automático para @duocuc.cl
- ✅ **Puntos LevelUp**: 1 punto = $10 CLP
- ✅ **Control Stock**: Automático con niveles críticos
- ✅ **Cálculos**: Subtotales, descuentos, totales

### **🎮 Catálogo de Productos**
- ✅ **Categorías**: Consolas, Juegos PS5/Xbox/Nintendo, Accesorios
- ✅ **Búsqueda**: Por nombre y filtros
- ✅ **Stock en tiempo real**
- ✅ **Gestión admin**: CRUD completo

### **👤 Gestión de Usuarios**
- ✅ **Perfil personal**: Edición de datos
- ✅ **Puntos LevelUp**: Consulta y uso
- ✅ **Admin panel**: Gestión usuarios
- ✅ **Roles y permisos**

---

## 📱 **API REST Endpoints**

### **Autenticación**
```
POST /api/v1/auth/login          # Login con JWT
POST /api/v1/auth/register       # Registro de usuario
POST /api/v1/auth/logout         # Logout
```

### **Productos**
```
GET  /api/v1/productos           # Listar todos
GET  /api/v1/productos/activos   # Solo activos
GET  /api/v1/productos/{codigo}  # Por código
GET  /api/v1/productos/categoria/{cat}  # Por categoría
POST /api/v1/productos           # Crear (Admin)
PUT  /api/v1/productos/{codigo}  # Actualizar (Admin)
```

### **Usuarios**
```
GET  /api/v1/usuarios/me         # Perfil actual
PUT  /api/v1/usuarios/me         # Actualizar perfil
POST /api/v1/usuarios/me/cambiar-password  # Cambiar password
GET  /api/v1/usuarios/puntos     # Consultar puntos LevelUp
```

**📋 Documentación completa**: `/swagger-ui.html`

---

## 🗄️ **Base de Datos**

### **Entidades Principales**
```sql
usuarios (run, nombres, apellidos, correo, tipo_usuario, puntos_levelup...)
productos (codigo, nombre, categoria, precio, stock, stock_critico...)
pedidos (id, usuario_run, fecha, total, estado...)
pedido_items (id, pedido_id, producto_codigo, cantidad, precio...)
boletas (numero, pedido_id, fecha_emision, total...)
```

### **Configuración Local MySQL**
```sql
CREATE DATABASE levelup_gamer_db;
CREATE USER 'levelup_user'@'localhost' IDENTIFIED BY 'levelup_password';
GRANT ALL PRIVILEGES ON levelup_gamer_db.* TO 'levelup_user'@'localhost';
```

---

## 🔧 **Configuración de Entornos**

### **Variables de Entorno - Backend**
```yaml
# Desarrollo (application.yml)
spring.datasource.url: jdbc:mysql://localhost:3306/levelup_gamer_db
spring.security.jwt.secret-key: mySecretKeyForDev

# Producción (Railway)
DATABASE_URL: postgresql://...  # Automático
JWT_SECRET: tuSecretoSeguroProduccion
SPRING_PROFILES_ACTIVE: prod
CORS_ORIGINS: https://v1ct0r-ops.github.io
```

### **Variables de Entorno - Frontend**
```javascript
// src/config/api.js
const API_BASE_URL = import.meta.env.PROD 
  ? 'https://levelup-gamer-backend-production.up.railway.app/api/v1'
  : 'http://localhost:8080/api/v1';
```

---

## 📊 **Datos de Prueba Incluidos**

### **Productos Inicializados**
- **Consolas**: PlayStation 5, Xbox Series X, Nintendo Switch
- **Juegos**: Títulos por plataforma (Spider-Man 2, God of War, Halo...)
- **Accesorios**: Controllers, headsets, memorias USB
- **Stock variado**: Algunos productos en nivel crítico para testing

### **Usuarios de Demostración**
- Administrador completo
- Vendedor con permisos limitados  
- Cliente DUOC con descuento automático
- Cliente normal con puntos LevelUp

---

## 🧪 **Testing y Monitoreo**

### **Health Checks**
```bash
# Backend health
curl https://levelup-gamer-backend-production.up.railway.app/actuator/health

# Test login
curl -X POST https://levelup-gamer-backend-production.up.railway.app/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@levelup.cl","password":"admin123"}'
```

### **Monitoring**
- **Railway Dashboard**: Logs en tiempo real
- **GitHub Actions**: CI/CD automático
- **Swagger UI**: Testing interactivo de API

---

## 📝 **Próximas Funcionalidades**

- [ ] **Carrito de Compras**: Persistente con backend
- [ ] **Proceso de Checkout**: Completo con descuentos y puntos
- [ ] **Historial de Pedidos**: Para usuarios
- [ ] **Panel de Reportes**: Para administradores
- [ ] **Notificaciones**: En tiempo real con WebSockets
- [ ] **Pagos**: Integración con Transbank/PayPal

---

## 🤝 **Contribución**

### **Estructura de Commits**
```
feat: nueva funcionalidad
fix: corrección de bug  
docs: documentación
style: formato de código
refactor: refactoring
test: pruebas
```

### **Desarrollo Local**
1. Fork del repositorio
2. Crear branch: `git checkout -b feature/nueva-funcionalidad`
3. Commit cambios: `git commit -m 'feat: agregar nueva funcionalidad'`
4. Push: `git push origin feature/nueva-funcionalidad`
5. Crear Pull Request

---

## 📞 **Soporte**

- **Repositorio**: https://github.com/v1ct0r-ops/proyecto_Semestral_full_stack_2
- **Issues**: https://github.com/v1ct0r-ops/proyecto_Semestral_full_stack_2/issues
- **Wiki**: https://github.com/v1ct0r-ops/proyecto_Semestral_full_stack_2/wiki

---

**Desarrollado como Proyecto Semestral Full-Stack**  
*React + Vite + Spring Boot 3 + JWT + MySQL/PostgreSQL*

🎮 **Level-Up Gamer** - ¡La mejor experiencia gaming online!