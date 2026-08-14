# Alcance de Sudolux

## Estado actual

**Demo web funcional en desarrollo activo.**

La versión web de Sudolux tiene un propósito introductorio, educativo y jugable. No es únicamente una página de exhibición: permite comprender las reglas y completar una partida clásica 9 × 9 de demostración.

## Alcance de la web

Incluye:

- explicación de qué es el Sudoku;
- estructura 9 × 9 y bloques 3 × 3;
- reglas de fila, columna y bloque;
- demostración visual de las reglas;
- información sobre razonamiento, atención y actividad mental;
- una partida clásica de demostración;
- generación de una variante válida cuando no existe una partida activa guardada;
- guardado local de una partida con progreso;
- restauración del tablero, movimientos, casillas falladas y selección tras recargar;
- validación de los datos persistidos antes de restaurarlos;
- funcionamiento en memoria cuando `localStorage` no está disponible;
- protección adicional ante salida o recarga cuando existe progreso;
- navegación mediante teclado;
- panel numérico;
- validación inmediata de cada número ingresado;
- métrica de casillas falladas por posición única;
- comprobación del estado actual del tablero sin modificar esa métrica histórica;
- reinicio del tablero actual;
- accesibilidad compartida con Neuronova Apps.

La métrica **Casillas falladas** cuenta cada posición una sola vez aunque se introduzcan varios números incorrectos en ella. Corregir o borrar la respuesta no elimina ese registro durante la partida. **Comprobar partida** muestra cuántas respuestas incorrectas siguen presentes en ese momento, pero no añade errores al historial.

La persistencia local se limita a la partida activa. No constituye una cuenta, sincronización entre dispositivos, historial de partidas ni estadísticas globales. Cuando el tablero queda sin progreso tras reiniciar o se completa correctamente, el guardado local se elimina.

## Límites actuales de la web

La demo web no incorpora todavía:

- selector de dificultad;
- notas o candidatos;
- pistas avanzadas;
- cronómetro competitivo;
- estadísticas persistentes entre partidas;
- historial de partidas;
- sincronización entre dispositivos;
- retos diarios;
- ranking;
- logros;
- múltiples modos de juego.

Estas funciones permanecen como ampliaciones futuras. Su incorporación podrá realizarse en la web, en una futura aplicación móvil o en ambas, según el alcance técnico y de producto.

## Alcance de una futura aplicación

La aplicación móvil se mantiene como una posible expansión para una experiencia más amplia y persistente. Podrá incorporar progresivamente:

- dificultades configurables;
- notas y candidatos;
- ayudas y pistas;
- gestión de múltiples partidas y sincronización;
- estadísticas;
- desafíos;
- XP y logros;
- ranking;
- personalización;
- modos adicionales.

Estas funciones no deben presentarse como disponibles hasta estar implementadas y verificadas.

## Alcance de bienestar cognitivo

Sudolux puede presentar el Sudoku como una actividad mentalmente estimulante y de entretenimiento activo. No debe afirmar que previene enfermedades, evita el deterioro cognitivo o sustituye recomendaciones profesionales de salud.

## Criterio de crecimiento

Las nuevas funciones deben mantener claridad, estabilidad, accesibilidad y una separación comprensible entre lo disponible actualmente y lo que sigue en desarrollo, sin restar valor a la demo web funcional.
