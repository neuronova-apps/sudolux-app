package com.example.sudoluxapp.domain.settings

import com.example.sudoluxapp.domain.progression.Medal
import com.example.sudoluxapp.domain.progression.PlayerLevelCalculator
import com.example.sudoluxapp.domain.progression.PlayerProgress
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
        val levelNine = progressAtLevel(9)
        val levelTen = progressAtLevel(10)
        val levelTwentyFive = progressAtLevel(25)
        val levelFifty = progressAtLevel(50)

        assertFalse(AppTheme.FOREST.isUnlocked(levelNine))
        assertTrue(AppTheme.FOREST.isUnlocked(levelTen))
        assertFalse(AppTheme.AMBAR.isUnlocked(levelTen))
        assertTrue(AppTheme.AMBAR.isUnlocked(levelTwentyFive))
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
    }

    private fun progressAtLevel(level: Int): PlayerProgress = PlayerProgress(
        totalXp = (1 until level).sumOf(PlayerLevelCalculator::xpRequiredForNextLevel)
    )
}
