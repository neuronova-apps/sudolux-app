package com.example.sudoluxapp.ui.game

import com.example.sudoluxapp.domain.progression.GameMode
import com.example.sudoluxapp.domain.sudoku.SudokuDifficulty
import com.example.sudoluxapp.domain.sudoku.SudokuPuzzle
import com.example.sudoluxapp.testutil.SudokuTestFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SudokuGameStateTest {
    private val puzzle = SudokuTestFixtures.puzzle

    @Test
    fun givenCellCannotBeChangedOrErased() {
        val initial = SudokuGameState(puzzle).select(0)
        assertEquals(initial, initial.enter(4))
        assertEquals(initial, initial.erase())
        assertEquals(5, initial.values[0])
    }

    @Test
    fun correctNumberIsPlacedAndUndoRestoresEmptyCell() {
        val played = SudokuGameState(puzzle).select(2).enter(4)
        assertEquals(4, played.values[2])
        assertTrue(played.notes[2].isEmpty())
        val undone = played.undo()
        assertEquals(0, undone.values[2])
        assertTrue(undone.history.isEmpty())
    }

    @Test
    fun notesToggleEraseAndUndoWork() {
        var state = SudokuGameState(puzzle).select(2).toggleNotes().enter(4).enter(1)
        assertEquals(setOf(1, 4), state.notes[2])
        state = state.enter(4)
        assertEquals(setOf(1), state.notes[2])
        val erased = state.erase()
        assertTrue(erased.notes[2].isEmpty())
        assertEquals(setOf(1), erased.undo().notes[2])
    }

    @Test
    fun impossibleNoteIsBlockedWithoutCountingAnErrorOrHistoryAction() {
        val state = SudokuGameState(puzzle).select(2).toggleNotes().enter(3)

        assertTrue(state.notes[2].isEmpty())
        assertEquals(0, state.errors)
        assertTrue(state.history.isEmpty())
        assertTrue(state.feedback.orEmpty().contains("fila"))
    }

    @Test
    fun correctPlacementCleansPeerNotesAndUndoRestoresExactPreviousNotes() {
        val notesBefore = MutableList<Set<Int>>(SudokuPuzzle.CELL_COUNT) { emptySet() }.apply {
            this[8] = setOf(2, 4)
            this[3] = setOf(2, 4)
            this[17] = setOf(2, 5)
            this[6] = setOf(2, 7)
            this[10] = setOf(2, 8)
        }
        val state = SudokuGameState(puzzle = puzzle, notes = notesBefore, selectedCell = 8)

        val placed = state.enter(2)
        assertTrue(placed.notes[8].isEmpty())
        assertEquals(setOf(4), placed.notes[3])
        assertEquals(setOf(5), placed.notes[17])
        assertEquals(setOf(7), placed.notes[6])
        assertEquals(setOf(2, 8), placed.notes[10])

        val undone = placed.undo()
        assertEquals(state.values, undone.values)
        assertEquals(notesBefore, undone.notes)
    }

    @Test
    fun disablingAutomaticCleanupKeepsPeerNotesButClearsPlacedCellNotes() {
        val notesBefore = MutableList<Set<Int>>(SudokuPuzzle.CELL_COUNT) { emptySet() }.apply {
            this[8] = setOf(2, 4)
            this[3] = setOf(2, 4)
            this[17] = setOf(2, 5)
            this[6] = setOf(2, 7)
        }
        val state = SudokuGameState(puzzle = puzzle, notes = notesBefore, selectedCell = 8)

        val placed = state.enter(2, autoCleanNotes = false)

        assertTrue(placed.notes[8].isEmpty())
        assertEquals(setOf(2, 4), placed.notes[3])
        assertEquals(setOf(2, 5), placed.notes[17])
        assertEquals(setOf(2, 7), placed.notes[6])
    }

    @Test
    fun hidingImmediateErrorsStillCountsThemAndPreservesTheLimit() {
        val state = SudokuGameState(puzzle).select(2)

        val updated = state.enter(1, showErrorsImmediately = false)

        assertEquals(1, updated.errors)
        assertTrue(updated.feedback == null)
        assertEquals(state.values, updated.values)
    }

    @Test
    fun visualHighlightPreferencesAreOutsideGameLogic() {
        val baseline = SudokuGameState(puzzle).select(2).enter(4)
        val withVisualPreferencesDisabled = SudokuGameState(puzzle).select(2).enter(4)

        assertEquals(baseline.values, withVisualPreferencesDisabled.values)
        assertEquals(baseline.notes, withVisualPreferencesDisabled.notes)
        assertEquals(baseline.errors, withVisualPreferencesDisabled.errors)
    }

    @Test
    fun selectingDefinitiveNumberActivatesMatchingValuesAndCandidates() {
        val notes = MutableList<Set<Int>>(SudokuPuzzle.CELL_COUNT) { emptySet() }.apply {
            this[10] = setOf(2, 5)
            this[20] = setOf(5)
        }

        val selected = SudokuGameState(puzzle = puzzle, notes = notes).select(0)

        assertEquals(5, selected.activeNumber)
        assertEquals(setOf(10, 20), selected.matchingCandidateCells)
    }

    @Test
    fun incorrectNumbersReachLimitAndUndoRestoresErrorCount() {
        var state = SudokuGameState(puzzle).select(2)
        state = state.enter(1).enter(2).enter(3)
        assertEquals(3, state.errors)
        assertTrue(state.hasReachedErrorLimit)
        val undone = state.undo()
        assertEquals(2, undone.errors)
        assertFalse(undone.hasReachedErrorLimit)
    }

    @Test
    fun hintUsesTheCurrentPuzzlesRealSolution() {
        val notes = MutableList<Set<Int>>(SudokuPuzzle.CELL_COUNT) { emptySet() }.apply {
            this[2] = setOf(1, 4)
            this[3] = setOf(4, 6)
        }
        val state = SudokuGameState(puzzle = puzzle, notes = notes)
        val hinted = state.hint()
        val selected = requireNotNull(hinted.selectedCell)
        assertFalse(puzzle.isGiven(selected))
        assertTrue(hinted.feedback.orEmpty().contains(puzzle.solution[selected].toString()))
        assertEquals(1, hinted.hintsUsed)
        assertEquals("Pistas 2/3", hinted.hintLabel)
        assertEquals(state.values, hinted.values)
        assertEquals(notes, hinted.notes)
    }

    @Test
    fun revealingHintPlacesNumberAndCleansRelatedCandidates() {
        val notes = MutableList<Set<Int>>(SudokuPuzzle.CELL_COUNT) { emptySet() }.apply {
            this[8] = setOf(2, 4)
            this[3] = setOf(2, 4)
            this[17] = setOf(2, 5)
            this[6] = setOf(2, 7)
            this[10] = setOf(2, 8)
        }
        val hinted = SudokuGameState(puzzle = puzzle, notes = notes, selectedCell = 8)
            .hint(revealNumber = true)

        assertEquals(2, hinted.values[8])
        assertTrue(hinted.notes[8].isEmpty())
        assertEquals(setOf(4), hinted.notes[3])
        assertEquals(setOf(5), hinted.notes[17])
        assertEquals(setOf(7), hinted.notes[6])
        assertEquals(setOf(2, 8), hinted.notes[10])
    }

    @Test
    fun placingLastCorrectNumberTriggersPerfectVictory() {
        val lastEditable = puzzle.initialBoard.indexOfLast { it == 0 }
        val almostSolved = SudokuGameState(
            puzzle = puzzle,
            values = puzzle.solution.toMutableList().apply { this[lastEditable] = 0 },
            selectedCell = lastEditable
        )
        val completed = almostSolved.enter(puzzle.solution[lastEditable])
        assertTrue(completed.showVictory)
        assertEquals(puzzle.solution, completed.values)
        assertEquals(0, completed.errors)
    }

    @Test
    fun completingNinthAppearanceDisablesNumberAndEraseReactivatesIt() {
        val editableFour = editableIndexFor(4)
        val almostComplete = SudokuGameState(
            puzzle = puzzle,
            values = puzzle.solution.toMutableList().apply { this[editableFour] = 0 },
            selectedCell = editableFour
        )
        val completed = almostComplete.enter(4)
        assertTrue(completed.isNumberCompleted(4))
        val erased = completed.copy(showVictory = false).erase()
        assertFalse(erased.isNumberCompleted(4))
    }

    @Test
    fun retryResetsProgressButKeepsPuzzleAndMode() {
        val played = SudokuGameState(puzzle, mode = GameMode.WITH_HINTS)
            .select(2).enter(1).toggleNotes().hint()
        val retried = played.retry()
        assertEquals(puzzle, retried.puzzle)
        assertNotEquals(played.gameId, retried.gameId)
        assertEquals(GameMode.WITH_HINTS, retried.mode)
        assertEquals(puzzle.initialBoard, retried.values)
        assertEquals(0, retried.errors)
        assertEquals(0, retried.hintsUsed)
        assertTrue(retried.history.isEmpty())
        assertFalse(retried.notesMode)
    }

    @Test
    fun newGameFactoryGeneratesANewPuzzleForTheSameDifficulty() {
        val factory = SudokuGameFactory()
        val first = factory.newGame(SudokuDifficulty.HARD, seed = 111L)
        val second = factory.newGame(SudokuDifficulty.HARD, seed = 222L)
        assertNotEquals(first.puzzle.initialBoard, second.puzzle.initialBoard)
        assertEquals(SudokuDifficulty.HARD, first.puzzle.difficulty)
        assertEquals(SudokuDifficulty.HARD, second.puzzle.difficulty)
    }

    @Test
    fun entryValidationUsesTheSolutionOwnedByTheCurrentPuzzle() {
        val shiftedSolution = puzzle.solution.map { value -> value % 9 + 1 }
        val shiftedBoard = puzzle.initialBoard.map { value -> if (value == 0) 0 else value % 9 + 1 }
        val currentPuzzle = SudokuPuzzle(
            initialBoard = shiftedBoard,
            solution = shiftedSolution,
            difficulty = SudokuDifficulty.HARD,
            seed = 2L
        )
        val editable = currentPuzzle.initialBoard.indexOfFirst { it == 0 }
        val played = SudokuGameState(currentPuzzle)
            .select(editable)
            .enter(currentPuzzle.solution[editable])
        assertEquals(0, played.errors)
        assertEquals(currentPuzzle.solution[editable], played.values[editable])
        assertNotEquals(puzzle.solution[editable], played.values[editable])
    }

    @Test
    fun extraHintRequiresConfirmationAndCancelDoesNotConsumeOrDiscount() {
        var state = SudokuGameState(puzzle)
        repeat(3) { state = state.hint() }
        val xpBefore = state.potentialXp
        val requested = state.hint()
        assertTrue(requested.pendingExtraHint != null)
        assertEquals(3, requested.hintsUsed)

        val cancelled = requested.cancelExtraHint()
        assertEquals(3, cancelled.hintsUsed)
        assertEquals(xpBefore, cancelled.potentialXp)
        assertTrue(cancelled.pendingExtraHint == null)

        val confirmed = requested.confirmExtraHint()
        assertEquals(4, confirmed.hintsUsed)
        assertTrue(confirmed.pendingExtraHint == null)
        assertTrue(confirmed.potentialXp < xpBefore)
    }

    @Test
    fun noHintsModeNeverActivatesAHint() {
        val state = SudokuGameState(puzzle, mode = GameMode.NO_HINTS)
        val hinted = state.hint()
        assertEquals(0, hinted.hintsUsed)
        assertEquals("Sin pistas", hinted.hintLabel)
        assertTrue(hinted.feedback.orEmpty().contains("desactivadas"))
    }

    private fun editableIndexFor(number: Int): Int =
        puzzle.solution.indices.first { index ->
            puzzle.solution[index] == number && !puzzle.isGiven(index)
        }
}
