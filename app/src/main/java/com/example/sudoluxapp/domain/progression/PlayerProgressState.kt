package com.example.sudoluxapp.domain.progression

/**
 * Progreso global del jugador durante la sesión actual.
 *
 * Es un valor inmutable para que una futura fuente persistente pueda sustituir al estado en
 * memoria sin cambiar los cálculos ni las pantallas que lo consumen.
 */
data class PlayerProgress(
    val totalXp: Int = 0,
    val medalCounts: Map<Medal, Int> = Medal.entries.associateWith { 0 },
    val absoluteMasteryCount: Int = 0,
    val unlockedIds: Set<String> = emptySet(),
    val processedGameIds: Set<String> = emptySet()
) {
    val level: PlayerLevel get() = PlayerLevelCalculator.calculate(totalXp)
    val currentLevel: Int get() = level.level
    val currentTitle: String get() = level.title
    val xpInCurrentLevel: Int get() = level.xpInLevel
    val xpRequiredForNextLevel: Int? get() = level.xpForNextLevel
    val progressToNextLevel: Float get() = level.progress
    val unlocked: List<Unlockable> get() = UnlockableCatalog.all.filter { it.id in unlockedIds }
    val legendMedalCount: Int get() = medalCount(Medal.LEGEND)
    /** Cada victoria válida concede exactamente una medalla, por lo que no se duplica el conteo. */
    val completedSudokus: Int get() = medalCounts.values.sum()

    fun medalCount(medal: Medal): Int = medalCounts[medal] ?: 0
}
