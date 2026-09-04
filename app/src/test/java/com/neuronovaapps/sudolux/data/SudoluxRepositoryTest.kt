package com.neuronovaapps.sudolux.data

import com.neuronovaapps.sudolux.domain.premium.AccessTier
import com.neuronovaapps.sudolux.domain.progression.BoardStyle
import com.neuronovaapps.sudolux.domain.progression.GameMode
import com.neuronovaapps.sudolux.domain.progression.CompletedGameRecord
import com.neuronovaapps.sudolux.domain.progression.GameCompletionStatus
import com.neuronovaapps.sudolux.domain.progression.Medal
import com.neuronovaapps.sudolux.domain.progression.PlayerProgress
import com.neuronovaapps.sudolux.testutil.SudokuTestFixtures
import com.neuronovaapps.sudolux.ui.game.SudokuGameState
import com.neuronovaapps.sudolux.ui.navigation.SudoluxAppScreen
import com.neuronovaapps.sudolux.ui.progress.ProgressScreenPresenter
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
        assertEquals(BoardStyle.DEFAULT, progress.selectedBoardStyle)
    }

    @Test
    fun selectedBoardStyleSurvivesRepositoryRecreation() {
        val storage = MemoryStorage()
        val progress = PlayerProgress(
            totalXp = xpToReach(25),
            selectedBoardStyle = BoardStyle.ADVANCED
        )

        SudoluxRepository(storage).saveProgress(progress)

        assertEquals(BoardStyle.ADVANCED, SudoluxRepository(storage).loadProgress().selectedBoardStyle)
    }

    @Test
    fun returningToDefaultBoardAlsoSurvivesRepositoryRecreation() {
        val storage = MemoryStorage()
        val repository = SudoluxRepository(storage)
        val advanced = PlayerProgress(totalXp = xpToReach(25))
            .selectBoardStyle(BoardStyle.ADVANCED)

        repository.saveProgress(advanced)
        repository.saveProgress(advanced.selectBoardStyle(BoardStyle.DEFAULT))

        assertEquals(BoardStyle.DEFAULT, SudoluxRepository(storage).loadProgress().selectedBoardStyle)
    }

    @Test
    fun changingBoardStyleDoesNotModifyAnActiveGame() {
        val storage = MemoryStorage()
        val repository = SudoluxRepository(storage)
        val puzzle = SudokuTestFixtures.puzzle
        val editable = puzzle.initialBoard.indexOfFirst { it == 0 }
        val activeGame = SudokuGameState(puzzle, gameId = "board-style-active")
            .select(editable)
            .toggleNotes()
            .enter(4)

        repository.saveActiveGame(activeGame)
        repository.saveProgress(
            PlayerProgress(totalXp = xpToReach(25))
                .selectBoardStyle(BoardStyle.ADVANCED)
        )

        val recreated = SudoluxRepository(storage)
        val restoredGame = requireNotNull(recreated.loadActiveGame())
        assertEquals(BoardStyle.ADVANCED, recreated.loadProgress().selectedBoardStyle)
        assertEquals(activeGame.gameId, restoredGame.gameId)
        assertEquals(activeGame.puzzle, restoredGame.puzzle)
        assertEquals(activeGame.values, restoredGame.values)
        assertEquals(activeGame.notes, restoredGame.notes)
        assertEquals(activeGame.selectedCell, restoredGame.selectedCell)
        assertEquals(activeGame.notesMode, restoredGame.notesMode)
        assertEquals(activeGame.errors, restoredGame.errors)
        assertEquals(activeGame.hintsUsed, restoredGame.hintsUsed)
    }

    @Test
    fun legacyMasteryMigratesToLegendMedalsAndParallelCounterIsRemovedOnSave() {
        val storage = MemoryStorage()
        storage.replace(
            emptySet(),
            mapOf(
                "progress.mastery" to "5",
                "progress.medal.LEGEND" to "2"
            )
        )
        val repository = SudoluxRepository(storage)

        val migrated = repository.loadProgress()
        repository.saveProgress(migrated)

        assertEquals(5, migrated.legendMedalCount)
        assertEquals(5, migrated.absoluteMasteryCount)
        assertNull(storage.get("progress.mastery"))
        assertEquals("5", storage.get("progress.medal.LEGEND"))
    }

    @Test
    fun xpMedalsUnlocksMasteryAndProcessedGamesSurviveRepositoryRecreation() {
        val storage = MemoryStorage()
        val expected = PlayerProgress(
            totalXp = 987,
            medalCounts = Medal.entries.associateWith { it.ordinal + 2 },
            unlockedIds = setOf("background_1", "special_master_background"),
            processedGameIds = setOf("game-1", "game-2"),
            completedGameRecords = mapOf(
                "game-1" to CompletedGameRecord(
                    difficulty = com.neuronovaapps.sudolux.domain.sudoku.SudokuDifficulty.EASY,
                    hintsUsed = 0,
                    completedAtEpochMillis = 123456L,
                    xpEarned = 75,
                    status = GameCompletionStatus.COMPLETED
                ),
                "game-2" to CompletedGameRecord(
                    difficulty = com.neuronovaapps.sudolux.domain.sudoku.SudokuDifficulty.MASTER,
                    hintsUsed = 3,
                    completedAtEpochMillis = 234567L,
                    xpEarned = 360
                )
            )
        )

        SudoluxRepository(storage).saveProgress(expected)
        val restored = SudoluxRepository(storage).loadProgress()

        assertEquals(expected, restored)
    }

    @Test
    fun legacyProgressWithoutDetailedRecordsKeepsItsCompletedTotalUnclassified() {
        val storage = MemoryStorage()
        storage.replace(
            emptySet(),
            mapOf(
                "progress.xp" to "500",
                "progress.medal.BRONZE" to "4",
                "progress.medal.GOLD" to "2"
            )
        )

        val restored = SudoluxRepository(storage).loadProgress()

        assertEquals(6, restored.completedSudokus)
        assertEquals(6, restored.statistics.totalCompleted)
        assertEquals(6, restored.statistics.historicalUnclassified)
        assertEquals(0, restored.statistics.classifiedTotal)
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
            unlockedIds = setOf("background_1", "special_master_background")
        )
        SudoluxRepository(storage).saveProgress(expected)

        val destination = SudoluxAppScreen.HOME
        val restored = SudoluxRepository(storage).loadProgress()
        val presented = ProgressScreenPresenter.present(restored)

        assertEquals(SudoluxAppScreen.HOME, destination)
        assertEquals(expected, restored)
        assertEquals(420, presented.level.totalXp)
        assertEquals(6, presented.absoluteMasteryCount)
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

    private fun xpToReach(level: Int): Int =
        (1 until level).sumOf(com.neuronovaapps.sudolux.domain.progression.PlayerLevelCalculator::xpRequiredForNextLevel)
}
