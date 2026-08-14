# Hoja de ruta de Sudolux

## Etapa 1 — Web introductoria

**Estado: implementada.**

- identidad visual;
- teoría del Sudoku;
- reglas interactivas;
- técnicas básicas de resolución;
- tablero clásico de demostración;
- variantes aleatorias válidas;
- protección de partida en progreso;
- accesibilidad compartida.

## Etapa 2 — Base de juego

**Estado: en progreso.**

Ya implementado:

- mejora del modelo de errores mediante casillas falladas por posición única;
- comprobación separada del estado actual del tablero;
- guardado local de una partida con progreso;
- restauración segura del mismo tablero y movimientos después de recargar;
- fallback en memoria cuando el almacenamiento local no está disponible;
- notas/candidatos 1–9 por casilla;
- modo Notas alternable desde botón o teclado;
- persistencia y restauración de candidatos junto con la partida;
- guía educativa de restricciones, candidatos, descarte y único posible.

Pendiente:

- mejoras adicionales de selección y estados;
- pistas comprensibles aplicadas al tablero actual;
- control de validación configurable.

## Etapa 3 — Experiencia ampliada

- dificultades configurables;
- múltiples partidas;
- cronómetro opcional;
- estadísticas;
- historial;
- gestión de partidas guardadas;
- sincronización opcional cuando exista una necesidad real;
- experiencia móvil optimizada si se desarrolla una aplicación.

## Etapa 4 — Progreso y retos

- XP;
- logros;
- desafíos diarios;
- rachas;
- ranking;
- objetivos personales.

## Etapa 5 — Expansión

- modos adicionales;
- variantes de Sudoku;
- personalización;
- mejoras basadas en datos de uso y pruebas de accesibilidad.

## Prioridad

La estabilidad del generador, la claridad de interacción y la calidad de futuras pistas aplicadas a la partida tienen prioridad sobre aumentar rápidamente la cantidad de modos.
