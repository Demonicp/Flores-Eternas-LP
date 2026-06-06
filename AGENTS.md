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
```

> Cada desarrollador modifica solo su `.env` local con su puerto, usuario y contraseña.

### Si cambias el `.env`
1.确保 el archivo está en `src/main/resources/.env`
2. Reinicia la aplicación desde Spring Boot Dashboard

## Reglas de Operacion

### Nunca hacer commit ni push
**Nunca realices `git commit` ni `git push` por iniciativa propia.** Solo lo harás cuando el usuario lo solicite explícitamente.

## Comandos de Desarrollo

```bash
# Ejecutar con Maven
cd backend-flp && ./mvnw spring-boot:run

# Compilar
./mvnw clean package

# Solo compilar (sin tests)
./mvnw compile
```

## Notas Técnicas

- **Dialect Hibernate**: PostgreSQLDialect configurado en `application.properties`
- **DDL auto**: `update` — Hibernate crea/actualiza tablas automáticamente
- **Dependencias clave**:
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-webmvc`
  - `postgresql` (runtime)
  - `springboot4-dotenv` (carga automática de .env)