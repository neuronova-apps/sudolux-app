package com.example.sudoluxapp.domain.progression

import com.example.sudoluxapp.domain.sudoku.SudokuDifficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressionCalculatorTest {
    @Test
    fun baseXpMatchesEveryDifficulty() {
        assertEquals(50, XpCalculator.baseXp(SudokuDifficulty.EASY))
        assertEquals(80, XpCalculator.baseXp(SudokuDifficulty.MEDIUM))
        assertEquals(120, XpCalculator.baseXp(SudokuDifficulty.HARD))
        assertEquals(180, XpCalculator.baseXp(SudokuDifficulty.EXPERT))
        assertEquals(250, XpCalculator.baseXp(SudokuDifficulty.MASTER))
    }

    @Test
    fun includedHintsApply10090_75And60Percent() {
        val expected = listOf(120, 108, 90, 72)
        expected.forEachIndexed { hints, expectedXp ->
            assertEquals(expectedXp, xp(difficulty = SudokuDifficulty.HARD, hints = hints, errors = 1))
        }
    }

    @Test
    fun extraHintCostsMatchFourthFifthSixthAndLaterRules() {
        assertEquals(15, XpCalculator.extraHintCost(4))
        assertEquals(20, XpCalculator.extraHintCost(5))
        assertEquals(25, XpCalculator.extraHintCost(6))
        assertEquals(30, XpCalculator.extraHintCost(7))
        assertEquals(30, XpCalculator.extraHintCost(20))
    }

    @Test
    fun extraHintsDiscountXpAfterIncludedHintReduction() {
        assertEquals(72, xp(difficulty = SudokuDifficulty.HARD, hints = 3, errors = 1))
        assertEquals(57, xp(difficulty = SudokuDifficulty.HARD, hints = 4, errors = 1))
        assertEquals(37, xp(difficulty = SudokuDifficulty.HARD, hints = 5, errors = 1))
        assertEquals(12, xp(difficulty = SudokuDifficulty.HARD, hints = 6, errors = 1))
        assertEquals(12, xp(difficulty = SudokuDifficulty.HARD, hints = 7, errors = 1))
    }

    @Test
    fun xpNeverBecomesNegativeAndCompletedRewardKeepsTenPercentMinimum() {
        val result = xp(difficulty = SudokuDifficulty.EASY, hints = 50, errors = 2)
        assertTrue(result >= 0)
        assertEquals(5, result)
    }

    @Test
    fun zeroErrorsAdds15PercentAndTwoErrorsSubtract10Percent() {
        assertEquals(57, xp(hints = 0, errors = 0))
        assertEquals(45, xp(hints = 0, errors = 2))
    }

    @Test
    fun thirdErrorOrIncompleteGameAwardsZeroXp() {
        assertEquals(0, xp(errors = 3))
        assertEquals(0, XpCalculator.calculate(performance(errors = 0, completed = false)))
    }

    @Test
    fun perfectNoHintsRewardsUseTheSpecialTable() {
        val expected = mapOf(
            SudokuDifficulty.EASY to 75,
            SudokuDifficulty.MEDIUM to 130,
            SudokuDifficulty.HARD to 210,
            SudokuDifficulty.EXPERT to 360,
            SudokuDifficulty.MASTER to 600
        )
        expected.forEach { (difficulty, expectedXp) ->
            assertEquals(
                expectedXp,
                XpCalculator.calculate(
                    performance(difficulty = difficulty, mode = GameMode.NO_HINTS, errors = 0)
                )
            )
        }
    }

    @Test
    fun onlyPerfectNoHintsMasterReceivesLegendMedal() {
        val legend = performance(
            difficulty = SudokuDifficulty.MASTER,
            mode = GameMode.NO_HINTS,
            errors = 0
        )
        assertEquals(Medal.LEGEND, MedalCalculator.calculate(legend))

        SudokuDifficulty.entries.forEach { difficulty ->
            GameMode.entries.forEach { mode ->
                (0..2).forEach { errors ->
                    val candidate = performance(difficulty, mode, errors = errors)
                    if (candidate != legend) {
                        assertFalse(MedalCalculator.calculate(candidate) == Medal.LEGEND)
                    }
                }
            }
        }
        assertNull(MedalCalculator.calculate(legend.copy(completed = false)))
    }

    @Test
    fun eachCompletedDifficultyAwardsItsSingleFixedMedal() {
        val expected = mapOf(
            SudokuDifficulty.EASY to Medal.BRONZE,
            SudokuDifficulty.MEDIUM to Medal.SILVER,
            SudokuDifficulty.HARD to Medal.GOLD,
            SudokuDifficulty.EXPERT to Medal.PLATINUM,
            SudokuDifficulty.MASTER to Medal.DIAMOND
        )

        expected.forEach { (difficulty, medal) ->
            val result = ProgressionCalculator.result(
                performance(
                    difficulty = difficulty,
                    mode = GameMode.WITH_HINTS,
                    hints = 1,
                    errors = 1
                )
            )
            val update = ProgressionCalculator.applyResult(
                PlayerProgress(),
                "fixed-${difficulty.name}",
                result
            )

            assertEquals(medal, result.medal)
            assertEquals(1, update.progress.completedSudokus)
            Medal.entries.forEach { candidate ->
                assertEquals(if (candidate == medal) 1 else 0, update.progress.medalCount(candidate))
            }
        }
    }

    @Test
    fun perfectMasterAwardsLegendWithoutAddingDiamond() {
        val result = ProgressionCalculator.result(
            performance(SudokuDifficulty.MASTER, GameMode.NO_HINTS, errors = 0)
        )
        val update = ProgressionCalculator.applyResult(PlayerProgress(), "perfect-master", result)

        assertEquals(Medal.LEGEND, result.medal)
        assertEquals(1, update.progress.medalCount(Medal.LEGEND))
        assertEquals(0, update.progress.medalCount(Medal.DIAMOND))
        assertEquals(1, update.progress.completedSudokus)
    }

    @Test
    fun playerLevelUsesProgressiveFormulaAndStopsAt100() {
        assertEquals(135, PlayerLevelCalculator.xpRequiredForNextLevel(1))
        assertEquals(450, PlayerLevelCalculator.xpRequiredForNextLevel(10))
        val levelTwo = PlayerLevelCalculator.calculate(135)
        assertEquals(2, levelTwo.level)
        assertEquals(0, levelTwo.xpInLevel)

        val xpForLevel100 = (1 until 100).sumOf(PlayerLevelCalculator::xpRequiredForNextLevel)
        val max = PlayerLevelCalculator.calculate(xpForLevel100 + 50_000)
        assertEquals(100, max.level)
        assertEquals("Leyenda Sudolux", max.title)
        assertEquals(1f, max.progress)
        assertEquals(xpForLevel100 + 50_000, max.totalXp)
    }

    @Test
    fun unlocksDependOnLevelsAndAbsoluteMasteryRatherThanPremium() {
        val xpForLevel10 = (1 until 10).sumOf(PlayerLevelCalculator::xpRequiredForNextLevel)
        val levelProgress = PlayerProgress(totalXp = xpForLevel10)
        assertEquals(
            UnlockStatus.UNLOCKED,
            UnlockableCatalog.levelRewards.first { it.id == "visual_theme_1" }.status(levelProgress)
        )
        assertFalse(UnlockableCatalog.levelRewards.first { it.id == "profile_frame" }.isUnlocked(levelProgress))

        val masteryProgress = PlayerProgress(medalCounts = mapOf(Medal.LEGEND to 10))
        assertTrue(UnlockableCatalog.achievementRewards.first { it.id == "exclusive_board" }.isUnlocked(masteryProgress))
        assertFalse(
            UnlockableCatalog.achievementRewards
                .first { it.id == "absolute_sudolux_legend_theme" }
                .isUnlocked(masteryProgress)
        )
        assertTrue(UnlockableCatalog.achievementRewards.all { it.origin == RewardOrigin.SPECIAL })
    }

    @Test
    fun absoluteMasteryTracksFirstAchievementAndMilestones() {
        val result = ProgressionCalculator.result(
            performance(SudokuDifficulty.MASTER, GameMode.NO_HINTS, errors = 0)
        )
        val first = ProgressionCalculator.applyResult(PlayerProgress(), "mastery-1", result)
        assertTrue(first.newAbsoluteMasteryAchievement)
        assertEquals(listOf(1), first.reachedAbsoluteMasteryMilestones)
        assertEquals(1, first.progress.absoluteMasteryCount)

        val fifth = ProgressionCalculator.applyResult(
            PlayerProgress(
                medalCounts = mapOf(Medal.LEGEND to 4)
            ),
            "mastery-5",
            result
        )
        assertFalse(fifth.newAbsoluteMasteryAchievement)
        assertEquals(listOf(5), fifth.reachedAbsoluteMasteryMilestones)
        assertTrue(fifth.newlyUnlocked.any { it.id == "special_master_background" })
    }

    @Test
    fun everyAbsoluteMasteryMilestoneComesDirectlyFromLegendMedals() {
        val legendResult = ProgressionCalculator.result(
            performance(SudokuDifficulty.MASTER, GameMode.NO_HINTS, errors = 0)
        )

        ProgressionCalculator.absoluteMasteryMilestones.forEach { milestone ->
            val before = PlayerProgress(
                medalCounts = mapOf(Medal.LEGEND to milestone - 1)
            )
            val update = ProgressionCalculator.applyResult(
                before,
                "mastery-$milestone",
                legendResult
            )

            assertEquals(milestone, update.progress.legendMedalCount)
            assertEquals(milestone, update.progress.absoluteMasteryCount)
            assertEquals(listOf(milestone), update.reachedAbsoluteMasteryMilestones)
        }
    }

    @Test
    fun medalCountsAreCumulativeWithoutAnArtificialMaximum() {
        val bronzeResult = ProgressionCalculator.result(
            performance(SudokuDifficulty.EASY, GameMode.WITH_HINTS, hints = 1, errors = 1)
        )
        val progress = (1..150).fold(PlayerProgress()) { current, gameNumber ->
            ProgressionCalculator.applyResult(current, "bronze-$gameNumber", bronzeResult).progress
        }

        assertEquals(150, progress.medalCount(Medal.BRONZE))
        assertEquals(150, progress.completedSudokus)
    }

    private fun xp(
        difficulty: SudokuDifficulty = SudokuDifficulty.EASY,
        hints: Int = 0,
        errors: Int = 1
    ): Int = XpCalculator.calculate(performance(difficulty, hints = hints, errors = errors))

    private fun performance(
        difficulty: SudokuDifficulty = SudokuDifficulty.EASY,
        mode: GameMode = GameMode.WITH_HINTS,
        hints: Int = 0,
        errors: Int = 1,
        completed: Boolean = true
    ) = GamePerformance(difficulty, mode, hints, errors, completed)
}
