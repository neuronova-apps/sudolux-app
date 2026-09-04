package com.neuronovaapps.sudolux.domain.settings

import com.neuronovaapps.sudolux.domain.progression.Medal
import com.neuronovaapps.sudolux.domain.progression.PlayerLevelCalculator
import com.neuronovaapps.sudolux.domain.progression.PlayerProgress
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppThemeTest {
    @Test
    fun newPlayerOnlyHasClassicAndNightAvailable() {
        val progress = PlayerProgress()

        assertTrue(AppTheme.CLASSIC.isUnlocked(progress))
        assertTrue(AppTheme.NIGHT.isUnlocked(progress))
        assertFalse(AppTheme.OCEAN.isUnlocked(progress))
        assertFalse(AppTheme.FOREST.isUnlocked(progress))
        assertFalse(AppTheme.AMBAR.isUnlocked(progress))
        assertFalse(AppTheme.MASTER.isUnlocked(progress))
    }

    @Test
    fun oceanUnlocksFromTenPersistedVictoryMedals() {
        val nineCompleted = PlayerProgress(medalCounts = mapOf(Medal.BRONZE to 9))
        val tenCompleted = PlayerProgress(medalCounts = mapOf(Medal.BRONZE to 10))

        assertFalse(AppTheme.OCEAN.isUnlocked(nineCompleted))
        assertTrue(AppTheme.OCEAN.isUnlocked(tenCompleted))
        assertEquals(10, tenCompleted.completedSudokus)
    }

    @Test
    fun levelThemesUnlockAtTheirExactBoundaries() {
        val levelFourteen = progressAtLevel(14)
        val levelFifteen = progressAtLevel(15)
        val levelTwentyFive = progressAtLevel(25)
        val levelFifty = progressAtLevel(50)

        assertFalse(AppTheme.FOREST.isUnlocked(levelFourteen))
        assertTrue(AppTheme.FOREST.isUnlocked(levelFifteen))
        assertEquals("Alcanza nivel 15", AppTheme.FOREST.unlockDescription)
        assertFalse(AppTheme.AMBAR.isUnlocked(levelFifteen))
        assertTrue(AppTheme.AMBAR.isUnlocked(levelTwentyFive))
        assertFalse(AppTheme.MASTER.isUnlocked(progressAtLevel(49)))
        assertFalse(AppTheme.MASTER.isUnlocked(levelTwentyFive))
        assertTrue(AppTheme.MASTER.isUnlocked(levelFifty))
    }

    @Test
    fun selectionPolicyRejectsLockedThemeAndAcceptsAvailableTheme() {
        val initial = UserSettings.Default
        val newPlayer = PlayerProgress()

        assertEquals(
            AppTheme.CLASSIC,
            ThemeSelectionPolicy.select(initial, AppTheme.MASTER, newPlayer).theme
        )
        assertEquals(
            AppTheme.NIGHT,
            ThemeSelectionPolicy.select(initial, AppTheme.NIGHT, newPlayer).theme
        )
        assertEquals(
            AppTheme.FOREST,
            ThemeSelectionPolicy.select(initial, AppTheme.FOREST, progressAtLevel(15)).theme
        )
    }

    private fun progressAtLevel(level: Int): PlayerProgress = PlayerProgress(
        totalXp = (1 until level).sumOf(PlayerLevelCalculator::xpRequiredForNextLevel)
    )
}
