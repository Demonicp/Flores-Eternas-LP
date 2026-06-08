# Historial de Dependencias — Frontend FLP

## 2026-06-06 — Setup inicial

### Producción
| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `pinia` | ^3.0.4 | State management |
| `@pinia/nuxt` | ^0.11.3 | Integración Pinia + Nuxt |

### Desarrollo
| Dependencia | Versión | Propósito |
|------------|---------|-----------|
| `tailwindcss` | ^4.3.0 | Framework CSS utilitario |
| `@tailwindcss/postcss` | ^4.3.0 | Plugin PostCSS para Tailwind |

### Notas
- Se bajó `pnpm` de v11.5.1 a v9.15.9 por compatibilidad con Node.js v20
- Se configuró `pnpm-workspace.yaml` con `onlyBuiltDependencies` para evitar errores de build
- Se usa PostCSS en vez de Vite plugin para mejor compatibilidad con Nuxt

### Comandos recordatorios
```bash
pnpm install              # Instalar todo
pnpm dev                  # Iniciar dev server
```

---

*Actualizar este archivo cada vez que se agregue/elimine una dependencia.*
