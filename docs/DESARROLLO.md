# Desarrollo de Sudolux

## Estado actual

**Demo web funcional en desarrollo activo.**

La web ya integra contenido introductorio, reglas interactivas y una partida clásica 9 × 9 jugable. La partida en curso puede persistir localmente en el navegador. La aplicación móvil permanece como una expansión futura y no es requisito para que la versión web tenga valor propio.

## Enfoque de desarrollo

Sudolux se desarrolla de forma progresiva, priorizando una base clásica de Sudoku estable antes de añadir funciones avanzadas.

## Estructura actual

La versión web se organiza en archivos con responsabilidades separadas:

- `index.html`: estructura semántica de la portada, contenido informativo, hero orbital y demo jugable;
- `styles.css`: estilos generales, secciones, componentes, tablero y responsive;
- `hero-orbit.css`: estilos exclusivos del sistema orbital del hero;
- `script.js`: navegación, revelado progresivo, demostración de reglas, lógica del Sudoku y persistencia local segura;
- `privacy/index.html`: política pública de privacidad;
- `privacy/styles.css`: estilos exclusivos de la política de privacidad;
- `sitemap.xml`: URLs públicas indexables;
- `favicon.svg` y `favicon.ico`: identidad gráfica;
- `.nojekyll`: publicación estática directa mediante GitHub Pages.

El hero orbital se declara directamente en `index.html`; `script.js` no reescribe su marcado. De esta manera, contenido, presentación y lógica mantienen responsabilidades diferenciadas.

## Publicación

La versión web se publica desde la rama `main` mediante GitHub Pages en `https://neuronova-apps.github.io/sudolux-app/`.

## Componentes actuales

La demo web funcional incluye:

- interfaz tecnológica y responsive;
- sección teórica;
- demostración interactiva de reglas;
- tablero 9 × 9 jugable;
- entrada mediante teclado y panel numérico;
- validación inmediata de movimientos;
- registro de casillas falladas por posición única;
- comprobación del estado actual del tablero sin alterar ese registro;
- persistencia local de una partida en curso;
- restauración del tablero, solución, movimientos, casillas falladas y selección;
- validación defensiva de datos guardados;
- fallback en memoria cuando `localStorage` no está disponible;
- reinicio del tablero actual;
- generación aleatoria de una variante válida cuando no existe progreso persistido;
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

## Persistencia local

La partida activa utiliza la clave `sudolux-demo-v1` en `localStorage`.

Se guardan únicamente datos necesarios para continuar la demo:

- tablero generado;
- solución correspondiente;
- valores introducidos;
- índices de casillas falladas;
- casilla seleccionada.

Antes de restaurar, el código verifica:

- longitud y rango numérico de los tres tableros;
- validez completa de filas, columnas y bloques de la solución;
- coherencia entre pistas fijas, solución y valores restaurados;
- índices válidos de casillas falladas;
- selección dentro del rango del tablero.

Si los datos no son válidos, se ignoran y se genera una nueva variante. Si `localStorage` lanza un error o está bloqueado, Sudolux sigue funcionando en memoria.

El almacenamiento se mantiene solo mientras exista progreso sin completar. Un tablero sin movimientos no queda fijado indefinidamente. Reiniciar hasta dejar el tablero limpio o completar correctamente la partida elimina el guardado local, permitiendo que una recarga posterior genere otra variante.

## Modelo de validación y errores

La entrada de un número se valida inmediatamente contra la solución de la variante actual.

La métrica visible **Casillas falladas** utiliza un conjunto de índices (`Set`) y representa posiciones distintas que han recibido al menos un intento incorrecto. Varias respuestas erróneas en la misma casilla siguen contando como una sola casilla fallada.

Corregir o borrar posteriormente una casilla no elimina su registro histórico durante la partida. El botón **Comprobar partida** recorre el tablero y marca las respuestas incorrectas que siguen presentes en ese momento, pero no añade nuevas posiciones al registro de casillas falladas. Así se mantienen separados:

- el historial de casillas que causaron al menos un error;
- el estado actual de respuestas incorrectas del tablero.

Reiniciar la demo devuelve el registro de casillas falladas a cero junto con los movimientos realizados.

## Estado de partida

Mientras exista progreso sin completar, la página utiliza `beforeunload` como protección adicional antes de recargar, cerrar o abandonar el sitio. Aunque la partida se guarda localmente, esta confirmación reduce salidas accidentales.

El botón Reiniciar conserva el mismo Sudoku en la vista actual, elimina los movimientos y limpia el guardado al quedar sin progreso.

## Accesibilidad

El tablero debe procurar:

- selección visible;
- identificación de fila y columna;
- navegación con flechas;
- entrada con teclas 1–9;
- borrado con `Backspace` o `Delete`;
- foco visible;
- mensajes de estado accesibles;
- mensaje de restauración cuando se recupera una partida local;
- compatibilidad con ampliación de texto y alto contraste.

## Pruebas prioritarias

1. validez de las variantes generadas;
2. restauración exacta después de recargar;
3. rechazo de datos persistidos inválidos o corruptos;
4. funcionamiento cuando `localStorage` no está disponible;
5. navegación completa con teclado;
6. funcionamiento en móvil y escritorio;
7. consistencia entre casillas falladas y estado actual al comprobar;
8. protección contra pérdida accidental de progreso;
9. accesibilidad de mensajes de error, restauración y finalización.

## Crecimiento

Las funciones futuras podrán ampliarse en la web, en una futura aplicación móvil o en ambas. La prioridad sigue siendo consolidar claridad, accesibilidad y estabilidad antes de añadir complejidad.
