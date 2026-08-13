# Desarrollo de Sudolux

## Enfoque de desarrollo

Sudolux se desarrolla de forma progresiva, priorizando una base clásica de Sudoku estable antes de añadir funciones avanzadas.

## Estructura actual

La versión web se organiza en archivos con responsabilidades separadas:

- `index.html`: estructura semántica de la portada, contenido informativo, hero orbital y demo jugable;
- `styles.css`: estilos generales, secciones, componentes, tablero y responsive;
- `hero-orbit.css`: estilos exclusivos del sistema orbital del hero;
- `script.js`: navegación, revelado progresivo, demostración de reglas y lógica del Sudoku;
- `privacy/index.html`: política pública de privacidad;
- `privacy/styles.css`: estilos exclusivos de la política de privacidad;
- `sitemap.xml`: URLs públicas indexables;
- `favicon.svg` y `favicon.ico`: identidad gráfica;
- `.nojekyll`: publicación estática directa mediante GitHub Pages.

El hero orbital se declara directamente en `index.html`; `script.js` no reescribe su marcado. De esta manera, contenido, presentación y lógica mantienen responsabilidades diferenciadas.

## Publicación

La versión web se publica desde la rama `main` mediante GitHub Pages en `https://neuronova-apps.github.io/sudolux-app/`.

## Componentes actuales

La versión web incluye:

- interfaz tecnológica y responsive;
- sección teórica;
- demostración interactiva de reglas;
- tablero 9 × 9 jugable;
- entrada mediante teclado y panel numérico;
- comprobación de movimientos;
- reinicio del tablero actual;
- generación aleatoria de una variante válida por carga;
- aviso del navegador cuando existe una partida en progreso y se intenta abandonar la página;
- módulo central de accesibilidad de Neuronova Apps.

## Generación del tablero

La web parte de una pareja tablero-solución válida y aplica transformaciones que preservan la estructura del Sudoku, como:

- permutación de dígitos;
- reorganización válida de filas dentro de bandas;
- reorganización válida de columnas dentro de pilas;
- intercambio de bandas y pilas;
- transposición cuando corresponda.

Este enfoque permite mostrar una variante distinta sin introducir inconsistencias en la solución.

## Estado de partida

Mientras exista progreso sin completar, la página utiliza `beforeunload` para solicitar confirmación antes de recargar, cerrar o abandonar el sitio.

El botón Reiniciar conserva el mismo Sudoku de la sesión y elimina únicamente los movimientos realizados tras confirmación.

## Accesibilidad

El tablero debe procurar:

- selección visible;
- identificación de fila y columna;
- navegación con flechas;
- entrada con teclas 1–9;
- borrado con `Backspace` o `Delete`;
- foco visible;
- mensajes de estado accesibles;
- compatibilidad con ampliación de texto y alto contraste.

## Pruebas prioritarias

1. validez de las variantes generadas;
2. navegación completa con teclado;
3. funcionamiento en móvil y escritorio;
4. persistencia visual de selección y estados;
5. protección contra pérdida accidental de progreso;
6. accesibilidad de mensajes de error y finalización.

## Estado

Desarrollo activo. La web seguirá siendo deliberadamente más limitada que la aplicación móvil.
