# Migración de estadísticas de partidas

Sudolux mantiene las medallas persistidas como fuente histórica de `completedSudokus`. La nueva
clave `progress.completed_games` se añade dentro de `sudolux_persistent_state_v1` y solo registra
finalizaciones nuevas para las que existen datos fiables de dificultad, pistas, fecha y XP.

Si una instalación existente tiene medallas pero no registros detallados, ese total se conserva y
se presenta como histórico sin clasificar. No se asigna artificialmente a una dificultad ni a
“Con pistas” o “Sin pistas”. Cada registro nuevo usa el `gameId` persistente de la sesión; el mismo
identificador continúa protegido por `progress.processed_games`, de modo que una reapertura o una
doble acción no vuelven a conceder XP, medalla, victoria ni estadística.
