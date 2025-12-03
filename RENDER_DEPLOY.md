# Instrucciones para Desplegar en Render

## Pasos para configurar el despliegue

### 1. Crear cuenta en Render
Ve a https://render.com y crea una cuenta gratuita

### 2. Conectar el repositorio
- Clic en "New" y selecciona "Web Service"
- Conecta tu cuenta de GitHub
- Selecciona el repositorio: RichardMoreano/LevelUpGamer_FullStack

### 3. Configurar el servicio

**Configuracion basica:**
- Name: levelup-gamer-backend
- Region: Oregon (US West) o el mas cercano
- Branch: main
- Root Directory: (dejar vacio)

**Comandos de compilacion:**
- Build Command: `chmod +x build.sh && ./build.sh`
- Start Command: `chmod +x start.sh && ./start.sh`

**Variables de entorno:**
Agregar estas variables en la seccion Environment:

```
SPRING_PROFILES_ACTIVE=prod
```

Si necesitas configurar la base de datos, agrega:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://tu-host:5432/tu-bd
SPRING_DATASOURCE_USERNAME=tu-usuario
SPRING_DATASOURCE_PASSWORD=tu-password
```

### 4. Desplegar
- Clic en "Create Web Service"
- Espera a que compile y despliegue (puede tardar 5-10 minutos)

### 5. Verificar que funciona
Una vez desplegado, Render te dara una URL como:
```
https://levelup-gamer-backend.onrender.com
```

Prueba que funciona accediendo a:
```
https://levelup-gamer-backend.onrender.com/actuator/health
```

Deberia responder con: `{"status":"UP"}`

### 6. Actualizar la app Android
Abre el archivo `LevelUpGamerPanelApp/app/src/main/java/com/example/levelupgamerpanel_app/data/api/ApiConfig.kt`

Cambia la URL de produccion:
```kotlin
private const val URL_PRODUCTION = "https://levelup-gamer-backend.onrender.com/api/v1/"
```

## Notas importantes

- Render puede tardar un poco en iniciar si la app estuvo inactiva (plan gratuito)
- La primera compilacion puede tardar hasta 15 minutos
- Si hay errores, revisa los logs en el dashboard de Render
- El plan gratuito puede dormir la app despues de 15 minutos sin uso

## Solucionar problemas comunes

### Error de compilacion
- Verifica que el JDK 17 este configurado en el proyecto
- Revisa que todas las dependencias esten en el pom.xml

### Error de inicio
- Revisa las variables de entorno
- Verifica los logs en Render para ver el error especifico

### Error de base de datos
- Si usas PostgreSQL de Render, copia la URL de la base de datos
- Agregala en las variables de entorno

## Proximos pasos despues del despliegue

1. Probar los endpoints principales
2. Actualizar el frontend para usar la nueva URL
3. Actualizar la app Android para usar la nueva URL
4. Hacer pruebas completas de login y funcionalidades
