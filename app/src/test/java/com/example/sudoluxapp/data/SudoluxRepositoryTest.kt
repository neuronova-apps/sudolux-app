package com.example.sudoluxapp.data

import com.example.sudoluxapp.domain.premium.AccessTier
import com.example.sudoluxapp.domain.progression.GameMode
import com.example.sudoluxapp.domain.progression.Medal
import com.example.sudoluxapp.domain.progression.PlayerProgress
import com.example.sudoluxapp.testutil.SudokuTestFixtures
import com.example.sudoluxapp.ui.game.SudokuGameState
import com.example.sudoluxapp.ui.navigation.SudoluxAppScreen
import com.example.sudoluxapp.ui.progress.ProgressScreenPresenter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SudoluxRepositoryTest {
    @Test
    fun newStorageLoadsLevelOnePlayer() {
        val progress = SudoluxRepository(MemoryStorage()).loadProgress()

        assertEquals(0, progress.totalXp)
        assertEquals(1, progress.currentLevel)
        assertEquals("Novato", progress.currentTitle)
    }

    @Test
    fun xpMedalsUnlocksMasteryAndProcessedGamesSurviveRepositoryRecreation() {
        val storage = MemoryStorage()
        val expected = PlayerProgress(
            totalXp = 987,
            medalCounts = Medal.entries.associateWith { it.ordinal + 2 },
            absoluteMasteryCount = 5,
            unlockedIds = setOf("background_1", "special_master_background"),
            processedGameIds = setOf("game-1", "game-2")
        )

        SudoluxRepository(storage).saveProgress(expected)
        val restored = SudoluxRepository(storage).loadProgress()

        assertEquals(expected, restored)
    }

    @Test
    fun activeGameSurvivesRecreationWithSamePuzzleValuesNotesErrorsHintsAndPremiumState() {
        val storage = MemoryStorage()
        val puzzle = SudokuTestFixtures.puzzle
        val editable = puzzle.initialBoard.indexOfFirst { it == 0 }
        val wrong = (1..9).first { it != puzzle.solution[editable] }
        var game = SudokuGameState(puzzle, gameId = "persisted", mode = GameMode.WITH_HINTS)
            .select(editable)
            .toggleNotes()
            .enter(4)
            .toggleNotes()
            .enter(wrong, AccessTier.PREMIUM)
            .enter(wrong, AccessTier.PREMIUM)
            .enter(wrong, AccessTier.PREMIUM)
            .continueWithPremiumPenalty()
        game = game.hint()

        SudoluxRepository(storage).saveActiveGame(game)
        val restored = requireNotNull(SudoluxRepository(storage).loadActiveGame())

        assertEquals(game.gameId, restored.gameId)
        assertEquals(game.puzzle, restored.puzzle)
        assertEquals(game.mode, restored.mode)
        assertEquals(game.values, restored.values)
        assertEquals(game.notes, restored.notes)
        assertEquals(game.errors, restored.errors)
        assertEquals(game.hintsUsed, restored.hintsUsed)
        assertEquals(game.xpPossible, restored.xpPossible)
        assertTrue(restored.premiumContinuationUsed)
    }

    @Test
    fun completedGameIsInvalidatedAndCannotAppearAsContinue() {
        val storage = MemoryStorage()
        val repository = SudoluxRepository(storage)
        repository.saveActiveGame(SudokuGameState(SudokuTestFixtures.puzzle, gameId = "active"))
        repository.saveActiveGame(
            SudokuGameState(SudokuTestFixtures.puzzle, gameId = "active", showVictory = true)
        )

        assertNull(SudoluxRepository(storage).loadActiveGame())
    }

    @Test
    fun navigatingToProgressDoesNotRemoveAnActiveGame() {
        val storage = MemoryStorage()
        val repository = SudoluxRepository(storage)
        val game = SudokuGameState(SudokuTestFixtures.puzzle, gameId = "navigation-active")
        repository.saveActiveGame(game)

        val destination = SudoluxAppScreen.PROGRESS

        assertEquals(SudoluxAppScreen.PROGRESS, destination)
        assertEquals(game.gameId, SudoluxRepository(storage).loadActiveGame()?.gameId)
    }

    @Test
    fun returningHomeKeepsProgressAndRestoredDataFeedsProgressPresentation() {
        val storage = MemoryStorage()
        val expected = PlayerProgress(
            totalXp = 420,
            medalCounts = Medal.entries.associateWith { it.ordinal + 1 },
            absoluteMasteryCount = 5,
            unlockedIds = setOf("background_1", "special_master_background")
        )
        SudoluxRepository(storage).saveProgress(expected)

        val destination = SudoluxAppScreen.HOME
        val restored = SudoluxRepository(storage).loadProgress()
        val presented = ProgressScreenPresenter.present(restored)

        assertEquals(SudoluxAppScreen.HOME, destination)
        assertEquals(expected, restored)
        assertEquals(420, presented.level.totalXp)
        assertEquals(5, presented.absoluteMasteryCount)
        assertEquals(6, presented.medals.size)
        assertTrue(presented.unlocked.any { it.id == "special_master_background" })
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
