package com.example.sudoluxapp.domain.progression

import com.example.sudoluxapp.domain.sudoku.SudokuDifficulty

object XpCalculator {
    private val baseXp = mapOf(
        SudokuDifficulty.EASY to 50,
        SudokuDifficulty.MEDIUM to 80,
        SudokuDifficulty.HARD to 120,
        SudokuDifficulty.EXPERT to 180,
        SudokuDifficulty.MASTER to 250
    )

    private val perfectNoHintsXp = mapOf(
        SudokuDifficulty.EASY to 75,
        SudokuDifficulty.MEDIUM to 130,
        SudokuDifficulty.HARD to 210,
        SudokuDifficulty.EXPERT to 360,
        SudokuDifficulty.MASTER to 600
    )

    fun baseXp(difficulty: SudokuDifficulty): Int = baseXp.getValue(difficulty)

    fun includedHintPercentage(hintsUsed: Int): Int = when (hintsUsed.coerceAtLeast(0)) {
        0 -> 100
        1 -> 90
        2 -> 75
        else -> 60
    }

    fun extraHintCost(hintNumber: Int): Int = when (hintNumber) {
        4 -> 15
        5 -> 20
        6 -> 25
        in 7..Int.MAX_VALUE -> 30
        else -> 0
    }

    fun minimumReward(difficulty: SudokuDifficulty): Int =
        (baseXp(difficulty) * MINIMUM_REWARD_PERCENT) / 100

    fun potentialXp(
        difficulty: SudokuDifficulty,
        mode: GameMode,
        hintsUsed: Int,
        errors: Int
    ): Int = calculate(
        GamePerformance(
            difficulty = difficulty,
            mode = mode,
            hintsUsed = hintsUsed,
            errors = errors,
            completed = errors < MAX_ERRORS
        )
    )

    fun calculate(performance: GamePerformance): Int {
        if (!performance.completed) return 0
        if (performance.premiumContinuationUsed) {
            return requireNotNull(performance.premiumAdjustedXp) {
                "Una continuación Premium requiere el XP ajustado por el dominio."
            }.coerceAtLeast(minimumReward(performance.difficulty))
        }
        if (performance.errors >= MAX_ERRORS) return 0

        if (performance.mode == GameMode.NO_HINTS && performance.errors == 0) {
            return perfectNoHintsXp.getValue(performance.difficulty)
        }

        val beforeErrors = when (performance.mode) {
            GameMode.NO_HINTS -> baseXp(performance.difficulty) * NO_HINTS_PERCENT / 100
            GameMode.WITH_HINTS -> xpAfterIncludedHints(performance.difficulty, performance.hintsUsed)
        }
        val accuracyAdjusted = when (performance.errors) {
            0 -> beforeErrors * PERFECT_ACCURACY_PERCENT / 100
            1 -> beforeErrors
            2 -> beforeErrors * TWO_ERROR_PERCENT / 100
            else -> 0
        }
        val adjusted = accuracyAdjusted - extraHintsTotalCost(performance.hintsUsed)
        return adjusted.coerceAtLeast(minimumReward(performance.difficulty))
    }

    fun extraHintConfirmation(
        difficulty: SudokuDifficulty,
        hintsUsed: Int,
        errors: Int
    ): ExtraHintConfirmation {
        require(hintsUsed >= INCLUDED_HINTS) { "La confirmación solo corresponde a pistas extra." }
        val currentXp = potentialXp(difficulty, GameMode.WITH_HINTS, hintsUsed, errors)
        val hintNumber = hintsUsed + 1
        val nextXp = potentialXp(difficulty, GameMode.WITH_HINTS, hintNumber, errors)
        return ExtraHintConfirmation(
            hintNumber = hintNumber,
            currentXp = currentXp,
            nominalCost = extraHintCost(hintNumber),
            xpAfterHint = nextXp
        )
    }

    private fun xpAfterIncludedHints(difficulty: SudokuDifficulty, hintsUsed: Int): Int =
        baseXp(difficulty) * includedHintPercentage(hintsUsed) / 100

    private fun extraHintsTotalCost(hintsUsed: Int): Int =
        if (hintsUsed <= INCLUDED_HINTS) 0
        else (INCLUDED_HINTS + 1..hintsUsed).sumOf(::extraHintCost)

    private const val INCLUDED_HINTS = 3
    private const val MAX_ERRORS = 3
    private const val MINIMUM_REWARD_PERCENT = 10
    private const val PERFECT_ACCURACY_PERCENT = 115
    private const val TWO_ERROR_PERCENT = 90
    private const val NO_HINTS_PERCENT = 125
}

/** Reglas de rescate Premium. La UI nunca calcula ni porcentajes ni mínimos. */
object PremiumContinuationPolicy {
    const val INITIAL_PENALTY_PERCENT = 30
    const val ADDITIONAL_ERROR_PENALTY_PERCENT = 10

    fun applyInitialPenalty(currentXp: Int, difficulty: SudokuDifficulty): Int =
        reduced(currentXp, INITIAL_PENALTY_PERCENT, difficulty)

    fun applyAdditionalErrorPenalty(currentXp: Int, difficulty: SudokuDifficulty): Int =
        reduced(currentXp, ADDITIONAL_ERROR_PENALTY_PERCENT, difficulty)

    private fun reduced(currentXp: Int, percent: Int, difficulty: SudokuDifficulty): Int {
        val retainedPercent = 100 - percent
        val rounded = (currentXp * retainedPercent + 50) / 100
        return rounded.coerceAtLeast(XpCalculator.minimumReward(difficulty))
    }
}
