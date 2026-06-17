# Análisis de Logging Estructurado — Flores Eternas LP

## Antes del refactoring

### Estado del logging (16/06/2026)

El proyecto **no contaba con logging estructurado**. Toda la capacidad de logging se reducía a una
clase utilitaria custom (`AdminLoginLogger`) que escribía en texto plano mediante `System.out`
y `FileWriter`.

### Evidencias

| Aspecto | Estado |
|---|---|
| **Framework de logging** | Ninguno. SLF4J y Logback disponibles como dependencias transitivas pero no utilizados |
| **Configuración de logging** | No existía ningún archivo (`logback.xml`, `logback-spring.xml`, `log4j2.xml`, etc.) |
| **Clase de logging** | Solo `utils/AdminLoginLogger.java` — clase custom con `System.out` + `FileWriter` |
| **Formato de salida** | Texto plano no parseable: `[timestamp] ADMIN_LOGIN \| email=... \| resultado=... \| ...` |
| **Cobertura de logging** | Solo `AuthService.java` (login y register de admin). Sin logging en `GlobalExceptionHandler`, `JwtAuthenticationFilter`, ni ningún otro service |
| **Campos registrados** | 3 campos en texto plano: email, resultado, detalles |
| **Parseabilidad** | No es parseable automáticamente por herramientas (Elasticsearch, Grafana, Splunk, etc.) |
| **IP del cliente** | No se registraba |
| **User-Agent** | No se registraba |

### Archivos involucrados (antes)

```
src/main/java/flores/eternas/backend/utils/AdminLoginLogger.java   (creado)
src/main/java/flores/eternas/backend/services/AuthService.java     (modificado)
src/test/java/flores/eternas/backend/utils/AdminLoginLoggerTest.java (creado)
```

### Ejemplo de salida (antes)

```
[2026-06-16 18:06:01] ADMIN_LOGIN | email=admin@test.com | resultado=EXITOSO | ID: 1 | Rol: ADMIN
```

---

## Después del refactoring

### Capacidades agregadas

Se implementó **logging estructurado en formato JSON** utilizando el ecosistema SLF4J + Logback,
con el encoder `logstash-logback-encoder` para producir salida JSON estándar.

### Cambios realizados

| Cambio | Detalle |
|---|---|
| **Dependencia agregada** | `net.logstash.logback:logstash-logback-encoder:8.0` en `pom.xml` |
| **Configuración Logback** | `logback-spring.xml` con appender RollingFileAppender + LogstashEncoder |
| **Logger específico** | `ADMIN_LOGIN` — logger con nombre canónico para filtrar eventos de admin |
| **IP del cliente** | Se obtiene vía `RequestContextHolder` -> `HttpServletRequest.getRemoteAddr()` |
| **User-Agent** | Se obtiene del header `User-Agent` de la petición HTTP |
| **Eliminado código obsoleto** | `AdminLoginLogger.java` reemplazado por SLF4J estándar |
| **Formato de salida** | JSON estructurado, parseable por herramientas de observabilidad |

### Campos estructurados incluidos

| Campo | Tipo | Descripción |
|---|---|---|
| `@timestamp` | ISO 8601 | Marca de tiempo del evento |
| `logger` | string | `"ADMIN_LOGIN"` |
| `level` | string | Nivel de log (`INFO`, `WARN`, `ERROR`) |
| `message` | string | Descripción del evento (`login`, `register`) |
| `email` | string | Correo del usuario que intenta autenticarse |
| `resultado` | string | `INTENTO`, `EXITOSO` o `FALLIDO` |
| `id` | number | ID del usuario (solo en EXITOSO) |
| `rol` | string | Rol del usuario (solo en EXITOSO) |
| `ip` | string | Dirección IP del cliente |
| `userAgent` | string | User-Agent del navegador/cliente HTTP |
| `accion` | string | Tipo de operación (`Inicio de sesion`, `Registro de administrador`) |
| `razon` | string | Motivo del fallo (solo en FALLIDO) |

### Ejemplo de salida (después)

```json
{
  "@timestamp": "2026-06-16T18:10:00.000-05:00",
  "logger": "ADMIN_LOGIN",
  "level": "INFO",
  "message": "login",
  "email": "admin@ejemplo.com",
  "resultado": "EXITOSO",
  "id": 1,
  "rol": "ADMIN",
  "ip": "192.168.1.10",
  "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36",
  "accion": "Inicio de sesion"
}
```

### Archivos resultantes

```
src/main/resources/logback-spring.xml                        (creado)
src/test/resources/logback-test.xml                           (creado)
src/main/java/.../services/AuthService.java                   (modificado)
src/test/java/.../utils/AdminLoginLoggerTest.java             (modificado)
src/main/java/.../utils/AdminLoginLogger.java                  (eliminado)
pom.xml                                                        (modificado)
```

### Beneficios

1. **Parseabilidad universal**: Cualquier herramienta que consuma JSON (Elasticsearch, Logstash, Grafana, Splunk, AWS CloudWatch) puede indexar y consultar estos logs
2. **Campos semánticos**: Cada dato tiene un nombre canónico (`email`, `ip`, `rol`, etc.) facilitando búsquedas y agregaciones
3. **IP y User-Agent**: Permite auditoría de origen geográfico y dispositivos
4. **Estándar de la industria**: SLF4J + Logback es el stack de logging más usado en el ecosistema Spring Boot
5. **Cero dependencias nuevas no estándar**: `logstash-logback-encoder` es el estándar de facto para JSON logging en Logback

---


