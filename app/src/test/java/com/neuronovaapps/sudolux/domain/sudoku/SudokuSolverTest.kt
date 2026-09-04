package com.neuronovaapps.sudolux.domain.sudoku

import com.neuronovaapps.sudolux.testutil.SudokuTestFixtures
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SudokuSolverTest {
    private val solver = SudokuSolver()
    private val solution = SudokuTestFixtures.solution

    @Test
    fun completeSolutionIsValid() {
        assertTrue(SudokuBoardValidator.isValidCompleteBoard(solution))
        assertTrue(solver.isValid(solution))
    }

    @Test
    fun completeSolutionHasValidRows() {
        assertTrue(SudokuBoardValidator.hasValidRows(solution))
    }

    @Test
    fun completeSolutionHasValidColumns() {
        assertTrue(SudokuBoardValidator.hasValidColumns(solution))
    }

    @Test
    fun completeSolutionHasValidThreeByThreeBlocks() {
        assertTrue(SudokuBoardValidator.hasValidBlocks(solution))
    }

    @Test
    fun solverResolvesAValidPuzzle() {
        assertEquals(solution, solver.solve(SudokuTestFixtures.initialBoard))
    }

    @Test
    fun solverDetectsPuzzleWithoutSolution() {
        val unsolvable = SudokuTestFixtures.initialBoard.toMutableList().apply {
            this[2] = 1
        }

        assertTrue(solver.isValid(unsolvable))
        assertNull(solver.solve(unsolvable))
        assertEquals(0, solver.countSolutions(unsolvable, limit = 2))
    }

    @Test
    fun solverStopsAtLimitForPuzzleWithMultipleSolutions() {
        val emptyBoard = List(SudokuPuzzle.CELL_COUNT) { 0 }

        assertEquals(2, solver.countSolutions(emptyBoard, limit = 2))
    }

    @Test
    fun duplicateInARowMakesBoardInvalid() {
        val invalid = SudokuTestFixtures.initialBoard.toMutableList().apply {
            this[2] = 5
        }

        assertFalse(solver.isValid(invalid))
        assertEquals(0, solver.countSolutions(invalid, limit = 2))
    }
}
