package com.neuronovaapps.sudolux.domain.progression

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AchievementsTest {
    @Test
    fun profileFrameIsDerivedAtEveryRequiredBoundary() {
        val expected = mapOf(
            1 to ProfileFrame.INITIAL,
            15 to ProfileFrame.ADVANCED_1,
            30 to ProfileFrame.ADVANCED_2,
            50 to ProfileFrame.MASTER,
            70 to ProfileFrame.ELITE,
            100 to ProfileFrame.LEGEND
        )

        expected.forEach { (level, frame) ->
            val progress = progressAtLevel(level)

            assertEquals(frame, ProfileFrame.currentFor(progress.currentLevel))
            assertEquals(frame, progress.currentProfileFrame)
            assertEquals(
                ProfileFrame.entries.filter { it.requiredLevel <= level },
                ProfileFrame.entries.filter { it.isUnlocked(progress) }
            )
        }
    }

    @Test
    fun onlyUnlockedBoardCanBeSelectedAndAnotherSelectionReplacesThePreviousOne() {
        val level25 = progressAtLevel(25)

        assertEquals(BoardStyle.DEFAULT, level25.selectedBoardStyle)
        assertEquals(
            BoardStyle.ALTERNATIVE,
            level25.selectBoardStyle(BoardStyle.ALTERNATIVE).selectedBoardStyle
        )
        assertEquals(
            BoardStyle.ADVANCED,
            level25
                .selectBoardStyle(BoardStyle.ALTERNATIVE)
                .selectBoardStyle(BoardStyle.ADVANCED)
                .selectedBoardStyle
        )
        assertEquals(
            BoardStyle.DEFAULT,
            level25.selectBoardStyle(BoardStyle.EXPERT).selectedBoardStyle
        )
        assertTrue(BoardStyle.DEFAULT.isUnlocked(PlayerProgress()))
        assertFalse(BoardStyle.EXCLUSIVE.isUnlocked(level25))
    }

    @Test
    fun boardUnlockConditionsShareTheProgressStateAsTheirOnlySource() {
        val level60 = progressAtLevel(60)
        val tenLegends = level60.copy(medalCounts = mapOf(Medal.LEGEND to 10))

        assertEquals(
            listOf(
                BoardStyle.DEFAULT,
                BoardStyle.ALTERNATIVE,
                BoardStyle.ADVANCED,
                BoardStyle.EXPERT,
                BoardStyle.GRAND_MASTER
            ),
            BoardStyle.entries.filter { it.isUnlocked(level60) }
        )
        assertTrue(BoardStyle.EXCLUSIVE.isUnlocked(tenLegends))
    }

    @Test
    fun exclusiveBoardUsesLegendMedalsWithoutIndependentMasteryState() {
        val nineLegends = PlayerProgress(medalCounts = mapOf(Medal.LEGEND to 9))
        val tenLegends = PlayerProgress(medalCounts = mapOf(Medal.LEGEND to 10))

        assertEquals(9, nineLegends.absoluteMasteryCount)
        assertFalse(BoardStyle.EXCLUSIVE.isUnlocked(nineLegends))
        assertEquals(10, tenLegends.absoluteMasteryCount)
        assertTrue(BoardStyle.EXCLUSIVE.isUnlocked(tenLegends))
    }

    @Test
    fun badgesAreDerivedFromExistingProgressOnly() {
        val progress = progressAtLevel(70).copy(
            medalCounts = mapOf(
                Medal.BRONZE to 1,
                Medal.GOLD to 1,
                Medal.DIAMOND to 1,
                Medal.LEGEND to 1
            )
        )

        val earned = AchievementBadge.entries.filter { it.isUnlocked(progress) }

        assertTrue(AchievementBadge.FIRST_STEP in earned)
        assertTrue(AchievementBadge.ASCENT in earned)
        assertTrue(AchievementBadge.ADVANCED in earned)
        assertTrue(AchievementBadge.CHALLENGE_COMPLETE in earned)
        assertTrue(AchievementBadge.MASTER in earned)
        assertTrue(AchievementBadge.FIRST_LEGEND in earned)
        assertTrue(AchievementBadge.GRAND_MASTER in earned)
        assertTrue(AchievementBadge.SUDOLUX_ELITE in earned)
        assertFalse(AchievementBadge.SUDOLUX_LEGEND in earned)
    }

    private fun progressAtLevel(level: Int): PlayerProgress = PlayerProgress(
        totalXp = (1 until level).sumOf(PlayerLevelCalculator::xpRequiredForNextLevel)
    )
}
