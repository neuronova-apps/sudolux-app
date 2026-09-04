package com.neuronovaapps.sudolux.domain.progression

/**
 * Progreso global del jugador durante la sesión actual.
 *
 * Es un valor inmutable para que una futura fuente persistente pueda sustituir al estado en
 * memoria sin cambiar los cálculos ni las pantallas que lo consumen.
 */
data class PlayerProgress(
    val totalXp: Int = 0,
    val medalCounts: Map<Medal, Int> = Medal.entries.associateWith { 0 },
    val selectedBoardStyle: BoardStyle = BoardStyle.DEFAULT,
    val unlockedIds: Set<String> = emptySet(),
    val processedGameIds: Set<String> = emptySet(),
    val completedGameRecords: Map<String, CompletedGameRecord> = emptyMap()
) {
    val level: PlayerLevel get() = PlayerLevelCalculator.calculate(totalXp)
    val currentLevel: Int get() = level.level
    val currentTitle: String get() = level.title
    val currentProfileFrame: ProfileFrame get() = ProfileFrame.currentFor(currentLevel)
    val xpInCurrentLevel: Int get() = level.xpInLevel
    val xpRequiredForNextLevel: Int? get() = level.xpForNextLevel
    val progressToNextLevel: Float get() = level.progress
    val unlocked: List<Unlockable> get() = UnlockableCatalog.all.filter { it.id in unlockedIds }
    val legendMedalCount: Int get() = medalCount(Medal.LEGEND)
    val absoluteMasteryCount: Int get() = legendMedalCount
    /** Cada victoria válida concede exactamente una medalla, por lo que no se duplica el conteo. */
    val completedSudokus: Int get() = medalCounts.values.sum()
    val statistics: GameStatistics
        get() = GameStatistics.from(completedGameRecords.values, completedSudokus)

    fun medalCount(medal: Medal): Int = medalCounts[medal] ?: 0

    fun selectBoardStyle(boardStyle: BoardStyle): PlayerProgress =
        if (boardStyle.isUnlocked(this)) copy(selectedBoardStyle = boardStyle) else this
}
