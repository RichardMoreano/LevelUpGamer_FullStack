# Guía para Consumir la API desde Aplicaciones Móviles

## URL Base de la API
- **Producción:** `https://levelup-gamer-backend.up.railway.app/api/v1`
- **Desarrollo:** `http://localhost:8080/api/v1`

## Autenticación

Todas las peticiones (excepto login y register) requieren un token JWT en el header:

```
Authorization: Bearer <tu_token_jwt>
```

### 1. Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "correo": "usuario@ejemplo.cl",
  "password": "contraseña123"
}
```

**Respuesta exitosa:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "run": "12345678-9",
  "nombres": "Juan",
  "apellidos": "Pérez",
  "correo": "usuario@ejemplo.cl",
  "tipoUsuario": "CLIENTE",
  "puntosLevelup": 150
}
```

### 2. Registro
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "run": "12345678-9",
  "nombres": "Juan",
  "apellidos": "Pérez",
  "correo": "usuario@ejemplo.cl",
  "password": "contraseña123",
  "telefono": "+56912345678",
  "direccion": "Calle Falsa 123"
}
```

## Endpoints Principales

### Productos

**Obtener todos los productos activos:**
```http
GET /api/v1/productos/activos
Authorization: Bearer <token>
```

**Obtener producto por código:**
```http
GET /api/v1/productos/{codigo}
Authorization: Bearer <token>
```

**Buscar productos por nombre:**
```http
GET /api/v1/productos/buscar?nombre=PlayStation
Authorization: Bearer <token>
```

**Filtrar por categoría:**
```http
GET /api/v1/productos/categoria/CONSOLAS
Authorization: Bearer <token>
```

### Usuario

**Obtener perfil del usuario actual:**
```http
GET /api/v1/usuarios/me
Authorization: Bearer <token>
```

**Actualizar perfil:**
```http
PUT /api/v1/usuarios/me
Authorization: Bearer <token>
Content-Type: application/json

{
  "telefono": "+56987654321",
  "direccion": "Nueva Dirección 456"
}
```

**Consultar puntos LevelUp:**
```http
GET /api/v1/usuarios/puntos
Authorization: Bearer <token>
```

### Pedidos

**Crear pedido:**
```http
POST /api/v1/pedidos
Authorization: Bearer <token>
Content-Type: application/json

{
  "items": [
    {
      "productoId": "PROD001",
      "cantidad": 2,
      "precioUnitario": 299990
    }
  ],
  "usarPuntos": true,
  "direccionEnvio": "Mi dirección"
}
```

**Obtener historial de pedidos:**
```http
GET /api/v1/pedidos/mis-pedidos
Authorization: Bearer <token>
```

## Manejo de Errores

La API retorna códigos HTTP estándar:

- **200:** Éxito
- **201:** Creado exitosamente
- **400:** Solicitud incorrecta (validación fallida)
- **401:** No autorizado (token inválido o expirado)
- **403:** Prohibido (sin permisos)
- **404:** Recurso no encontrado
- **500:** Error interno del servidor

**Ejemplo de respuesta de error:**
```json
{
  "timestamp": "2025-12-03T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "El correo ya está registrado",
  "path": "/api/v1/auth/register"
}
```

## Ejemplo en Android (Kotlin con Retrofit)

### 1. Dependencias (build.gradle)
```gradle
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.squareup.okhttp3:logging-interceptor:4.10.0'
```

### 2. Modelo de datos
```kotlin
data class LoginRequest(
    val correo: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val tipo: String,
    val run: String,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val tipoUsuario: String,
    val puntosLevelup: Int
)

data class Producto(
    val codigo: String,
    val nombre: String,
    val categoria: String,
    val precio: Double,
    val stock: Int,
    val imagen: String
)
```

### 3. API Service
```kotlin
interface LevelUpApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    
    @GET("productos/activos")
    suspend fun getProductosActivos(@Header("Authorization") token: String): List<Producto>
    
    @GET("usuarios/me")
    suspend fun getPerfil(@Header("Authorization") token: String): Usuario
}
```

### 4. Retrofit Instance
```kotlin
object RetrofitClient {
    private const val BASE_URL = "https://levelup-gamer-backend.up.railway.app/api/v1/"
    
    val api: LevelUpApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LevelUpApi::class.java)
    }
}
```

### 5. Uso en un ViewModel
```kotlin
class LoginViewModel : ViewModel() {
    fun login(correo: String, password: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.api.login(
                    LoginRequest(correo, password)
                )
                // Guardar token
                saveToken(response.token)
                // Navegar a pantalla principal
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }
    
    private fun saveToken(token: String) {
        // Guardar en SharedPreferences o DataStore
    }
}
```

## Ejemplo en React Native

### 1. Instalación
```bash
npm install axios
```

### 2. API Service
```javascript
import axios from 'axios';

const API_BASE_URL = 'https://levelup-gamer-backend.up.railway.app/api/v1';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para agregar token automáticamente
api.interceptors.request.use(
  async (config) => {
    const token = await AsyncStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export const authService = {
  login: (correo, password) => api.post('/auth/login', { correo, password }),
  register: (userData) => api.post('/auth/register', userData),
};

export const productService = {
  getActivos: () => api.get('/productos/activos'),
  getByCodigo: (codigo) => api.get(`/productos/${codigo}`),
  buscar: (nombre) => api.get('/productos/buscar', { params: { nombre } }),
};

export const userService = {
  getPerfil: () => api.get('/usuarios/me'),
  updatePerfil: (data) => api.put('/usuarios/me', data),
  getPuntos: () => api.get('/usuarios/puntos'),
};
```

## Consideraciones de Seguridad

1. **Almacenamiento del Token:**
   - Android: Usar `EncryptedSharedPreferences`
   - iOS: Usar `Keychain`
   - React Native: Usar `react-native-keychain`

2. **HTTPS:**
   - Siempre usar HTTPS en producción
   - No enviar tokens por HTTP

3. **Expiración del Token:**
   - Los tokens expiran después de 24 horas
   - Manejar el error 401 y redirigir al login

4. **Validación:**
   - Validar siempre los datos antes de enviarlos
   - Manejar errores de red apropiadamente

## Documentación Completa

Para ver la documentación interactiva completa de la API, visita:
https://levelup-gamer-backend.up.railway.app/swagger-ui.html

---

*Si tienes dudas o problemas, revisa el repositorio del proyecto o contacta al equipo de desarrollo.*
