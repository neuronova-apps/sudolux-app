package com.neuronovaapps.sudolux.domain.progression

import com.neuronovaapps.sudolux.domain.premium.AccessTier
import com.neuronovaapps.sudolux.domain.sudoku.SudokuDifficulty
import com.neuronovaapps.sudolux.testutil.SudokuTestFixtures
import com.neuronovaapps.sudolux.ui.game.SudokuGameState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PremiumContinuationTest {
    @Test
    fun freeAttemptFinishesWithZeroXpAtThirdError() {
        val lost = afterThreeErrors(AccessTier.FREE)

        assertTrue(lost.attemptFinished)
        assertFalse(lost.premiumContinuationPending)
        assertEquals(0, lost.potentialXp)
    }

    @Test
    fun premiumOffersContinuationAndAcceptingAppliesThirtyPercentPenalty() {
        val pending = afterThreeErrors(AccessTier.PREMIUM)
        assertTrue(pending.premiumContinuationPending)
        assertFalse(pending.attemptFinished)

        val continued = pending.continueWithPremiumPenalty()
        assertTrue(continued.premiumContinuationUsed)
        assertFalse(continued.premiumContinuationPending)
        assertEquals(
            PremiumContinuationPolicy.applyInitialPenalty(pending.potentialXp, SudokuDifficulty.HARD),
            continued.potentialXp
        )
    }

    @Test
    fun finishingPremiumOfferDoesNotActivateRescue() {
        val finished = afterThreeErrors(AccessTier.PREMIUM).finishPremiumAttempt()

        assertTrue(finished.attemptFinished)
        assertFalse(finished.premiumContinuationUsed)
        assertEquals(0, finished.potentialXp)
    }

    @Test
    fun everyErrorAfterRescueAppliesTenPercentAndNeverDropsBelowMinimum() {
        var state = afterThreeErrors(AccessTier.PREMIUM).continueWithPremiumPenalty()
        val before = state.potentialXp
        state = state.enter(wrongNumber(state), AccessTier.PREMIUM)
        assertEquals(
            PremiumContinuationPolicy.applyAdditionalErrorPenalty(before, state.puzzle.difficulty),
            state.potentialXp
        )

        repeat(100) { state = state.enter(wrongNumber(state), AccessTier.PREMIUM) }
        assertEquals(XpCalculator.minimumReward(state.puzzle.difficulty), state.potentialXp)
        assertFalse(state.attemptFinished)
    }

    @Test
    fun premiumRescueCannotAwardLegendOrAbsoluteMastery() {
        val rescuedMaster = GamePerformance(
            difficulty = SudokuDifficulty.MASTER,
            mode = GameMode.NO_HINTS,
            hintsUsed = 0,
            errors = 3,
            completed = true,
            premiumContinuationUsed = true,
            premiumAdjustedXp = 175
        )
        val result = ProgressionCalculator.result(rescuedMaster)
        val update = ProgressionCalculator.applyResult(PlayerProgress(), "rescued-master", result)

        assertNotEquals(Medal.LEGEND, result.medal)
        assertFalse(result.isAbsoluteMastery)
        assertEquals(0, update.progress.absoluteMasteryCount)
        assertEquals(175, update.progress.totalXp)
    }

    @Test
    fun perfectMasterWithoutRescueStillAwardsSixHundredLegendAndMastery() {
        val result = ProgressionCalculator.result(
            GamePerformance(SudokuDifficulty.MASTER, GameMode.NO_HINTS, 0, 0, true)
        )
        val update = ProgressionCalculator.applyResult(PlayerProgress(), "perfect-master", result)

        assertEquals(600, result.xpEarned)
        assertEquals(Medal.LEGEND, result.medal)
        assertEquals(1, update.progress.absoluteMasteryCount)
    }

    private fun afterThreeErrors(tier: AccessTier): SudokuGameState {
        var state = baseGame()
        repeat(3) { state = state.enter(wrongNumber(state), tier) }
        return state
    }

    private fun baseGame(): SudokuGameState {
        val puzzle = SudokuTestFixtures.puzzle.copy(difficulty = SudokuDifficulty.HARD)
        return SudokuGameState(puzzle).select(puzzle.initialBoard.indexOfFirst { it == 0 })
    }

    private fun wrongNumber(state: SudokuGameState): Int {
        val index = requireNotNull(state.selectedCell)
        return (1..9).first { it != state.puzzle.solution[index] }
    }
}
