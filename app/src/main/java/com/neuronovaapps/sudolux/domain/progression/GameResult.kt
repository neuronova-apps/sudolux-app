package com.neuronovaapps.sudolux.domain.progression

import com.neuronovaapps.sudolux.domain.sudoku.SudokuDifficulty

enum class GameMode(val displayName: String, val description: String) {
    WITH_HINTS(
        displayName = "Con pistas",
        description = "Hasta 3 pistas incluidas. Usar pistas reduce progresivamente el XP obtenido."
    ),
    NO_HINTS(
        displayName = "Sin pistas",
        description = "Las pistas estarán desactivadas durante toda la partida. Obtendrás mayor XP si completas el Sudoku."
    )
}

enum class Medal(val displayName: String) {
    BRONZE("Bronce"),
    SILVER("Plata"),
    GOLD("Oro"),
    PLATINUM("Platino"),
    DIAMOND("Diamante"),
    LEGEND("Leyenda")
}

data class GamePerformance(
    val difficulty: SudokuDifficulty,
    val mode: GameMode,
    val hintsUsed: Int,
    val errors: Int,
    val completed: Boolean,
    val premiumContinuationUsed: Boolean = false,
    val premiumAdjustedXp: Int? = null
) {
    init {
        require(hintsUsed >= 0) { "Las pistas utilizadas no pueden ser negativas." }
        require(errors >= 0) { "Los errores no pueden ser negativos." }
        require(mode == GameMode.WITH_HINTS || hintsUsed == 0) {
            "Una partida sin pistas no puede registrar pistas utilizadas."
        }
        require(premiumAdjustedXp == null || premiumAdjustedXp >= 0) {
            "El XP ajustado no puede ser negativo."
        }
        require(premiumContinuationUsed || premiumAdjustedXp == null) {
            "El XP ajustado solo existe después de una continuación Premium."
        }
    }

    val isAbsoluteMastery: Boolean
        get() = completed &&
            difficulty == SudokuDifficulty.MASTER &&
            mode == GameMode.NO_HINTS &&
            errors == 0 &&
            !premiumContinuationUsed
}

data class GameResult(
    val performance: GamePerformance,
    val xpEarned: Int,
    val medal: Medal?
) {
    val isAbsoluteMastery: Boolean get() = performance.isAbsoluteMastery
}

data class ExtraHintConfirmation(
    val hintNumber: Int,
    val currentXp: Int,
    val nominalCost: Int,
    val xpAfterHint: Int
) {
    val actualXpDiscount: Int get() = currentXp - xpAfterHint
}
