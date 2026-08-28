package com.example.sudoluxapp.domain.progression

data class ProgressUpdate(
    val progress: PlayerProgress,
    val rewardApplied: Boolean,
    val previousLevel: Int,
    val newAbsoluteMasteryAchievement: Boolean,
    val reachedAbsoluteMasteryMilestones: List<Int>,
    val newlyUnlocked: List<Unlockable>
) {
    val leveledUp: Boolean get() = progress.currentLevel > previousLevel
}

object ProgressionCalculator {
    val absoluteMasteryMilestones = listOf(1, 5, 10, 25)

    fun result(performance: GamePerformance): GameResult = GameResult(
        performance = performance,
        xpEarned = XpCalculator.calculate(performance),
        medal = MedalCalculator.calculate(performance)
    )

    fun applyResult(
        current: PlayerProgress,
        gameId: String,
        result: GameResult,
        completedAtEpochMillis: Long = System.currentTimeMillis()
    ): ProgressUpdate {
        require(gameId.isNotBlank()) { "El identificador de partida no puede estar vacío." }
        val previousLevel = current.currentLevel
        if (gameId in current.processedGameIds) {
            return unchanged(current, previousLevel)
        }

        val isVictory = result.performance.completed &&
            (result.performance.errors < 3 || result.performance.premiumContinuationUsed)
        if (!isVictory) {
            return unchanged(
                current.copy(processedGameIds = current.processedGameIds + gameId),
                previousLevel
            )
        }

        val beforeUnlocked = (
            current.unlockedIds + UnlockableCatalog.unlocked(current).map { it.id }
        ).toSet()
        val updatedMedals = result.medal?.let { medal ->
            current.medalCounts + (medal to current.medalCount(medal) + 1)
        } ?: current.medalCounts
        val updatedBase = current.copy(
            totalXp = current.totalXp + result.xpEarned,
            medalCounts = updatedMedals,
            processedGameIds = current.processedGameIds + gameId,
            completedGameRecords = current.completedGameRecords + (
                gameId to CompletedGameRecord(
                    difficulty = result.performance.difficulty,
                    hintsUsed = result.performance.hintsUsed,
                    completedAtEpochMillis = completedAtEpochMillis,
                    xpEarned = result.xpEarned
                )
            )
        )
        val availableUnlocks = UnlockableCatalog.unlocked(updatedBase)
        val updated = updatedBase.copy(
            unlockedIds = beforeUnlocked + availableUnlocks.map { it.id }
        )
        val reachedMilestones = absoluteMasteryMilestones.filter { milestone ->
            current.absoluteMasteryCount < milestone && updated.absoluteMasteryCount >= milestone
        }
        return ProgressUpdate(
            progress = updated,
            rewardApplied = true,
            previousLevel = previousLevel,
            newAbsoluteMasteryAchievement =
                result.medal == Medal.LEGEND && current.legendMedalCount == 0,
            reachedAbsoluteMasteryMilestones = reachedMilestones,
            newlyUnlocked = availableUnlocks.filterNot { it.id in beforeUnlocked }
        )
    }

    private fun unchanged(current: PlayerProgress, previousLevel: Int) = ProgressUpdate(
        progress = current,
        rewardApplied = false,
        previousLevel = previousLevel,
        newAbsoluteMasteryAchievement = false,
        reachedAbsoluteMasteryMilestones = emptyList(),
        newlyUnlocked = emptyList()
    )
}
