package com.neuronovaapps.sudolux.domain.progression

import com.neuronovaapps.sudolux.domain.sudoku.SudokuDifficulty

object MedalCalculator {
    fun calculate(performance: GamePerformance): Medal? {
        if (!performance.completed) return null
        if (performance.errors >= 3 && !performance.premiumContinuationUsed) return null
        if (performance.isAbsoluteMastery) return Medal.LEGEND

        return when (performance.difficulty) {
            SudokuDifficulty.EASY -> Medal.BRONZE
            SudokuDifficulty.MEDIUM -> Medal.SILVER
            SudokuDifficulty.HARD -> Medal.GOLD
            SudokuDifficulty.EXPERT -> Medal.PLATINUM
            SudokuDifficulty.MASTER -> Medal.DIAMOND
        }
    }
}
