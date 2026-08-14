# Alcance de Sudolux

## Estado actual

**Demo web funcional en desarrollo activo.**

La versión web de Sudolux tiene un propósito introductorio, educativo y jugable. Permite comprender las reglas y completar una partida clásica 9 × 9 de demostración con valores definitivos, candidatos y persistencia local.

## Alcance de la web

Incluye:

- explicación de qué es el Sudoku;
- estructura 9 × 9 y bloques 3 × 3;
- reglas de fila, columna y bloque;
- demostración visual de las reglas;
- información sobre razonamiento, atención y actividad mental;
- una partida clásica de demostración;
- generación de una variante válida cuando no existe una partida activa guardada;
- guardado local de una partida con valores o candidatos;
- restauración del tablero, movimientos, candidatos, casillas falladas, selección y modo Notas tras recargar;
- validación de los datos persistidos antes de restaurarlos;
- funcionamiento en memoria cuando `localStorage` no está disponible;
- protección adicional ante salida o recarga cuando existe progreso;
- navegación mediante teclado;
- panel numérico;
- modo **Notas** para candidatos 1–9;
- tecla `N` para alternar el modo de entrada;
- validación inmediata de respuestas definitivas;
- métrica de casillas falladas por posición única;
- comprobación del estado actual del tablero sin modificar esa métrica histórica;
- reinicio del tablero actual;
- accesibilidad compartida con Neuronova Apps.

Los candidatos son ayudas de trabajo de la persona usuaria. No se validan contra la solución, no cuentan como respuestas, no modifican el porcentaje completado y no incrementan **Casillas falladas**.

La persistencia local se limita a la partida activa. No constituye una cuenta, sincronización entre dispositivos, historial de partidas ni estadísticas globales. Cuando el tablero queda sin valores ni candidatos tras reiniciar o se completa correctamente, el guardado local se elimina.

## Límites actuales de la web

La demo web no incorpora todavía:

- selector de dificultad;
- pistas automáticas;
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
