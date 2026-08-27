package com.example.sudoluxapp.domain.progression

import com.example.sudoluxapp.domain.sudoku.SudokuDifficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GameStatisticsTest {
    @Test
    fun everyDifficultyClassifiesZeroHintsAndPositiveHintsSeparately() {
        var progress = PlayerProgress()

        SudokuDifficulty.entries.forEachIndexed { index, difficulty ->
            progress = applyVictory(progress, "${difficulty.name}-without", difficulty, 0, index * 2L).progress
            progress = applyVictory(progress, "${difficulty.name}-with", difficulty, 2, index * 2L + 1L).progress
        }

        SudokuDifficulty.entries.forEach { difficulty ->
            val counts = progress.statistics.forDifficulty(difficulty)
            assertEquals(1, counts.withoutHints)
            assertEquals(1, counts.withHints)
            assertEquals(counts.withoutHints + counts.withHints, counts.total)
        }
        assertEquals(5, progress.statistics.withoutHints)
        assertEquals(5, progress.statistics.withHints)
        assertEquals(10, progress.statistics.classifiedTotal)
        assertEquals(10, progress.statistics.totalCompleted)
    }

    @Test
    fun duplicateGameIdChangesNeitherRewardsNorStatisticsButAnotherIdDoes() {
        val result = victory(SudokuDifficulty.EASY, hintsUsed = 0)
        val first = ProgressionCalculator.applyResult(PlayerProgress(), "same-game", result, 100L)
        val duplicate = ProgressionCalculator.applyResult(first.progress, "same-game", result, 200L)
        val another = ProgressionCalculator.applyResult(duplicate.progress, "another-game", result, 300L)

        assertTrue(first.rewardApplied)
        assertFalse(duplicate.rewardApplied)
        assertEquals(first.progress, duplicate.progress)
        assertEquals(1, duplicate.progress.statistics.classifiedTotal)
        assertEquals(first.progress.totalXp, duplicate.progress.totalXp)
        assertEquals(first.progress.completedSudokus, duplicate.progress.completedSudokus)
        assertTrue(another.rewardApplied)
        assertEquals(2, another.progress.statistics.classifiedTotal)
        assertEquals(2, another.progress.completedSudokus)
    }

    @Test
    fun historicalMedalsRemainInTotalWithoutInventingClassification() {
        val historical = PlayerProgress(medalCounts = mapOf(Medal.BRONZE to 7))
        val updated = applyVictory(historical, "new-medium", SudokuDifficulty.MEDIUM, 1, 900L).progress

        assertEquals(8, updated.completedSudokus)
        assertEquals(8, updated.statistics.totalCompleted)
        assertEquals(1, updated.statistics.classifiedTotal)
        assertEquals(7, updated.statistics.historicalUnclassified)
        assertEquals(0, updated.statistics.forDifficulty(SudokuDifficulty.MEDIUM).withoutHints)
        assertEquals(1, updated.statistics.forDifficulty(SudokuDifficulty.MEDIUM).withHints)
    }

    private fun applyVictory(
        progress: PlayerProgress,
        gameId: String,
        difficulty: SudokuDifficulty,
        hintsUsed: Int,
        completedAt: Long
    ): ProgressUpdate = ProgressionCalculator.applyResult(
        progress,
        gameId,
        victory(difficulty, hintsUsed),
        completedAt
    )

    private fun victory(difficulty: SudokuDifficulty, hintsUsed: Int): GameResult =
        ProgressionCalculator.result(
            GamePerformance(
                difficulty = difficulty,
                mode = GameMode.WITH_HINTS,
                hintsUsed = hintsUsed,
                errors = 0,
                completed = true
            )
        )
}
