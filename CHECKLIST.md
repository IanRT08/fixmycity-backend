# FixMyCity Backend - Guía de configuración

## ⚙️ Requisitos
- Java 17+
- Maven
- Las credenciales del proyecto (pídelas al equipo)

---

## 🚀 Configuración inicial

### 1. Clonar el repositorio
```bash
git clone <url-del-repo>
```

### 2. Agregar la Wallet de Oracle
Coloca la carpeta `Wallet_FIXMYCITY` en:
```
src/main/resources/Wallet_FIXMYCITY/
```

### 3. Crear el archivo de configuración local
Crea el archivo `src/main/resources/application-local.properties` con las credenciales que te compartieron:
```properties
spring.datasource.url=jdbc:oracle:thin:@fixmycity_high?TNS_ADMIN=src/main/resources/Wallet_FIXMYCITY
spring.datasource.username=ADMIN
spring.datasource.password=EL_PASSWORD_QUE_TE_COMPARTIERON

jwt.secret=EL_JWT_SECRET_QUE_TE_COMPARTIERON
jwt.expiration=86400000

cors.allowed.origins=http://localhost:3000,http://localhost:4200
```

### 4. Ejecutar
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

---

## 🧪 Verificar que funciona

Login de prueba:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"nombreUsuario":"tu_usuario","contrasenia":"tu_password"}'
```
Debe regresar un token JWT.

---

## 🚨 Problemas comunes

**"Could not resolve placeholder 'DB_PASSWORD'"**
→ Falta el `application-local.properties` o no estás corriendo con el perfil `local`

**"Invalid JWT signature"**
→ El `jwt.secret` no coincide con el que se usó para generar el token, vuelve a hacer login

**"CORS policy error"**
→ Agrega tu origen a `cors.allowed.origins` en el `application-local.properties`

**"TNS_ADMIN" o error de conexión a BD**
→ Verifica que la carpeta `Wallet_FIXMYCITY` esté en la ruta correcta
