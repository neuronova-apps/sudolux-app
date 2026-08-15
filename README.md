# Sudolux

Sudolux es una aplicación de Neuronova Apps orientada a aprender las reglas del Sudoku, practicar técnicas básicas y resolver una partida clásica 9 × 9 mediante una experiencia web clara y accesible.

## Estado del proyecto

- **Web:** demo funcional en desarrollo activo.
- **Publicación:** disponible mediante GitHub Pages.
- **Android:** rama `android` separada en trabajo en progreso; no es una versión estable ni publicada.

## Alcance actual

Sudolux combina explicación y práctica de Sudoku en una experiencia web. Se presenta como entretenimiento y actividad mental. No constituye tratamiento, terapia ni método de prevención de deterioro cognitivo.

## Funciones disponibles

- explicación de la estructura 9 × 9 y bloques 3 × 3;
- reglas de fila, columna y bloque;
- técnicas básicas de restricciones, candidatos, descarte y único posible;
- cinco guías educativas públicas e indexables;
- partida clásica jugable;
- generación de una variante válida cuando no existe una partida activa;
- entrada mediante teclado o panel numérico;
- modo Notas para candidatos;
- validación de respuestas definitivas;
- persistencia local y restauración de la partida en curso;
- protección frente a salida accidental cuando existe progreso;
- navegación por teclado y capa ARIA específica para el tablero;
- diseño responsive e integración con la accesibilidad compartida de Neuronova Apps.

## Tecnología

La versión web utiliza HTML5, CSS3, JavaScript en el navegador, `localStorage`, GitHub Pages, recursos educativos HTML estáticos y el módulo central de accesibilidad de Neuronova Apps más una capa específica para el tablero. No requiere proceso de compilación.

## Accesibilidad

El tablero expone estructura de grid, filas y columnas, estados `aria-selected`, `aria-readonly` y `aria-invalid`, instrucciones asociadas y navegación mediante flechas. También conserva el foco después de usar el panel numérico y comunica los resultados mediante estados accesibles.

La superficie pública forma parte de la auditoría automática central del ecosistema. Estas medidas no equivalen a una certificación WCAG y continúan sujetas a revisión manual con tecnologías de asistencia.

## Privacidad

La partida y sus candidatos permanecen en el navegador mediante almacenamiento local. La versión actual no requiere cuenta ni sincronización remota.

Política pública: https://neuronova-apps.github.io/sudolux-app/privacy/

## Limitaciones conocidas

La versión actual se concentra en una experiencia clásica y todavía no ofrece una variedad amplia de modos, niveles o sistemas de progresión. El almacenamiento es local y no existe sincronización entre dispositivos. La revisión manual completa de accesibilidad continúa pendiente y la rama Android no es una aplicación publicada.

## Roadmap

Las líneas previstas son ampliar modos de juego y retos, desarrollar progresión, aumentar recursos educativos y completar validaciones manuales de accesibilidad del tablero y sus controles.

## Desarrollo local

```bash
git clone https://github.com/neuronova-apps/sudolux-app.git
cd sudolux-app
python3 -m http.server 8000
```

Después abre `http://localhost:8000`. La rama `main` corresponde a la web pública y `android` mantiene el desarrollo móvil separado.

## Estructura principal

- `index.html`: portada, contenido educativo y demo;
- `script.js`: lógica del Sudoku, candidatos y persistencia;
- `game-accessibility.js`: ARIA y gestión de foco del tablero;
- hojas CSS: sistema visual, hero, técnicas y recursos;
- páginas HTML educativas: cinco guías indexables;
- `privacy/`: política pública;
- `assets/social/`: tarjeta social;
- `sitemap.xml`: URLs públicas.

## Enlaces

- **Web:** https://neuronova-apps.github.io/sudolux-app/
- **Privacidad:** https://neuronova-apps.github.io/sudolux-app/privacy/
- **Repositorio:** https://github.com/neuronova-apps/sudolux-app
- **Ecosistema:** https://neuronova-apps.github.io/

## Neuronova Apps

Sudolux forma parte de Neuronova Apps y comparte con el resto de proyectos criterios comunes de identidad visual, accesibilidad, privacidad, SEO, documentación y publicación, conservando su repositorio independiente.

## Autoría

Proyecto personal e independiente desarrollado por Gabriel Berrospi dentro del ecosistema Neuronova Apps.

## Última revisión

2026-08-15
