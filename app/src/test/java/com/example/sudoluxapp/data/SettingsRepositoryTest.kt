package com.example.sudoluxapp.data

import com.example.sudoluxapp.domain.progression.Medal
import com.example.sudoluxapp.domain.progression.PlayerProgress
import com.example.sudoluxapp.domain.settings.AppTheme
import com.example.sudoluxapp.domain.settings.SudokuNumberSize
import com.example.sudoluxapp.domain.settings.UserSettings
import com.example.sudoluxapp.testutil.SudokuTestFixtures
import com.example.sudoluxapp.ui.game.SudokuGameState
import com.example.sudoluxapp.ui.navigation.SudoluxAppScreen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SettingsRepositoryTest {
    @Test
    fun newStorageUsesDocumentedDefaults() {
        assertEquals(UserSettings.Default, SettingsRepository(MemoryStorage()).load())
        with(UserSettings.Default) {
            assertEquals(AppTheme.CLASSIC, theme)
            assertFalse(highContrast)
            assertEquals(SudokuNumberSize.NORMAL, numberSize)
            assertFalse(reduceAnimations)
            assertTrue(soundEnabled)
            assertTrue(hapticsEnabled)
            assertTrue(autoCleanNotes)
            assertTrue(showErrorsImmediately)
            assertTrue(highlightMatchingNumbers)
            assertTrue(highlightRelatedArea)
        }
    }

    @Test
    fun changedPreferenceIsSavedAndSurvivesRepositoryRecreation() {
        val storage = MemoryStorage()
        val expected = UserSettings.Default.copy(
            theme = AppTheme.OCEAN,
            highContrast = true,
            numberSize = SudokuNumberSize.LARGE,
            reduceAnimations = true,
            soundEnabled = false,
            hapticsEnabled = false,
            autoCleanNotes = false,
            showErrorsImmediately = false,
            highlightMatchingNumbers = false,
            highlightRelatedArea = false
        )

        SettingsRepository(storage).save(expected)

        assertEquals(expected, SettingsRepository(storage).load())
    }

    @Test
    fun savedThemeIsNotOverwrittenByClassicDefault() {
        val storage = MemoryStorage()
        SettingsRepository(storage).save(UserSettings.Default.copy(theme = AppTheme.NIGHT))

        assertEquals(AppTheme.NIGHT, SettingsRepository(storage).load().theme)
    }

    @Test
    fun legacyLightDarkAndSystemValuesMigrateSafely() {
        fun migrated(value: String): AppTheme {
            val storage = MemoryStorage()
            storage.replace(emptySet(), mapOf("settings.theme" to value))
            return SettingsRepository(storage).load().theme
        }

        assertEquals(AppTheme.CLASSIC, migrated("LIGHT"))
        assertEquals(AppTheme.NIGHT, migrated("DARK"))
        assertEquals(AppTheme.CLASSIC, migrated("SYSTEM"))
    }

    @Test
    fun resetRestoresOnlySettingsAndKeepsProgressAndActiveGame() {
        val storage = MemoryStorage()
        val stateRepository = SudoluxRepository(storage)
        val progress = PlayerProgress(
            totalXp = 840,
            medalCounts = Medal.entries.associateWith { 2 },
            absoluteMasteryCount = 3,
            unlockedIds = setOf("background_1")
        )
        val game = SudokuGameState(SudokuTestFixtures.puzzle, gameId = "still-active")
            .select(2)
            .toggleNotes()
            .enter(4)
        stateRepository.saveProgress(progress)
        stateRepository.saveActiveGame(game)
        val settingsRepository = SettingsRepository(storage)
        settingsRepository.save(UserSettings.Default.copy(highContrast = true, soundEnabled = false))

        assertEquals(UserSettings.Default, settingsRepository.reset())
        assertEquals(AppTheme.CLASSIC, settingsRepository.load().theme)
        assertEquals(progress, stateRepository.loadProgress())
        assertEquals(game.gameId, stateRepository.loadActiveGame()?.gameId)
        assertEquals(game.notes, stateRepository.loadActiveGame()?.notes)
    }

    @Test
    fun openingAndLeavingSettingsDoesNotDiscardActiveGame() {
        val storage = MemoryStorage()
        val repository = SudoluxRepository(storage)
        val game = SudokuGameState(SudokuTestFixtures.puzzle, gameId = "settings-navigation")
        repository.saveActiveGame(game)

        val route = SudoluxAppScreen.SETTINGS
        val returnRoute = SudoluxAppScreen.HOME

        assertEquals(SudoluxAppScreen.SETTINGS, route)
        assertEquals(SudoluxAppScreen.HOME, returnRoute)
        assertEquals(game.gameId, repository.loadActiveGame()?.gameId)
    }

    private class MemoryStorage : KeyValueStorage {
        private val values = mutableMapOf<String, String>()
        override fun get(key: String): String? = values[key]
        override fun replace(removedKeys: Set<String>, values: Map<String, String>) {
            removedKeys.forEach(this.values::remove)
            this.values.putAll(values)
        }
    }
}
