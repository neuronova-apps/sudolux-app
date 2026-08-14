# Sudolux

Sudolux es una iniciativa personal orientada al desarrollo de una experiencia digital de Sudoku clara, accesible y progresiva.

El proyecto forma parte del ecosistema [Neuronova Apps](https://neuronova-apps.github.io/) y se desarrolla de manera independiente por Gabriel Berrospi.

## Propósito

Sudolux busca combinar tres componentes: conocer la lógica del Sudoku, comprender sus reglas y ofrecer una experiencia de juego sencilla. La web funciona actualmente como una **demo web funcional en desarrollo activo**: integra contenido introductorio, reglas interactivas y una partida clásica 9 × 9 jugable. La aplicación móvil permanece como una expansión futura para funciones de mayor amplitud.

## Qué es el Sudoku

El Sudoku clásico es un rompecabezas lógico formado por una cuadrícula de 9 × 9, dividida en nueve bloques de 3 × 3.

El objetivo consiste en completar las casillas utilizando los números del 1 al 9 de manera que:

- cada fila contenga los números del 1 al 9 sin repetir;
- cada columna contenga los números del 1 al 9 sin repetir;
- cada bloque de 3 × 3 contenga los números del 1 al 9 sin repetir.

No requiere realizar operaciones matemáticas. Los números funcionan como símbolos y la resolución depende principalmente de la deducción y el descarte de posibilidades.

## Actividad mental

Resolver Sudoku implica sostener la atención, comparar alternativas, recordar información temporalmente y aplicar reglas de manera ordenada. Por ello puede formar parte de una rutina de actividades mentalmente estimulantes y de entretenimiento activo.

Existe investigación observacional que ha encontrado asociaciones entre una mayor frecuencia de uso de rompecabezas numéricos y un mejor rendimiento en distintas medidas cognitivas en adultos mayores. Sin embargo, estas asociaciones no demuestran que el Sudoku, por sí solo, prevenga el deterioro cognitivo o enfermedades como la demencia.

El National Institute on Aging señala que mantenerse mentalmente activo y participar en actividades significativas puede ser beneficioso, pero también advierte que la evidencia sobre beneficios cognitivos duraderos de juegos o aplicaciones concretas no es definitiva.

Sudolux presenta el Sudoku como entretenimiento y estimulación mental, no como tratamiento, terapia ni método de prevención.

## Componentes de la web

La demo web funcional incorpora:

- explicación de la estructura del Sudoku clásico;
- reglas de fila, columna y bloque;
- demostración visual interactiva de las tres reglas;
- información sobre razonamiento, atención y actividad mental;
- una partida clásica de demostración;
- generación aleatoria de una variante válida del Sudoku en cada nueva carga de la página;
- protección ante recarga, cierre o salida accidental cuando existe una partida en progreso;
- navegación por teclado dentro del tablero;
- ingreso mediante teclado o panel numérico;
- validación inmediata de movimientos;
- registro de casillas falladas sin duplicar intentos repetidos sobre la misma posición;
- comprobación del estado actual del tablero sin alterar ese registro;
- reinicio de la partida actual;
- integración con el módulo de accesibilidad compartido de Neuronova Apps.

## Modo de juego web

El juego web está limitado intencionalmente a una partida clásica de demostración por sesión.

Cada nueva carga genera una variante diferente mediante transformaciones que conservan una estructura de Sudoku válida. Mientras el usuario tenga movimientos realizados y la partida no esté completada, el navegador solicita confirmación antes de recargar, cerrar o abandonar la página para reducir la pérdida accidental del progreso.

La métrica **Casillas falladas** representa cuántas casillas distintas han recibido al menos una respuesta incorrecta durante la partida. Una misma casilla solo cuenta una vez aunque se prueben varios números erróneos. Si después se corrige o se borra, permanece en ese registro histórico hasta reiniciar la demo.

El botón **Comprobar partida** evalúa el estado actual del tablero y resalta las casillas incorrectas que sigan presentes, pero no suma elementos al registro de casillas falladas. De este modo se separan el historial de dificultad de la partida y el estado actual del tablero.

El botón **Reiniciar** no genera un Sudoku nuevo: restaura el mismo tablero de la sesión, borra los movimientos y devuelve el registro de casillas falladas a cero. Si ya existe progreso, solicita confirmación antes de borrar los movimientos realizados.

No incluye por ahora:

- selector de dificultad;
- notas o candidatos;
- pistas automáticas;
- cronómetro competitivo;
- estadísticas persistentes;
- desafíos diarios;
- ranking;
- logros;
- distintos modos de juego.

Estas funciones permanecen como ampliaciones futuras del proyecto y podrán distribuirse entre la web y una futura aplicación móvil según su alcance técnico y de producto.

## Accesibilidad

Sudolux utiliza el núcleo central de accesibilidad de Neuronova Apps. Entre sus opciones se encuentran tamaño de texto en tres niveles, alto contraste, espaciado de letras y palabras, interlineado amplio, lectura amigable para dislexia, guía de lectura, reducción de movimiento y foco de teclado reforzado.

El tablero de demostración también contempla navegación mediante teclas de dirección, ingreso con teclas numéricas y borrado mediante `Backspace` o `Delete`.

## Alcance de la aplicación

La aplicación móvil se mantiene como una expansión futura de Sudolux y podrá incorporar progresivamente funciones que requieran una experiencia más amplia o persistente, como:

- dificultades configurables;
- notas y candidatos;
- ayudas y pistas;
- estadísticas de juego;
- partidas guardadas;
- desafíos diarios;
- sistema de XP y logros;
- ranking;
- diferentes modos de juego;
- opciones adicionales de personalización.

Estas funciones no se consideran disponibles hasta estar implementadas y verificadas.

## Desarrollo

Sudolux se encuentra en desarrollo activo. La web es actualmente una **demo web funcional** que combina presentación, contenido informativo y una partida clásica jugable. La aplicación móvil continúa en la hoja de ruta como expansión futura, no como requisito para que la versión web tenga valor propio.

## Ecosistema

Sudolux forma parte de [Neuronova Apps](https://neuronova-apps.github.io/), plataforma matriz que reúne distintos proyectos digitales desarrollados de manera independiente.

## Autoría

Proyecto personal desarrollado por Gabriel Berrospi.

## Estado

**Demo web funcional en desarrollo activo.**
