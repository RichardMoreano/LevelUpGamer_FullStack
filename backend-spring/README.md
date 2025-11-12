# Level-Up Gamer Backend - Spring Boot API

Backend completo desarrollado con **Spring Boot 3.2.0** para el sistema de e-commerce Level-Up Gamer, migrado desde LocalStorage a arquitectura full-stack con autenticación JWT y MySQL.

## 🏗️ Arquitectura Técnica

### Stack Tecnológico
- **Framework**: Spring Boot 3.2.0
- **Seguridad**: Spring Security + JWT
- **Base de Datos**: MySQL 8.0 + JPA/Hibernate
- **Documentación**: Swagger/OpenAPI 3
- **Build**: Maven
- **Java**: 17+

### Estructura del Proyecto
```
backend-spring/
├── src/main/java/cl/duoc/levelup/
│   ├── LevelUpGamerApplication.java          # Aplicación principal
│   ├── config/
│   │   ├── DataInitializer.java              # Datos iniciales
│   │   └── SecurityConfig.java               # Configuración seguridad
│   ├── controller/
│   │   ├── AuthController.java               # Autenticación
│   │   ├── ProductoController.java           # Productos
│   │   └── UsuarioController.java            # Usuarios
│   ├── entity/
│   │   ├── Usuario.java                      # Entidad Usuario
│   │   ├── Producto.java                     # Entidad Producto
│   │   ├── Pedido.java                       # Entidad Pedido
│   │   ├── PedidoItem.java                   # Items del pedido
│   │   └── Boleta.java                       # Boletas
│   ├── repository/
│   │   ├── UsuarioRepository.java            # Repositorio usuarios
│   │   ├── ProductoRepository.java           # Repositorio productos
│   │   ├── PedidoRepository.java             # Repositorio pedidos
│   │   └── BoletaRepository.java             # Repositorio boletas
│   ├── security/
│   │   ├── JwtTokenProvider.java             # Proveedor JWT
│   │   ├── UserPrincipal.java                # Principal usuario
│   │   ├── CustomUserDetailsService.java     # Servicio usuarios
│   │   ├── JwtAuthenticationFilter.java      # Filtro JWT
│   │   └── JwtAuthenticationEntryPoint.java  # Entry point
│   └── service/
│       ├── AuthService.java                  # Servicio autenticación
│       ├── UsuarioService.java               # Servicio usuarios
│       └── ProductoService.java              # Servicio productos
├── src/main/resources/
│   └── application.yml                       # Configuración aplicación
├── pom.xml                                   # Dependencias Maven
├── database-setup.sql                       # Script base de datos
├── start-backend.bat                        # Iniciador Windows
└── start-backend.sh                         # Iniciador Linux/Mac
```

## 🗄️ Configuración Base de Datos

### 1. Crear Base de Datos MySQL

```sql
-- Conectarse a MySQL como root
mysql -u root -p

-- Crear base de datos y usuario
CREATE DATABASE levelup_gamer_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'levelup_user'@'localhost' IDENTIFIED BY 'levelup_password';
GRANT ALL PRIVILEGES ON levelup_gamer_db.* TO 'levelup_user'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configuración en application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/levelup_gamer_db
    username: levelup_user
    password: levelup_password
  jpa:
    hibernate:
      ddl-auto: create-drop  # Cambiar a 'update' en producción
```

## 🚀 Ejecución del Backend

### Requisitos Previos
- Java 17 o superior
- Maven 3.6+
- MySQL 8.0+ ejecutándose en puerto 3306

### Inicio Rápido

**Windows:**
```bash
./start-backend.bat
```

**Linux/Mac:**
```bash
chmod +x start-backend.sh
./start-backend.sh
```

**Manual:**
```bash
# Compilar
mvn clean compile

# Ejecutar
mvn spring-boot:run

# O con jar
mvn package
java -jar target/levelup-gamer-backend-1.0.0.jar
```

### Verificación del Sistema
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

## 🔐 Sistema de Autenticación JWT

### Roles de Usuario
- **ADMIN**: Acceso completo al sistema
- **VENDEDOR**: Gestión de productos y stock
- **CLIENTE**: Compras y perfil personal

### Usuarios Por Defecto
| RUN | Correo | Password | Rol | Puntos LevelUp |
|-----|--------|----------|-----|----------------|
| 12345678-9 | admin@levelup.cl | admin123 | ADMIN | 0 |
| 98765432-1 | vendedor@levelup.cl | vendedor123 | VENDEDOR | 0 |
| 11111111-1 | maria.estudiante@duocuc.cl | duoc123 | CLIENTE | 150 |
| 22222222-2 | pedro.cliente@gmail.com | cliente123 | CLIENTE | 50 |

### Flujo de Autenticación
1. **Login**: `POST /api/v1/auth/login`
2. **Registro**: `POST /api/v1/auth/register`
3. **Token JWT** válido por 24 horas
4. **Header**: `Authorization: Bearer <token>`

## 📋 API Endpoints

### Autenticación
```
POST   /api/v1/auth/login          # Iniciar sesión
POST   /api/v1/auth/register       # Registrar usuario
POST   /api/v1/auth/logout         # Cerrar sesión
```

### Usuarios
```
GET    /api/v1/usuarios/me                     # Perfil actual
PUT    /api/v1/usuarios/me                     # Actualizar perfil
POST   /api/v1/usuarios/me/cambiar-password    # Cambiar contraseña
GET    /api/v1/usuarios/puntos                 # Consultar puntos LevelUp

# Endpoints Admin
GET    /api/v1/usuarios                        # Listar usuarios
GET    /api/v1/usuarios/activos                # Usuarios activos
GET    /api/v1/usuarios/{run}                  # Usuario por RUN
PUT    /api/v1/usuarios/{run}                  # Actualizar usuario
PUT    /api/v1/usuarios/{run}/activar          # Activar usuario
PUT    /api/v1/usuarios/{run}/desactivar       # Desactivar usuario
POST   /api/v1/usuarios/{run}/puntos           # Agregar puntos
```

### Productos
```
GET    /api/v1/productos                       # Listar productos
GET    /api/v1/productos/activos               # Productos activos
GET    /api/v1/productos/{codigo}              # Producto por código
GET    /api/v1/productos/categoria/{categoria} # Por categoría
GET    /api/v1/productos/buscar?nombre=X       # Buscar por nombre
GET    /api/v1/productos/categorias            # Listar categorías
GET    /api/v1/productos/stock-critico         # Stock crítico

# Endpoints Admin/Vendedor
POST   /api/v1/productos                       # Crear producto
PUT    /api/v1/productos/{codigo}              # Actualizar producto
DELETE /api/v1/productos/{codigo}              # Eliminar producto
PUT    /api/v1/productos/{codigo}/stock        # Actualizar stock
```

## 🎯 Lógica de Negocio Implementada

### Sistema de Puntos LevelUp
- **1 punto = $10 CLP** de descuento
- Los puntos se acumulan con cada compra
- Uso de puntos en el checkout

### Descuento DUOC
- **20% de descuento** automático para correos `@duocuc.cl`
- Validación por dominio de correo
- Aplicación automática en el cálculo de totales

### Control de Stock
- **Verificación automática** de disponibilidad
- **Stock crítico** configurable por producto
- **Reducción automática** tras confirmación de pedido
- **Restauración** en caso de cancelación

### Seguridad
- **Autenticación JWT** con roles
- **CORS configurado** para React (localhost:5173)
- **Validación** de entrada en todos los endpoints
- **Encriptación** de contraseñas con BCrypt

## 🔧 Configuración para Desarrollo

### Variables de Entorno
```yaml
# JWT Configuration
spring.security.jwt.secret-key: mySecretKeyForJWTTokensLevelUpGamer2024
spring.security.jwt.expiration: 86400000  # 24 horas
spring.security.jwt.refresh-expiration: 604800000  # 7 días

# CORS Origins
app.cors.allowed-origins: http://localhost:5173,http://localhost:3000
```

### Profiles
- **dev**: Desarrollo local (por defecto)
- **prod**: Producción (configurar variables de entorno)

## 📊 Datos de Prueba

### Productos Inicializados
- **10 productos** en diferentes categorías
- **Consolas**: PS5, Xbox Series X, Nintendo Switch
- **Juegos**: Por plataforma (PS5, Xbox, Nintendo)
- **Accesorios**: Controls, headsets, memoria USB
- **Stock variado** con algunos productos en nivel crítico

### Categorías Disponibles
- Consolas
- Juegos PS5
- Juegos Xbox
- Juegos Nintendo
- Accesorios

## 🚦 Testing

### Endpoints de Prueba
```bash
# Health check
curl http://localhost:8080/actuator/health

# Login admin
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@levelup.cl","password":"admin123"}'

# Listar productos
curl http://localhost:8080/api/v1/productos/activos
```

### Swagger UI
Accede a http://localhost:8080/swagger-ui.html para probar todos los endpoints interactivamente.

## 🔄 Integración con Frontend React

### Configuración CORS
El backend está configurado para aceptar requests desde:
- `http://localhost:5173` (Vite dev server)
- `http://localhost:3000` (Create React App)

### Servicios API Recomendados
```typescript
// Ejemplo de servicio para el frontend React
const API_BASE = 'http://localhost:8080/api/v1';

export const authService = {
  login: (credentials) => fetch(`${API_BASE}/auth/login`, {...}),
  register: (userData) => fetch(`${API_BASE}/auth/register`, {...})
};

export const productService = {
  getAll: () => fetch(`${API_BASE}/productos/activos`),
  getById: (codigo) => fetch(`${API_BASE}/productos/${codigo}`)
};
```

## 📝 Próximos Pasos

1. **Completar Controllers**: Pedidos, Boletas, Reportes
2. **Implementar WebSockets**: Notificaciones en tiempo real
3. **Añadir Tests**: Unitarios e integración
4. **Docker Setup**: Containerización completa
5. **CI/CD Pipeline**: Deploy automatizado

## ⚠️ Notas Importantes

- **Cambiar passwords por defecto** en producción
- **Configurar HTTPS** para tokens JWT
- **Backup regular** de base de datos
- **Monitorear logs** de seguridad
- **Actualizar dependencias** periódicamente

---

**Desarrollado para el Proyecto Semestral Full-Stack Level-Up Gamer**
*Spring Boot 3 + JPA + Security JWT + MySQL + Swagger*