# Desarrollo de Sudolux

## Estado actual

**Demo web funcional en desarrollo activo.**

La web ya integra contenido introductorio, reglas interactivas, técnicas básicas de resolución y una partida clásica 9 × 9 jugable. La partida en curso puede persistir localmente en el navegador e incluye valores definitivos y candidatos. La aplicación móvil permanece como una expansión futura y no es requisito para que la versión web tenga valor propio.

## Enfoque de desarrollo

Sudolux se desarrolla de forma progresiva, priorizando una base clásica de Sudoku estable antes de añadir funciones avanzadas.

## Estructura actual

La versión web se organiza en archivos con responsabilidades separadas:

- `index.html`: estructura semántica de la portada, contenido informativo, técnicas básicas, hero orbital y controles de la demo jugable;
- `styles.css`: estilos generales, secciones, tablero, candidatos y responsive;
- `techniques.css`: estilos exclusivos de la sección educativa de técnicas de resolución;
- `hero-orbit.css`: estilos exclusivos del sistema orbital del hero;
- `script.js`: navegación, demostración de reglas, lógica del Sudoku, candidatos y persistencia local segura;
- `privacy/index.html`: política pública de privacidad;
- `privacy/styles.css`: estilos exclusivos de la política de privacidad;
- `sitemap.xml`: URLs públicas indexables;
- `favicon.svg` y `favicon.ico`: identidad gráfica;
- `.nojekyll`: publicación estática directa mediante GitHub Pages.

## Capa educativa de técnicas

La portada incorpora una secuencia estática y accesible de cuatro pasos:

1. observar las restricciones de fila, columna y bloque;
2. anotar candidatos;
3. descartar posibilidades cuando cambia el tablero;
4. resolver una casilla cuando queda un único candidato posible.

La secuencia se resume como **observar → anotar → descartar → resolver** y enlaza directamente con la demo para practicar usando el modo Notas.

Esta capa no analiza automáticamente el estado del Sudoku ni calcula una pista. La futura funcionalidad de pistas comprensibles deberá ser una capa independiente que explique por qué una técnica aplica sobre la partida actual.

## Componentes actuales

La demo web funcional incluye:

- tablero 9 × 9 jugable;
- entrada mediante teclado y panel numérico;
- modo de respuestas definitivas;
- modo **Notas** para candidatos 1–9;
- alternancia de Notas mediante botón o tecla `N`;
- candidatos representados visualmente en una cuadrícula 3 × 3 dentro de la casilla;
- etiquetas accesibles que incluyen los candidatos de cada casilla;
- validación inmediata de respuestas definitivas;
- registro de casillas falladas por posición única;
- comprobación del estado actual del tablero sin alterar ese registro;
- persistencia local de una partida en curso;
- restauración de tablero, solución, valores, candidatos, casillas falladas, selección y modo Notas;
- validación defensiva de datos guardados;
- fallback en memoria cuando `localStorage` no está disponible;
- generación aleatoria de una variante válida cuando no existe progreso persistido;
- protección adicional con `beforeunload` cuando existe progreso;
- módulo central de accesibilidad de Neuronova Apps.

## Candidatos y modo Notas

Cada casilla editable mantiene un conjunto (`Set`) de candidatos del 1 al 9.

Cuando el modo Notas está activo, pulsar un número:

- lo añade si todavía no estaba presente;
- lo elimina si ya estaba presente;
- no compara el candidato con la solución;
- no incrementa **Casillas falladas**;
- no afecta el porcentaje completado.

El modo normal introduce un valor definitivo. Al hacerlo, los candidatos de esa casilla se eliminan. El botón **Borrar casilla** elimina el valor o los candidatos de la posición seleccionada.

Las notas se muestran como nueve posiciones pequeñas ordenadas dentro de la casilla. Para lectores de pantalla, el `aria-label` de la casilla enumera los candidatos presentes.

## Persistencia local

La partida activa utiliza la clave `sudolux-demo-v1` en `localStorage`. El objeto guardado utiliza actualmente `version: 2`, manteniendo compatibilidad con partidas previas que no incluían candidatos.

Se guardan únicamente datos necesarios para continuar la demo:

- tablero generado;
- solución correspondiente;
- valores definitivos;
- candidatos por casilla;
- índices de casillas falladas;
- casilla seleccionada;
- estado del modo Notas.

Antes de restaurar, el código verifica la solución completa, las pistas y los valores, filtra índices inválidos y normaliza los candidatos. Las notas solo se admiten en casillas editables sin valor definitivo.

Si los datos no son válidos, se ignoran y se genera una nueva variante. Si `localStorage` lanza un error o está bloqueado, Sudolux sigue funcionando en memoria.

El almacenamiento se mantiene solo mientras exista al menos un valor o candidato y la partida no esté completada. Reiniciar hasta dejar el tablero limpio o completar correctamente la partida elimina el guardado local.

## Modelo de validación y errores

La entrada de una respuesta definitiva se valida inmediatamente contra la solución. Los candidatos no se validan como respuestas.

La métrica visible **Casillas falladas** utiliza un conjunto de índices (`Set`) y representa posiciones distintas que han recibido al menos un intento definitivo incorrecto. Varias respuestas erróneas en la misma casilla siguen contando como una sola casilla fallada.

**Comprobar partida** evalúa los valores definitivos presentes. Una casilla que solo contiene candidatos sigue considerándose pendiente. El proceso no añade nuevas posiciones a Casillas falladas.

## Estado de partida

Mientras exista un valor o candidato y la partida no esté completada, la página utiliza `beforeunload` como protección adicional antes de recargar, cerrar o abandonar el sitio.

El botón Reiniciar conserva el mismo Sudoku en la vista actual, elimina valores y candidatos, limpia las casillas falladas, desactiva Notas y elimina el guardado al quedar sin progreso.

## Accesibilidad

El tablero procura:

- selección visible;
- identificación de fila y columna;
- navegación con flechas;
- entrada con teclas 1–9;
- alternancia de Notas con `N`;
- botón Notas con `aria-pressed`;
- candidatos incluidos en el nombre accesible de cada casilla;
- borrado con `Backspace` o `Delete`;
- foco visible;
- mensajes de estado accesibles;
- mensaje de restauración cuando se recupera una partida local;
- compatibilidad con ampliación de texto y alto contraste.

La sección educativa utiliza encabezados, artículos y ejemplos textuales; las representaciones visuales de candidatos llevan descripción accesible cuando aportan significado.

## Pruebas prioritarias

1. validez de las variantes generadas;
2. añadir y quitar candidatos con panel y teclado;
3. asegurar que candidatos no alteren errores ni progreso;
4. restauración exacta de candidatos y modo Notas después de recargar;
5. compatibilidad con partidas guardadas anteriores sin notas;
6. rechazo de datos persistidos inválidos o corruptos;
7. funcionamiento cuando `localStorage` no está disponible;
8. navegación completa con teclado;
9. funcionamiento en móvil y escritorio;
10. legibilidad de la sección de técnicas en diferentes tamaños y configuraciones de accesibilidad;
11. accesibilidad de candidatos, mensajes y estados.

## Crecimiento

Las funciones futuras podrán ampliarse en la web, en una futura aplicación móvil o en ambas. La prioridad sigue siendo consolidar claridad, accesibilidad y estabilidad antes de añadir complejidad.
