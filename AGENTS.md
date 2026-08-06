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

### Nunca agregar colaboradores al repositorio
**Nunca agregues a `opencode` ni a `claude` (ni a ningun otro agente / bot de IA) como colaborador del repositorio en GitHub ni en ninguna otra plataforma.** Esto aplica para invites directos, GitHub Apps, OAuth Apps, o cualquier otro mecanismo de colaboracion. Solo se agregaran personas reales cuando el usuario lo solicite explicitamente.

### Documentación de código
- Usar `@author <nombre>`, documentado con el nombre de usuario de quien escribió el código, en todos los comentarios Javadoc
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

## Session Summary (2026-08-05) — Código hecho por santiago para correcciones 5/08/2026

### Goal
- Corregir problemas visuales del carrito de compras (sidebar) en el catálogo: bordes incompletos (faltaban derecho e inferior), despegue del borde derecho en resoluciones grandes, y altura fija que no crecía con los productos.

### Cambios frontend
- **`app/pages/index.vue`**: el contenedor `flex max-w-7xl mx-auto` se cambió a `flex` simple (sin max-width ni centrado) para que el carrito quede pegado al borde derecho en todas las resoluciones. Wrapper del carrito: `w-80 flex-shrink-0 sticky top-0 self-start` (se eliminó `h-screen overflow-hidden` para que el carrito crezca hacia abajo con el contenido).
- **`app/components/catalogo/CarritoLateral.vue`**: sidebar de `border-l` a `border` completo (4 lados) y `h-full` a `max-h-screen`, para que la altura sea dinámica (crece con los productos, topa en el viewport).

### Notas
- **No commit/push**: no se subió nada a remoto. Ningún commit fue realizado.
- **Build**: `pnpm build` pasa correctamente.
- Backend no modificado en esta sesión.

## Session Summary (2026-08-05) — Checkout departamento→ciudad + retiro en local (santiago 5/08/2026)

### Goal
- Dependencia real departamento → ciudad en el checkout (paquete oficial DANE `divipola`), y nueva opción de **retiro en local** que inyecta la dirección del local al pedido sin cambiar las validaciones del backend (`@NotBlank` de `direccionEntrega`/`ciudad`/`region` se mantienen intactas).

### Backend (módulo `local` creado y compilado)
- `model/Local.java`: entidad `local` con `nombreLocal`, `direccion`, `ciudad`, `region`, `activo`.
- `repository/LocalRepository.java`: `findByActivoTrue()`.
- `services/LocalService.java` y `controller/LocalController.java`: **GET `/api/locales`** (público) + CRUD en **`/api/admin/locales`**.
- `config/SecurityConfig.java`: `.requestMatchers("/api/locales/**").permitAll()`.
- `config/DataSeeder.java`: seed del local "Local Principal — Palmira" (Calle 25 #28-32) **movido antes** del early-return de categorías para que se cree también en BD ya pobladas.

### Frontend
- `app/utils/colombia.ts`: **reemplaza el dataset corrupto** por wrapper del paquete **`divipola`** (pnpm dep). Exporta `getDepartamentos()` y `getCiudades(dep)` (normaliza nombres DANE → etiquetas amigables, maneja Bogotá D.C. y San Andrés y Providencia).
- `app/models/local.model.ts` + `app/services/local.service.ts` (`listarActivos()` cacheado).
- `app/stores/cart.store.ts`: `checkoutForm` gana `modoEntrega` ('domicilio' | 'retiro') y `localSeleccionadoId`.
- `app/components/catalogo/ContCartInterno.vue`: radio modo entrega; departamento y ciudad como **selects encadenados**; en modo retiro oculta dirección/ciudad/departamento y pide elegir local de retiro.
- `app/components/catalogo/CarritoLateral.vue`: al pagar en modo retiro, inyecta `direccion/ciudad/region` del local (payload y form Wompi `shipping-address`).
- `app/pages/flor/resumen-pedido.vue`: misma lógica en el flujo de ramo personalizado.
- Admin: `app/pages/admin/pedidos.vue` y `pedidos/[id].vue` muestran **"Retiro en local: {nombre}"** cuando `direccionEntrega` coincide con la dirección de un local.

### Notas
- **UI admin CRUD de locales**: pendiente (futuro). El módulo backend ya expone los endpoints admin.
- **No commit/push**. Builds: `pnpm build` y `mvn compile` pasan.

## Session Summary (2026-08-05) — Tests del módulo local (santiago 5/08/2026)

### Goal
- Crear tests para el módulo `local` siguiendo el patrón de los tests existentes del backend, y compilar/ejecutar toda la suite para verificar que todo sigue funcionando.

### Tests nuevos (backend, `src/test/java/flores/eternas/backend/`)
- `repository/LocalRepositoryTest.java` (`@DataJpaTest` + H2): `findByActivoTrue` devuelve solo activos, lista vacía sin activos/registros, guardar y buscar por id. (5 tests)
- `services/LocalServiceTest.java` (`@SpringBootTest`): `listarActivos` filtra inactivos, `listarTodos` incluye todos, `crear` persiste, `actualizar` ok, `actualizar` id inexistente → `RuntimeException`, `eliminar` ok, `eliminar` id inexistente → excepción. (7 tests)
- `controller/LocalControllerTest.java` (`@SpringBootTest` + `@AutoConfigureMockMvc` + `@MockitoBean`): **solo endpoint público** `GET /api/locales` → 200 + JSON; sin tocar seguridad (no se generan tokens de admin en tests). (2 tests)

### Resultado de la suite
- `mvn test` (Maven local 3.9.16; `mvnw` roto por red): **Tests run: 100, Failures: 0, Errors: 0, Skipped: 0** — BUILD SUCCESS. Todos los tests existentes siguen pasando (ninguna regresión).
- `pnpm build`: BUILD SUCCESS.
- `EmailIntegrationTest` loguea "Authentication failed" al intentar SMTP local (`localhost:3025`) pero pasa porque usa `assertDoesNotThrow`; es comportamiento preexistente, no modificado.

### Notas
- Javadoc de los 3 tests nuevos con `@author santiago` (código de esta sesión).
- **No commit/push**. Sin cambios en código de producción ni en tests existentes.