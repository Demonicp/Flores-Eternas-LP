# AGENTS.md — Flores Eternas LP

## Proyecto
- **Backend**: `backend-flp/` — Spring Boot 4 + PostgreSQL + Hibernate
- **Puerto default PostgreSQL**: 5432, **BD default**: flores_eternas_lp

## Configuración de Entorno

### Variables de entorno
El proyecto usa `springboot4-dotenv` para cargar `.env` automáticamente desde el classpath.

**Ubicación del `.env`**: `backend-flp/src/main/resources/.env`

Variables requeridas:
```
DB_URL=jdbc:postgresql://localhost:5433/flp
DB_USERNAME=postgres
DB_PASSWORD=tu_contraseña
DDL_AUTO=update
JWT_SECRET=tu_clave_secreta_jwt_minimo_64_caracteres
```

> Cada desarrollador modifica solo su `.env` local con su puerto, usuario y contraseña.
> **Importante**: `JWT_SECRET` debe ser mínimo 64 caracteres para HMAC-SHA.

### Si cambias el `.env`
1. Asegúrate que el archivo está en `src/main/resources/.env`
2. Reinicia la aplicación desde Spring Boot Dashboard

## Reglas de Operacion

### Nunca hacer commit ni push
**Nunca realices `git commit` ni `git push` por iniciativa propia.** Solo lo harás cuando el usuario lo solicite explícitamente.

### Documentación de código
- Usar `@author esteban` en todos los comentarios Javadoc
- Documentar propósito de cada método y clase
- Comentarios concisos pero descriptivos

## Comandos de Desarrollo

```bash
# Ejecutar con Maven
cd backend-flp && ./mvnw spring-boot:run

# Compilar
./mvnw clean package

# Solo compilar (sin tests)
./mvnw compile
```

## Arquitectura Backend

```
backend-flp/src/main/java/flores/eternas/backend/
├── BackendApplication.java
├── config/                    # Configuraciones (Security, Web)
├── controller/                # REST Controllers
├── dto/                       # Data Transfer Objects
├── exception/                 # Excepciones y manejo de errores
├── model/                     # Entidades JPA
├── repository/                # JPA Repositories
├── services/                  # Lógica de negocio
└── utils/                     # Utilidades (JWT)
```

## Notas Técnicas

### Autenticación JWT
- Tokens con expiración de 24 horas
- Contraseñas hasheadas con BCrypt
- Clave secreta configurable via `JWT_SECRET`
- Endpoints `/api/auth/**` públicos, `/api/admin/**` requieren rol ADMIN

### Relaciones JPA
- `Usuario.persona` usa `cascade = CascadeType.ALL` — al guardar un Usuario se guarda automáticamente su Persona asociada

### Dependencias clave
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-webmvc`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- `postgresql` (runtime)
- `springboot4-dotenv` (carga automática de .env)
- `jjwt-api/impl/jackson` (JWT)

### Dialect y DDL
- **Dialect Hibernate**: PostgreSQLDialect configurado en `application.properties`
- **DDL auto**: `update` — Hibernate crea/actualiza tablas automáticamente

## Session Summary (2026-06-25)

### Goal
- Add flower icon management: `icono` field on `TipoFlor` entity, dropdown in admin `OpcionesSection.vue`, `<Icon>` in personalization flow replacing 🌸 emoji.

### Changes
- **Backend**: +`icono` (String, nullable) column in `TipoFlor.java`; added to `TipoFlorDTO` constructor (5th param); mapped in `TipoFlorService.java` (crear, actualizar, toDTO); updated callsites in `FlorService.java` and `RamoService.java`.
- **Frontend model**: `tipo-flor.model.ts` + `icono?: string | null`.
- **Frontend store**: `negocio.store.ts` +`florFormIcono` state, included in `guardarFlor` (as nullable payload field), `editarFlor`, `resetFlorForm`.
- **Admin UI**: `OpcionesSection.vue` — 4-column grid with icon dropdown (19 `mdi:` icon options) + live preview; icon column in table.
- **Personalization flow**: 🌸 → `<Icon :icon="flor.icono || 'mdi:flower-tulip-outline'" />` in `SeleccionFlor.vue`, `seleccion-apartados.vue`, `adiciones.vue`, `resumen-pedido.vue`.
- **Build**: `pnpm build` passes. Backend requires JAVA_HOME to verify.