# Solucion al Error de Render

## El Problema

Render no tiene Java ni Maven instalados por defecto. Por eso fallo con:
- `mvn: command not found`
- `java: command not found`

## La Solucion: Usar Docker

Ya cree un `Dockerfile` que tiene todo lo necesario (Java + Maven).

## Nuevos Pasos para Desplegar

### Opcion 1: Dejar que Render detecte automaticamente (MAS FACIL)

1. Borra el Web Service que creaste antes (si lo creaste)
2. Ve a Render Dashboard
3. Haz clic en "New +" → "Web Service"
4. Conecta el repositorio: RichardMoreano/LevelUpGamer_FullStack
5. Render detectara el archivo `render.yaml` automaticamente
6. Haz clic en "Apply" y luego "Create Web Service"
7. Espera a que compile (15-20 minutos la primera vez)

### Opcion 2: Configuracion manual

Si la opcion 1 no funciona, usa estos valores:

**Name:** levelup-gamer-backend

**Region:** Oregon (US West)

**Branch:** main

**Root Directory:** (dejar vacio)

**Runtime:** Docker

**Dockerfile Path:** ./Dockerfile

**Docker Context:** ./

**Instance Type:** Free

**Variables de entorno:**
- SPRING_PROFILES_ACTIVE = prod

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
