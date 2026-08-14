# Sudolux

Sudolux es una iniciativa personal orientada al desarrollo de una experiencia digital de Sudoku clara, accesible y progresiva.

El proyecto forma parte del ecosistema [Neuronova Apps](https://neuronova-apps.github.io/) y se desarrolla de manera independiente por Gabriel Berrospi.

## Propósito

Sudolux busca combinar tres componentes: conocer la lógica del Sudoku, comprender sus reglas y ofrecer una experiencia de juego sencilla. La web funciona actualmente como una **demo web funcional en desarrollo activo**: integra contenido introductorio, reglas interactivas, técnicas básicas de resolución, cinco guías educativas públicas y una partida clásica 9 × 9 jugable. La aplicación móvil permanece como una expansión futura para funciones de mayor amplitud.

## Qué es el Sudoku

El Sudoku clásico es un rompecabezas lógico formado por una cuadrícula de 9 × 9, dividida en nueve bloques de 3 × 3.

El objetivo consiste en completar las casillas utilizando los números del 1 al 9 de manera que:

- cada fila contenga los números del 1 al 9 sin repetir;
- cada columna contenga los números del 1 al 9 sin repetir;
- cada bloque de 3 × 3 contenga los números del 1 al 9 sin repetir.

No requiere realizar operaciones matemáticas. Los números funcionan como símbolos y la resolución depende principalmente de la deducción y el descarte de posibilidades.

## Técnicas básicas de resolución

La web incorpora una secuencia introductoria para pasar de las reglas a una decisión concreta:

1. **Observar restricciones:** revisar fila, columna y bloque para detectar números que ya no pueden ocupar una casilla.
2. **Anotar candidatos:** registrar las posibilidades restantes mediante el modo **Notas**.
3. **Descartar posibilidades:** actualizar los candidatos cuando aparecen nuevos valores relacionados.
4. **Buscar un único posible:** colocar un valor definitivo cuando una casilla queda con una sola opción válida.

La secuencia recomendada es **observar → anotar → descartar → resolver**. Estas explicaciones son contenido educativo introductorio; Sudolux todavía no detecta ni propone automáticamente la siguiente técnica o movimiento.

## Guías educativas indexables

La portada enlaza cinco recursos públicos independientes que amplían el contenido educativo y conducen de nuevo a la práctica:

- `como-jugar-sudoku.html`: objetivo, primeros pasos, candidatos y uso de la demo;
- `reglas-sudoku.html`: fila, columna y bloque explicados como restricciones simultáneas;
- `sudoku-para-principiantes.html`: rutina inicial, uso de notas y único posible;
- `tecnicas-basicas-sudoku.html`: restricciones, candidatos, descarte y único posible con mayor detalle;
- `sudoku-y-actividad-mental.html`: atención, razonamiento y límites de las afirmaciones sobre efectos cognitivos.

Cada página tiene título, descripción, canonical, `index, follow`, metadatos Open Graph/Twitter, navegación interna, accesibilidad compartida y enlaces de vuelta a la demo. El sitemap incluye portada, las cinco guías y privacidad.

Estas guías son contenido educativo estático. No analizan automáticamente la partida actual ni sustituyen futuras pistas explicadas sobre el tablero.

## Imagen social y metadatos de compartición

Sudolux utiliza una tarjeta social propia en `assets/social/sudolux-social.png`, preparada a **1200 × 630 px** para compartir la portada y las cinco guías educativas.

La portada y las guías utilizan:

- `og:image` apuntando al PNG dedicado;
- `og:image:type="image/png"`;
- `og:image:width="1200"` y `og:image:height="630"`;
- texto alternativo común para la imagen social;
- `twitter:card="summary_large_image"`;
- la misma imagen en `twitter:image`;
- títulos y descripciones sociales específicos de cada página.

El favicon SVG se conserva únicamente como icono del sitio y ya no se utiliza como imagen social en la portada ni en las guías educativas.

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
- guía de técnicas básicas: restricciones, candidatos, descarte y único posible;
- cinco páginas educativas indexables enlazadas desde la portada;
- tarjeta social dedicada 1200 × 630 para portada y guías;
- información sobre razonamiento, atención y actividad mental;
- una partida clásica de demostración;
- generación aleatoria de una variante válida cuando no existe una partida activa guardada;
- guardado local de la partida en curso cuando existen valores o candidatos;
- restauración del mismo tablero, movimientos, candidatos, casillas falladas, selección y modo de entrada después de recargar;
- validación de los datos guardados antes de restaurarlos;
- funcionamiento en memoria si `localStorage` no está disponible;
- protección adicional ante recarga, cierre o salida accidental cuando existe una partida en progreso;
- navegación por teclado dentro del tablero;
- ingreso mediante teclado o panel numérico;
- modo **Notas** para añadir o quitar candidatos del 1 al 9;
- tecla `N` para alternar entre valor definitivo y candidatos;
- validación inmediata únicamente de respuestas definitivas;
- registro de casillas falladas sin duplicar intentos repetidos sobre la misma posición;
- comprobación del estado actual del tablero sin alterar ese registro;
- reinicio de la partida actual;
- integración con el módulo de accesibilidad compartido de Neuronova Apps;
- capa específica de accesibilidad para el tablero y el flujo de foco.

## Modo de juego web

La web mantiene una partida clásica activa. El modo normal introduce respuestas definitivas y el modo **Notas** permite registrar candidatos en casillas editables vacías. Cada candidato funciona como interruptor: pulsar un número lo añade y pulsarlo nuevamente lo elimina.

Las notas no se comparan con la solución, no aumentan **Casillas falladas** y no cuentan para el porcentaje completado. Al colocar un valor definitivo en una casilla, sus candidatos se eliminan. **Borrar casilla** elimina el contenido de la posición seleccionada, sea un valor o un conjunto de candidatos.

El modo Notas puede alternarse con el botón correspondiente o con la tecla `N`. Las teclas 1–9 y el panel numérico respetan el modo activo.

Cuando existe al menos un valor o candidato, Sudolux guarda localmente en el navegador el tablero generado, su solución, los valores, candidatos, registro de casillas falladas, casilla seleccionada y modo de entrada. Al recargar, esos datos se validan y, si son coherentes, se restaura la misma partida.

El almacenamiento utiliza la clave `sudolux-demo-v1` y permanece únicamente en el navegador. La estructura persistida está versionada internamente y mantiene compatibilidad con partidas guardadas antes de incorporar candidatos. No requiere cuenta, backend ni sincronización externa. Si `localStorage` no puede utilizarse, la demo continúa funcionando durante la sesión sin persistencia.

Un tablero recién generado y sin valores ni candidatos no se conserva de forma permanente. Si la partida se reinicia hasta quedar sin progreso o se completa correctamente, el guardado local se elimina. De este modo, una recarga posterior puede generar otra variante válida.

Mientras exista progreso sin completar, el navegador mantiene además la confirmación `beforeunload` como protección adicional frente a una salida accidental.

La métrica **Casillas falladas** representa cuántas casillas distintas han recibido al menos una respuesta definitiva incorrecta durante la partida. Una misma casilla solo cuenta una vez aunque se prueben varios números erróneos. Si después se corrige o se borra, permanece en ese registro histórico hasta reiniciar la demo.

El botón **Comprobar partida** evalúa únicamente los valores definitivos del tablero. Las notas se consideran casillas aún por completar y no suman elementos al registro de casillas falladas.

El botón **Reiniciar** no genera inmediatamente un Sudoku nuevo: restaura el mismo tablero, borra valores y candidatos, devuelve el registro de casillas falladas a cero, desactiva el modo Notas y elimina la partida persistida al quedar sin progreso. Si ya existe progreso, solicita confirmación antes de borrar lo realizado.

No incluye por ahora:

- selector de dificultad;
- pistas automáticas o detección de técnicas sobre el tablero actual;
- cronómetro competitivo;
- estadísticas persistentes entre partidas;
- historial de partidas;
- sincronización entre dispositivos;
- desafíos diarios;
- ranking;
- logros;
- distintos modos de juego.

Estas funciones permanecen como ampliaciones futuras del proyecto y podrán distribuirse entre la web y una futura aplicación móvil según su alcance técnico y de producto.

## Accesibilidad

Sudolux utiliza el núcleo central de accesibilidad de Neuronova Apps. Entre sus opciones se encuentran tamaño de texto en tres niveles, alto contraste, espaciado de letras y palabras, interlineado amplio, lectura amigable para dislexia, guía de lectura, reducción de movimiento y foco de teclado reforzado.

El tablero de demostración contempla navegación mediante teclas de dirección, ingreso con teclas numéricas, alternancia del modo Notas con `N` y borrado mediante `Backspace` o `Delete`. Las etiquetas accesibles de cada casilla incluyen sus candidatos cuando existen.

La capa `game-accessibility.js` refuerza específicamente la experiencia del Sudoku:

- declara el grid como una estructura de 9 filas y 9 columnas;
- expone `aria-rowindex` y `aria-colindex` en cada casilla;
- sincroniza `aria-selected`, `aria-readonly` y `aria-invalid` con el estado visual;
- proporciona instrucciones de teclado asociadas al tablero mediante `aria-describedby`;
- devuelve el foco al botón del menú cuando se cierra con `Escape`;
- después de usar el panel numérico o borrar, devuelve el foco a la casilla activa;
- después de comprobar la partida, lleva el foco al mensaje de estado para comunicar el resultado;
- al reiniciar, conserva el comportamiento de foco del control para no interferir con una posible cancelación de la confirmación.

Las guías educativas emplean encabezados semánticos, navegación identificada, enlaces de salto, foco visible y el módulo de accesibilidad compartido.

Estas mejoras no se presentan como certificación WCAG ni como sustituto de una auditoría formal de accesibilidad.

## Arquitectura visual y de interacción

- `styles.css`: estilos globales, tablero y componentes principales;
- `techniques.css`: estilos exclusivos de la sección educativa de técnicas;
- `guide-cards.css`: tarjetas de acceso a las guías desde la portada;
- `resources.css`: presentación compartida por las cinco páginas educativas;
- `assets/social/sudolux-social.png`: tarjeta social 1200 × 630 para Open Graph y Twitter;
- `hero-orbit.css`: presentación orbital del hero;
- `script.js`: lógica de juego, candidatos y persistencia;
- `game-accessibility.js`: semántica ARIA y gestión complementaria del foco del tablero;
- las cinco páginas `.html` de guías: contenido educativo indexable sin lógica de juego duplicada.

La separación evita mezclar el estado del juego con las capas de contenido, presentación y accesibilidad, y facilita revisar cada responsabilidad de manera independiente.

## Alcance de la aplicación

La aplicación móvil se mantiene como una expansión futura de Sudolux y podrá incorporar progresivamente funciones que requieran una experiencia más amplia o persistente, como:

- dificultades configurables;
- ayudas y pistas;
- estadísticas de juego;
- gestión de múltiples partidas y sincronización;
- desafíos diarios;
- sistema de XP y logros;
- ranking;
- diferentes modos de juego;
- opciones adicionales de personalización.

Estas funciones no se consideran disponibles hasta estar implementadas y verificadas.

## Desarrollo

Sudolux se encuentra en desarrollo activo. La web es actualmente una **demo web funcional** que combina aprendizaje introductorio, guías públicas, técnicas básicas, una partida clásica jugable con candidatos y persistencia local de la partida en curso. La aplicación móvil continúa en la hoja de ruta como expansión futura, no como requisito para que la versión web tenga valor propio.

## Ecosistema

Sudolux forma parte de [Neuronova Apps](https://neuronova-apps.github.io/), plataforma matriz que reúne distintos proyectos digitales desarrollados de manera independiente.

## Autoría

Proyecto personal desarrollado por Gabriel Berrospi.

## Estado

**Demo web funcional en desarrollo activo.**
