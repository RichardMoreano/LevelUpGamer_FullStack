# Como Configurar Render para usar Docker

## El Problema que Tienes

Render esta usando `build.sh` y `start.sh`, pero esos scripts no funcionan porque Render no tiene Java ni Maven instalados.

La solucion es decirle a Render que use **Docker** en lugar de esos scripts.

## Paso a Paso para Arreglarlo

### Opcion A: Si Ya Tienes un Web Service Creado

1. **Abre tu Web Service en Render**
   - Ve a https://dashboard.render.com
   - Haz clic en tu servicio "levelup-gamer-backend" (o como lo hayas llamado)

2. **Ve a Settings (Configuracion)**
   - En el menu izquierdo, haz clic en "Settings"

3. **Cambia el Environment**
   - Busca la seccion "Build & Deploy"
   - Donde dice "Environment", cambialo a: **Docker**

4. **Configura el Dockerfile**
   - Dockerfile Path: `./Dockerfile`
   - Docker Context: `./`

5. **IMPORTANTE: Borra los comandos**
   - Build Command: **Dejar VACIO** (borra todo lo que diga `chmod +x build.sh...`)
   - Start Command: **Dejar VACIO** (borra todo lo que diga `chmod +x start.sh...`)

6. **Guarda los cambios**
   - Haz clic en "Save Changes" abajo

7. **Despliega de nuevo**
   - Ve a "Manual Deploy" (arriba)
   - Haz clic en "Deploy latest commit"
   - Espera 15-20 minutos

### Opcion B: Crear un Nuevo Web Service desde Cero

1. **Borra el Web Service anterior**
   - Ve a Settings → "Delete Web Service"
   - Confirma que quieres borrarlo

2. **Crea uno nuevo**
   - Dashboard → "New +" → "Web Service"

3. **Conecta el repo**
   - Selecciona: RichardMoreano/LevelUpGamer_FullStack
   - Branch: main
   - Haz clic en "Connect"

4. **Llena EXACTAMENTE asi:**

   ```
   Name: levelup-gamer-backend
   
   Region: Oregon (US West)
   
   Branch: main
   
   Root Directory: (dejar vacio)
   
   Environment: Docker  ← ESTO ES LO MAS IMPORTANTE
   
   Dockerfile Path: ./Dockerfile
   
   Docker Context: ./
   
   Build Command: (dejar VACIO, no escribir nada)
   
   Start Command: (dejar VACIO, no escribir nada)
   
   Instance Type: Free
   ```

5. **Agrega variable de entorno**
   - En "Environment Variables"
   - Add Environment Variable
   - Key: `SPRING_PROFILES_ACTIVE`
   - Value: `prod`

6. **Crea el servicio**
   - Haz clic en "Create Web Service"
   - Espera 15-20 minutos

## Como Saber si esta Funcionando

Mientras compila, veras en los logs:

```
==> Using Docker to build...
==> Building image from Dockerfile...
Step 1/X : FROM maven:3.9-eclipse-temurin-17
...
```

**SI ves esto, esta bien** ✅

Si sigue diciendo `mvn: command not found`, entonces **NO configuraste Docker correctamente**. Vuelve a revisar los pasos.

## Una Vez que Termine

1. Render te dara una URL como:
   ```
   https://levelup-gamer-backend.onrender.com
   ```

2. Prueba que funcione:
   ```
   https://levelup-gamer-backend.onrender.com/actuator/health
   ```
   
   Debe responder:
   ```json
   {"status":"UP"}
   ```

3. Actualiza la app Android:
   - Abre: `LevelUpGamerPanelApp/app/src/main/java/com/example/levelupgamerpanel_app/data/api/ApiConfig.kt`
   - Cambia: `URL_PRODUCTION = "https://TU-URL-DE-RENDER.onrender.com/api/v1/"`
   - Recompila la app

## Si Necesitas Ayuda

Los logs en Render te diran exactamente que esta pasando. Si ves:

- ✅ `FROM maven:3.9-eclipse-temurin-17` → Esta usando Docker (bien)
- ❌ `./build.sh: line 7: mvn: command not found` → NO esta usando Docker (mal)

Si sigue sin funcionar, revisa que el campo "Environment" diga **Docker** y que Build Command y Start Command esten **VACIOS**.
