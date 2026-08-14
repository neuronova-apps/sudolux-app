package com.example.sudoluxapp.domain.sudoku

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SudokuCandidateHelperTest {
    @Test
    fun candidateFourIsRejectedWhenFourExistsInSameRow() {
        val board = boardWith(number = 4, atIndex = 36)

        assertFalse(SudokuCandidateHelper.isCandidateAllowed(board, cellIndex = 40, number = 4))
        assertEquals(CandidateConflict.ROW, SudokuCandidateHelper.candidateConflict(board, 40, 4))
    }

    @Test
    fun candidateFourIsRejectedWhenFourExistsInSameColumn() {
        val board = boardWith(number = 4, atIndex = 4)

        assertFalse(SudokuCandidateHelper.isCandidateAllowed(board, cellIndex = 40, number = 4))
        assertEquals(CandidateConflict.COLUMN, SudokuCandidateHelper.candidateConflict(board, 40, 4))
    }

    @Test
    fun candidateFourIsRejectedWhenFourExistsInSameBlock() {
        val board = boardWith(number = 4, atIndex = 30)

        assertFalse(SudokuCandidateHelper.isCandidateAllowed(board, cellIndex = 40, number = 4))
        assertEquals(CandidateConflict.BLOCK, SudokuCandidateHelper.candidateConflict(board, 40, 4))
    }

    @Test
    fun legalCandidateIsAllowed() {
        assertTrue(SudokuCandidateHelper.isCandidateAllowed(emptyBoard(), cellIndex = 40, number = 4))
    }

    @Test
    fun placementRemovesOnlyMatchingCandidateFromRowColumnAndBlock() {
        val notes = MutableList<Set<Int>>(SudokuPuzzle.CELL_COUNT) { emptySet() }.apply {
            this[8] = setOf(2, 4)
            this[3] = setOf(2, 4) // misma fila
            this[17] = setOf(2, 4) // misma columna
            this[6] = setOf(2, 4) // mismo bloque
            this[10] = setOf(2, 4) // sin relación
        }

        val cleaned = SudokuCandidateHelper.cleanNotesAfterPlacement(notes, cellIndex = 8, number = 2)

        assertTrue(cleaned[8].isEmpty())
        assertEquals(setOf(4), cleaned[3])
        assertEquals(setOf(4), cleaned[17])
        assertEquals(setOf(4), cleaned[6])
        assertEquals(setOf(2, 4), cleaned[10])
    }

    @Test
    fun matchingCandidateCellsAreIdentifiedForHighlighting() {
        val notes = MutableList<Set<Int>>(SudokuPuzzle.CELL_COUNT) { emptySet() }.apply {
            this[10] = setOf(2, 5)
            this[20] = setOf(5)
            this[30] = setOf(7)
        }

        assertEquals(setOf(10, 20), SudokuCandidateHelper.matchingCandidateCells(notes, 5))
        assertTrue(SudokuCandidateHelper.matchingCandidateCells(notes, null).isEmpty())
    }

    private fun emptyBoard(): List<Int> = List(SudokuPuzzle.CELL_COUNT) { 0 }

    private fun boardWith(number: Int, atIndex: Int): List<Int> =
        emptyBoard().toMutableList().apply { this[atIndex] = number }
}
