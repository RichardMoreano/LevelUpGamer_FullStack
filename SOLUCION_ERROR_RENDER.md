# Solucion al Error de Render

## El Problema

Render no tiene Java ni Maven instalados por defecto. Por eso fallo con:
- `mvn: command not found`
- `java: command not found`

## La Solucion: Usar Docker

Ya cree un `Dockerfile` que tiene todo lo necesario (Java + Maven).

## IMPORTANTE: Debes Cambiar la Configuracion

Render sigue usando `build.sh` y `start.sh`. Necesitas decirle que use Docker.

## Pasos para Arreglarlo

### SI YA CREASTE EL WEB SERVICE:

1. **Ve a tu Web Service en Render**
2. **Haz clic en "Settings" (Configuracion)**
3. **Busca la seccion "Build & Deploy"**
4. **Cambia estos valores:**
   
   **Environment:** Docker
   
   **Dockerfile Path:** ./Dockerfile
   
   **Docker Context:** ./
   
   **Borra estos campos (dejalos vacios):**
   - Build Command: (borrar todo)
   - Start Command: (borrar todo)

5. **Haz clic en "Save Changes"**
6. **Ve a "Manual Deploy" y haz clic en "Deploy latest commit"**

### SI NO HAS CREADO EL WEB SERVICE AUN:

1. **Borra el Web Service anterior** (si existe)
2. **Ve a Render Dashboard**
3. **Haz clic en "New +" → "Web Service"**
4. **Conecta el repositorio:** RichardMoreano/LevelUpGamer_FullStack
5. **Configuracion manual (NO uses el render.yaml):**

**Name:** levelup-gamer-backend

**Region:** Oregon (US West)

**Branch:** main

**Root Directory:** (dejar vacio)

**Environment:** Docker ← MUY IMPORTANTE

**Dockerfile Path:** ./Dockerfile

**Docker Context:** ./

**Instance Type:** Free

6. **NO llenes Build Command ni Start Command** (deja esos campos vacios)

7. **En "Environment Variables" agrega:**
   - Key: SPRING_PROFILES_ACTIVE
   - Value: prod

8. **Haz clic en "Create Web Service"**

9. **Espera 15-20 minutos** (Docker tarda mas la primera vez)

## Verificar que funciona

Una vez desplegado, abre:
```
https://TU-URL.onrender.com/actuator/health
```

Deberia responder:
```json
{"status":"UP"}
```

## Si sigue fallando

Revisa los logs en Render. Busca errores como:
- Problemas de compilacion Maven
- Errores de conexion a base de datos
- Puerto incorrecto

## Notas importantes

- Docker tarda mas en compilar la primera vez (15-20 minutos)
- Las siguientes compilaciones son mas rapidas (5-10 minutos)
- El plan gratuito puede dormir la app despues de 15 minutos sin uso
- La primera peticion despues de dormir puede tardar 30-60 segundos

## Base de datos

Si necesitas PostgreSQL:

1. En Render, crea una "PostgreSQL" database
2. Copia la "Internal Database URL"
3. Agregala como variable de entorno:
   - SPRING_DATASOURCE_URL = (la URL que copiaste)

El archivo `application-prod.yml` ya esta configurado para usar PostgreSQL.

## Despues del despliegue

1. Copia la URL que te dio Render
2. Actualiza `ApiConfig.kt` en la app Android con esa URL
3. Recompila la app Android
4. Prueba la app
