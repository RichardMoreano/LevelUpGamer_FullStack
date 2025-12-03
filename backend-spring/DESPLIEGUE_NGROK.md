# Despliegue Rápido con ngrok

## ¿Qué es ngrok?
ngrok crea un túnel HTTPS desde internet hacia tu localhost, permitiendo que cualquier dispositivo acceda a tu backend local.

## Instalación

### Windows:
1. Descarga: https://ngrok.com/download
2. Extrae `ngrok.exe` en una carpeta
3. Abre CMD en esa carpeta

### Alternativa (con Chocolatey):
```bash
choco install ngrok
```

## Uso

### 1. Inicia tu backend local:
```bash
cd backend-spring
mvn spring-boot:run
```

### 2. En otra terminal, inicia ngrok:
```bash
ngrok http 8080
```

### 3. Verás algo como esto:
```
Forwarding   https://abc123.ngrok-free.app -> http://localhost:8080
```

### 4. Copia la URL HTTPS (ejemplo: `https://abc123.ngrok-free.app`)

### 5. Actualiza la app Android:

Abre `ApiConfig.kt` y cambia:
```kotlin
private const val URL_PRODUCTION = "https://abc123.ngrok-free.app/api/v1/"
private const val IS_DEVELOPMENT = false
```

¡Listo! Ahora cualquier dispositivo puede acceder a tu backend local.

## Ventajas
- ✅ Gratis
- ✅ Funcionamiento inmediato
- ✅ No requiere despliegue
- ✅ Ideal para pruebas

## Desventajas
- ⚠️ La URL cambia cada vez que reinicias ngrok (en la versión gratuita)
- ⚠️ Tu PC debe estar encendida
- ⚠️ Solo para desarrollo/pruebas

## Consejos
- Mantén ngrok corriendo mientras pruebas la app
- Si reinicias ngrok, la URL cambiará y deberás actualizar `ApiConfig.kt`
- Para una URL fija, usa la versión de pago de ngrok o despliega en Render/Railway
