package com.example.sudoluxapp.ui.progress

import com.example.sudoluxapp.domain.progression.Medal
import com.example.sudoluxapp.domain.progression.PlayerLevelCalculator
import com.example.sudoluxapp.domain.progression.PlayerProgress
import com.example.sudoluxapp.domain.progression.CompletedGameRecord
import com.example.sudoluxapp.domain.sudoku.SudokuDifficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressScreenPresenterTest {
    @Test
    fun newPlayerShowsLevelOneAndZeroXp() {
        val state = ProgressScreenPresenter.present(PlayerProgress())

        assertEquals(1, state.level.level)
        assertEquals("Novato", state.level.title)
        assertEquals(0, state.level.totalXp)
        assertEquals(0, state.level.xpInLevel)
        assertEquals(135, state.level.xpForNextLevel)
        assertEquals(135, state.level.xpRemaining)
        assertEquals(0f, state.level.fraction)
        assertFalse(state.level.isMaximum)
    }

    @Test
    fun accumulatedXpIsPresentedInsideItsRealLevel() {
        val state = ProgressScreenPresenter.present(PlayerProgress(totalXp = 420))

        assertEquals(3, state.level.level)
        assertEquals(420, state.level.totalXp)
        assertEquals(115, state.level.xpInLevel)
        assertEquals(205, state.level.xpForNextLevel)
        assertEquals(90, state.level.xpRemaining)
        assertEquals(115f / 205f, state.level.fraction)
    }

    @Test
    fun everyMedalShowsItsPersistedCount() {
        val counts = Medal.entries.associateWith { medal -> medal.ordinal * 3 + 1 }
        val state = ProgressScreenPresenter.present(PlayerProgress(medalCounts = counts))

        assertEquals(Medal.entries.size, state.medals.size)
        state.medals.forEach { medal ->
            assertEquals(counts.getValue(medal.medal), medal.count)
        }
    }

    @Test
    fun absoluteMasteryShowsCountAndReachedMilestones() {
        val state = ProgressScreenPresenter.present(PlayerProgress(absoluteMasteryCount = 7))

        assertEquals(7, state.absoluteMasteryCount)
        assertEquals(listOf(1, 5, 10, 25), state.masteryMilestones.map { it.target })
        assertEquals(listOf(true, true, false, false), state.masteryMilestones.map { it.reached })
    }

    @Test
    fun persistedUnlocksArePresentedAsUnlocked() {
        val state = ProgressScreenPresenter.present(
            PlayerProgress(unlockedIds = setOf("background_1", "legend_frame"))
        )

        assertTrue(state.unlocked.any { it.id == "background_1" && it.isUnlocked })
        assertTrue(state.unlocked.any { it.id == "legend_frame" && it.isUnlocked })
        assertFalse(state.locked.any { it.id == "background_1" || it.id == "legend_frame" })
    }

    @Test
    fun nearestPendingUnlockIsCalculatedFromCurrentRequirements() {
        val newPlayer = ProgressScreenPresenter.present(PlayerProgress())
        val nearMastery = ProgressScreenPresenter.present(
            PlayerProgress(
                totalXp = xpToReach(3),
                unlockedIds = setOf("background_1"),
                absoluteMasteryCount = 4
            )
        )

        assertEquals("background_1", newPlayer.nextUnlock?.id)
        assertEquals("special_master_background", nearMastery.nextUnlock?.id)
    }

    @Test
    fun levelOneHundredShowsMaximumWithoutNextLevel() {
        val state = ProgressScreenPresenter.present(
            PlayerProgress(totalXp = xpToReach(PlayerLevelCalculator.MAX_LEVEL))
        )

        assertEquals(100, state.level.level)
        assertEquals("Leyenda Sudolux", state.level.title)
        assertTrue(state.level.isMaximum)
        assertNull(state.level.xpForNextLevel)
        assertEquals(0, state.level.xpRemaining)
        assertEquals(1f, state.level.fraction)
    }

    @Test
    fun nextUnlockIsEmptyWhenEveryAvailableUnlockIsObtained() {
        val state = ProgressScreenPresenter.present(
            PlayerProgress(
                totalXp = xpToReach(PlayerLevelCalculator.MAX_LEVEL),
                medalCounts = mapOf(Medal.LEGEND to 1),
                absoluteMasteryCount = 25
            )
        )

        assertTrue(state.locked.isEmpty())
        assertNull(state.nextUnlock)
    }

    @Test
    fun statisticsPresentAllDifficultiesAndPreserveHistoricalUnclassifiedTotal() {
        val progress = PlayerProgress(
            medalCounts = mapOf(Medal.BRONZE to 4),
            completedGameRecords = mapOf(
                "easy-clean" to CompletedGameRecord(SudokuDifficulty.EASY, 0, 10L, 75),
                "easy-helped" to CompletedGameRecord(SudokuDifficulty.EASY, 1, 20L, 60)
            )
        )

        val statistics = ProgressScreenPresenter.present(progress).statistics
        val easy = statistics.difficulties.single { it.difficulty == SudokuDifficulty.EASY }

        assertEquals(SudokuDifficulty.entries, statistics.difficulties.map { it.difficulty })
        assertEquals(1, easy.withoutHints)
        assertEquals(1, easy.withHints)
        assertEquals(2, easy.total)
        assertEquals(4, statistics.totalCompleted)
        assertEquals(2, statistics.historicalUnclassified)
    }

    private fun xpToReach(level: Int): Int =
        (1 until level).sumOf(PlayerLevelCalculator::xpRequiredForNextLevel)
}
