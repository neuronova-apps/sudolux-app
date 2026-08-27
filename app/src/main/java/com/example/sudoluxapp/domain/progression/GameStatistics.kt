package com.example.sudoluxapp.domain.progression

import com.example.sudoluxapp.domain.sudoku.SudokuDifficulty

enum class GameCompletionStatus {
    COMPLETED
}

/** Datos fiables disponibles para una finalización nueva, identificada por la clave del mapa. */
data class CompletedGameRecord(
    val difficulty: SudokuDifficulty,
    val hintsUsed: Int,
    val completedAtEpochMillis: Long,
    val xpEarned: Int,
    val status: GameCompletionStatus = GameCompletionStatus.COMPLETED
) {
    init {
        require(hintsUsed >= 0) { "Las pistas utilizadas no pueden ser negativas." }
        require(completedAtEpochMillis >= 0) { "La fecha de finalización no puede ser negativa." }
        require(xpEarned >= 0) { "El XP obtenido no puede ser negativo." }
    }

    val usedHints: Boolean get() = hintsUsed > 0
}

data class DifficultyCompletionStatistics(
    val withoutHints: Int = 0,
    val withHints: Int = 0
) {
    val total: Int get() = withoutHints + withHints
}

data class GameStatistics(
    val byDifficulty: Map<SudokuDifficulty, DifficultyCompletionStatistics>,
    val historicalUnclassified: Int,
    val totalCompleted: Int
) {
    val withoutHints: Int get() = byDifficulty.values.sumOf { it.withoutHints }
    val withHints: Int get() = byDifficulty.values.sumOf { it.withHints }
    val classifiedTotal: Int get() = withoutHints + withHints

    fun forDifficulty(difficulty: SudokuDifficulty): DifficultyCompletionStatistics =
        byDifficulty[difficulty] ?: DifficultyCompletionStatistics()

    companion object {
        fun from(
            records: Collection<CompletedGameRecord>,
            historicalTotal: Int
        ): GameStatistics {
            val completed = records.filter { it.status == GameCompletionStatus.COMPLETED }
            val byDifficulty = SudokuDifficulty.entries.associateWith { difficulty ->
                val matching = completed.filter { it.difficulty == difficulty }
                DifficultyCompletionStatistics(
                    withoutHints = matching.count { !it.usedHints },
                    withHints = matching.count { it.usedHints }
                )
            }
            val classifiedTotal = byDifficulty.values.sumOf { it.total }
            return GameStatistics(
                byDifficulty = byDifficulty,
                historicalUnclassified = (historicalTotal - classifiedTotal).coerceAtLeast(0),
                totalCompleted = maxOf(historicalTotal, classifiedTotal)
            )
        }
    }
}
