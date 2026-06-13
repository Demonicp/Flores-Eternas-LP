# Design System — Flores Eternas LP

## @author esteban
Documentación del sistema de diseño para el proyecto Flores Eternas.
Contiene tokens de colores, tipografía, imágenes y convenciones visuales.

---

## Colores

### Fondo página
- **Nombre**: bg-page
- **Hex**: `#FFFCF6`
- **Descripción**: Color de fondo principal de la aplicación (nude claro)

### Fondo header/botones
- **Nombre**: bg-header
- **Hex**: `#FFEDE3`
- **Descripción**: Color para headers y botones (rosa pálido)

### Texto principal (títulos y subtítulos)
- **Nombre**: text-primary
- **Hex**: `#83572E`
- **Descripción**: Color para títulos, subtítulos y texto importante (marrón)

### Texto blanco
- **Nombre**: text-white
- **Hex**: `#FFFFFF`
- **Descripción**: Color para texto sobre fondos oscuros

### Borde suave
- **Nombre**: border-soft
- **Hex**: `#E8D5C8`
- **Descripción**: Color para bordes y separadores sutiles

### Fondo inputs
- **Nombre**: bg-input
- **Hex**: `#FFFFFF`
- **Descripción**: Fondo blanco para campos de formulario

### Fondo cards
- **Nombre**: bg-card
- **Hex**: `#FCEEE3`
- **Descripción**: Fondo para tarjetas y contenedores internos

---

## Tipografía

### Familia: Poppins
- **Usos**: Opciones de navegación, botones, labels de formularios
- **Variantes**: Regular (400), Medium (500), SemiBold (600)
- **Ejemplo**: `font-family: 'Poppins', sans-serif`

### Familia: Radley
- **Usos**: Títulos decorativos, subtítulos principales, headers de sección
- **Variante**: Regular (400)
- **Ejemplo**: `font-family: 'Radley', serif`

### Familia: Lora
- **Usos**: Precios, valores numéricos destacados
- **Variante**: Regular (400)
- **Ejemplo**: `font-family: 'Lora', serif`

---

## Imágenes y logos

### Logo blanco
- **Nombre**: flplogowhite
- **Ruta**: `/assets/images/flplogowhite.png`
- **Usos**: Sobre fondos oscuros (header cuando hay imagen de fondo)

### Logo negro
- **Nombre**: flplogoblack
- **Ruta**: `/assets/images/flplogoblack.png`
- **Usos**: Sobre fondos claros

### Logo marrón
- **Nombre**: flplogobrown
- **Ruta**: `/assets/images/flplogobrown.png`
- **Usos**: Alternativa para fondos claros

### Imagen de fondo landing
- **Nombre**: landing
- **Ruta**: `/assets/images/landing.jpg`
- **Usos**: Imagen de fondo en la parte superior de las páginas de admin

### Imagen provisional (legacy)
- **Nombre**: FondoProvicional
- **Ruta**: `/assets/images/FondoProvicional.png`
- **Usos**: Temporal, será reemplazada por `landing.jpg`

---

## Variables CSS (Tailwind v4)

Estas variables están definidas en `app/assets/css/main.css` y se usan con la sintaxis de Tailwind:

```css
--color-bg-page: #FFFCF6
--color-bg-header: #FFEDE3
--color-text-primary: #83572E
--color-text-white: #FFFFFF
--color-border-soft: #E8D5C8
--color-bg-input: #FFFFFF
--color-bg-card: #FCEEE3
```

### Clases utilitarias equivalentes

| Variable CSS | Clase Tailwind |
|--------------|----------------|
| bg-page | `bg-[#FFFCF6]` |
| bg-header | `bg-[#FFEDE3]` |
| text-primary | `text-[#83572E]` |
| text-white | `text-white` |
| border-soft | `border-[#E8D5C8]` |
| bg-input | `bg-white` |
| bg-card | `bg-[#FCEEE3]` |

---

## Convenciones de uso

### Header con imagen de fondo
1. Usar imagen `landing.jpg` como `background-image`
2. Overlay semi-transparente oscuro para legibilidad
3. Logo blanco (`flplogowhite.png`) en la parte superior
4. Texto en color `text-white` con sombra para contraste

### Botones primarios
1. Fondo: `bg-[#FFEDE3]`
2. Texto: `text-[#83572E]`
3. Borde redondeado: `rounded-lg`
4. Hover: `opacity-90`

### Títulos y subtítulos
1. Usar fuente `Radley` para títulos principales
2. Color: `text-[#83572E]`
3. Tamaño según jerarquía (text-xl, text-2xl, etc.)

### Precios
1. Usar fuente `Lora`
2. Color: `text-[#83572E]`
3. Formato con símbolo $ y decimales

### Tarjetas y contenedores
1. Fondo blanco o `bg-[#FCEEE3]`
2. Borde: `border border-[#E8D5C8]`
3. Borde redondeado: `rounded-xl`
4. Sombra sutil: `shadow-sm`

### Campos de formulario (inputs)
1. Fondo blanco (`bg-white`)
2. Borde: `border border-[#E8D5C8]`
3. Focus: `ring-2 ring-[#FFEDE3]`
4. Texto placeholder en color suave

---

## Responsividad

- **Mobile**: < 768px
- **Tablet**: 768px - 1024px
- **Desktop**: > 1024px

El layout principal usa max-width de `6xl` (1152px) centrado con `mx-auto`.

---

## Notas de implementación

- El sistema usa Tailwind CSS v4 con PostCSS
- Los colores se manejan como variables CSS custom properties
- La paleta se basa en tonos nude/marrón para mantener coherencia con la marca floral
- Los logos están en PNG con fondo transparente para máxima flexibilidad