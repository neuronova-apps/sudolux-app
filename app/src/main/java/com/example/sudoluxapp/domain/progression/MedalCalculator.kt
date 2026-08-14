package com.example.sudoluxapp.domain.progression

import com.example.sudoluxapp.domain.sudoku.SudokuDifficulty

object MedalCalculator {
    fun calculate(performance: GamePerformance): Medal? {
        if (!performance.completed) return null
        if (performance.errors >= 3 && !performance.premiumContinuationUsed) return null
        if (performance.isAbsoluteMastery) return Medal.LEGEND

        val rank = performance.difficulty.provisionalComplexityRank
        val noHints = performance.mode == GameMode.NO_HINTS
        val fewHints = performance.hintsUsed <= 1
        val accurate = performance.errors <= 1

        if (performance.premiumContinuationUsed) {
            return when {
                rank >= SudokuDifficulty.MEDIUM.provisionalComplexityRank -> Medal.GOLD
                performance.hintsUsed <= 3 -> Medal.SILVER
                else -> Medal.BRONZE
            }
        }

        return when {
            rank >= SudokuDifficulty.EXPERT.provisionalComplexityRank &&
                accurate && (noHints || fewHints) -> Medal.DIAMOND

            rank >= SudokuDifficulty.HARD.provisionalComplexityRank &&
                performance.errors <= 2 && (noHints || performance.hintsUsed <= 2) -> Medal.PLATINUM

            rank >= SudokuDifficulty.MEDIUM.provisionalComplexityRank &&
                performance.errors <= 2 && (noHints || performance.hintsUsed <= 3) -> Medal.GOLD

            rank >= SudokuDifficulty.MEDIUM.provisionalComplexityRank ||
                performance.hintsUsed <= 3 -> Medal.SILVER

            else -> Medal.BRONZE
        }
    }
}
