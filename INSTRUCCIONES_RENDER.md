# Pasos para Desplegar en Render

Los archivos ya estan listos en el repositorio. Ahora solo necesitas configurar Render.

## Paso 1: Crear cuenta en Render

1. Ve a https://render.com
2. Haz clic en "Get Started" o "Sign Up"
3. Conecta tu cuenta de GitHub

## Paso 2: Crear nuevo Web Service

1. En el dashboard de Render, haz clic en "New +"
2. Selecciona "Web Service"
3. Conecta el repositorio: RichardMoreano/LevelUpGamer_FullStack
4. Haz clic en "Connect"

## Paso 3: Configurar el servicio

Llena los campos con esta informacion:

**Name:** levelup-gamer-backend

**Region:** Oregon (US West) o el que este mas cerca

**Branch:** main

**Root Directory:** (dejar vacio)

**Runtime:** Docker

**Build Command:**
```
docker build -t levelup-backend .
```

**Start Command:**
```
docker run -p $PORT:8080 levelup-backend
```

**Instance Type:** Free

## Paso 4: Variables de entorno

Render detectara automaticamente las variables de `render.yaml`, pero si quieres agregar mas:

En la seccion "Environment", agrega esta variable:

**Clave:** SPRING_PROFILES_ACTIVE
**Valor:** prod

Si necesitas configurar base de datos PostgreSQL de Render, agrega tambien:

**Clave:** SPRING_DATASOURCE_URL
**Valor:** (la URL de tu base de datos PostgreSQL de Render)

**Clave:** SPRING_DATASOURCE_USERNAME
**Valor:** (tu usuario de PostgreSQL)

**Clave:** SPRING_DATASOURCE_PASSWORD
**Valor:** (tu password de PostgreSQL)

## Paso 5: Desplegar

1. Haz clic en "Create Web Service"
2. Espera a que compile (puede tardar 10-15 minutos la primera vez)
3. Una vez que termine, Render te dara una URL como:
   ```
   https://levelup-gamer-backend.onrender.com
   ```

## Paso 6: Verificar que funciona

Abre en tu navegador la URL que te dio Render mas /actuator/health

Por ejemplo:
```
https://levelup-gamer-backend.onrender.com/actuator/health
```

Deberia responder:
```json
{"status":"UP"}
```

Si ves eso, el backend esta funcionando correctamente.

## Paso 7: Probar el login

Puedes probar que el login funciona con este comando (reemplaza la URL):

```bash
curl -X POST https://levelup-gamer-backend.onrender.com/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"correo":"admin@levelup.cl","password":"admin123"}'
```

Si obtienes un token JWT, todo esta funcionando bien.

## Paso 8: Actualizar la app Android

Una vez que tengas la URL de Render, actualiza la app Android:

1. Abre: LevelUpGamerPanelApp/app/src/main/java/com/example/levelupgamerpanel_app/data/api/ApiConfig.kt

2. Cambia esta linea:
   ```kotlin
   private const val URL_PRODUCTION = "https://TU-URL-DE-RENDER.onrender.com/api/v1/"
   ```

3. Asegurate de que este en modo produccion:
   ```kotlin
   private const val IS_DEVELOPMENT = false
   ```

4. Recompila la app y pruebala

## Notas importantes

- El plan gratuito de Render puede dormir tu app despues de 15 minutos sin uso
- Cuando la app esta dormida, la primera peticion puede tardar 30-60 segundos
- Si hay errores, revisa los logs en el dashboard de Render
- Puedes ver los logs en tiempo real desde el dashboard

## Si algo sale mal

1. Revisa los logs en Render
2. Verifica que las variables de entorno esten correctas
3. Asegurate de que el repositorio se haya actualizado correctamente
4. Intenta hacer un nuevo despliegue desde el dashboard de Render

Una vez que este funcionando, ya podras usar la app Android desde cualquier dispositivo con internet.
