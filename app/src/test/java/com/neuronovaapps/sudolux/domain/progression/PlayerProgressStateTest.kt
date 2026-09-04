package com.neuronovaapps.sudolux.domain.progression

import com.neuronovaapps.sudolux.domain.sudoku.SudokuDifficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlayerProgressStateTest {
    @Test
    fun newPlayerStartsAtLevelOneNoviceWithZeroXp() {
        val progress = PlayerProgress()

        assertEquals(0, progress.totalXp)
        assertEquals(1, progress.currentLevel)
        assertEquals("Novato", progress.currentTitle)
        assertEquals(0, progress.xpInCurrentLevel)
        assertEquals(135, progress.xpRequiredForNextLevel)
        assertEquals(0f, progress.progressToNextLevel)
    }

    @Test
    fun perfectEasyWithoutHintsAwards75XpExactlyOnce() {
        val result = victory(SudokuDifficulty.EASY)
        val first = ProgressionCalculator.applyResult(PlayerProgress(), "easy-1", result)
        val duplicate = ProgressionCalculator.applyResult(first.progress, "easy-1", result)

        assertEquals(75, result.xpEarned)
        assertEquals(75, first.progress.totalXp)
        assertTrue(first.rewardApplied)
        assertEquals(first.progress, duplicate.progress)
        assertFalse(duplicate.rewardApplied)
        assertEquals(1, duplicate.progress.completedSudokus)
        assertEquals(1, duplicate.progress.statistics.classifiedTotal)
    }

    @Test
    fun lostGameAwardsNothingAndCreatesNoVictoryRewards() {
        val loss = ProgressionCalculator.result(
            GamePerformance(
                difficulty = SudokuDifficulty.MASTER,
                mode = GameMode.NO_HINTS,
                hintsUsed = 0,
                errors = 3,
                completed = false
            )
        )
        val update = ProgressionCalculator.applyResult(PlayerProgress(), "loss-1", loss)

        assertEquals(0, update.progress.totalXp)
        assertEquals(1, update.progress.currentLevel)
        assertEquals(0, update.progress.absoluteMasteryCount)
        assertTrue(update.progress.medalCounts.values.all { it == 0 })
        assertTrue(update.newlyUnlocked.isEmpty())
        assertFalse(update.rewardApplied)
    }

    @Test
    fun perfectMasterWithoutHintsAwards600XpAndCanCrossSeveralLevels() {
        val result = victory(SudokuDifficulty.MASTER)
        val update = ProgressionCalculator.applyResult(PlayerProgress(), "master-1", result)

        assertEquals(600, result.xpEarned)
        assertEquals(4, update.progress.currentLevel)
        assertEquals(90, update.progress.xpInCurrentLevel)
        assertEquals(600, update.progress.totalXp)
        assertTrue(update.leveledUp)
    }

    @Test
    fun levelUpPreservesExcessXp() {
        val update = ProgressionCalculator.applyResult(
            PlayerProgress(),
            "hard-1",
            victory(SudokuDifficulty.HARD)
        )

        assertEquals(2, update.progress.currentLevel)
        assertEquals(75, update.progress.xpInCurrentLevel)
        assertEquals(210, update.progress.totalXp)
    }

    @Test
    fun titlesChangeAtEveryConfiguredBoundary() {
        val expected = mapOf(
            1 to "Novato",
            6 to "Principiante",
            11 to "Aprendiz",
            21 to "Estratega",
            31 to "Analista",
            41 to "Experto",
            51 to "Maestro",
            61 to "Gran maestro",
            71 to "Élite",
            81 to "Maestro legendario",
            91 to "Leyenda",
            100 to "Leyenda Sudolux"
        )

        expected.forEach { (level, title) ->
            assertEquals(title, PlayerLevelCalculator.calculate(xpToReach(level)).title)
        }
    }

    @Test
    fun levelNeverExceeds100ButXpKeepsAccumulating() {
        val atMaximum = PlayerProgress(totalXp = xpToReach(100))
        val update = ProgressionCalculator.applyResult(
            atMaximum,
            "max-1",
            victory(SudokuDifficulty.EASY)
        )

        assertEquals(100, update.progress.currentLevel)
        assertEquals("Leyenda Sudolux", update.progress.currentTitle)
        assertEquals(atMaximum.totalXp + 75, update.progress.totalXp)
    }

    @Test
    fun victoryIncrementsOnlyItsAwardedMedal() {
        val result = victory(SudokuDifficulty.HARD)
        val update = ProgressionCalculator.applyResult(PlayerProgress(), "medal-1", result)
        val awarded = requireNotNull(result.medal)

        Medal.entries.forEach { medal ->
            assertEquals(if (medal == awarded) 1 else 0, update.progress.medalCount(medal))
        }
    }

    @Test
    fun levelAndAchievementUnlocksAreDetectedAndStored() {
        val result = victory(SudokuDifficulty.MASTER)
        val update = ProgressionCalculator.applyResult(PlayerProgress(), "unlock-1", result)
        val newIds = update.newlyUnlocked.map { it.id }.toSet()

        assertTrue("background_1" in newIds)
        assertTrue("legend_frame" in newIds)
        assertTrue(update.progress.unlockedIds.containsAll(newIds))
        assertEquals(1, update.progress.absoluteMasteryCount)
    }

    @Test
    fun absoluteMasteryUnlockIsDetectedAtItsMilestone() {
        val current = PlayerProgress(
            medalCounts = mapOf(Medal.LEGEND to 4)
        )
        val update = ProgressionCalculator.applyResult(
            current,
            "mastery-unlock-5",
            victory(SudokuDifficulty.MASTER)
        )

        assertEquals(5, update.progress.absoluteMasteryCount)
        assertTrue(update.newlyUnlocked.any { it.id == "special_master_background" })
    }

    private fun victory(difficulty: SudokuDifficulty): GameResult = ProgressionCalculator.result(
        GamePerformance(
            difficulty = difficulty,
            mode = GameMode.NO_HINTS,
            hintsUsed = 0,
            errors = 0,
            completed = true
        )
    )

    private fun xpToReach(level: Int): Int =
        (1 until level).sumOf(PlayerLevelCalculator::xpRequiredForNextLevel)
}
