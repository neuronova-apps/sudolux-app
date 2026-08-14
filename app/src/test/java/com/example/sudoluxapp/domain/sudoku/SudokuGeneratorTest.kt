package com.example.sudoluxapp.domain.sudoku

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.system.measureNanoTime

class SudokuGeneratorTest {
    private val solver = SudokuSolver()
    private val validator = SudokuPuzzleValidator(solver)

    @Test
    fun twentyGeneratedPuzzlesAreValidUniqueAndVaried() {
        val generator = SudokuGenerator(solver, validator)
        lateinit var puzzles: List<SudokuPuzzle>
        val elapsedNanos = measureNanoTime {
            puzzles = List(SAMPLE_SIZE) { index ->
                val difficulty = SudokuDifficulty.entries[index % SudokuDifficulty.entries.size]
                generator.generate(difficulty, seed = 10_000L + index)
            }
        }

        assertEquals(SAMPLE_SIZE, puzzles.size)
        puzzles.forEach { puzzle ->
            assertEquals(SudokuPuzzle.CELL_COUNT, puzzle.initialBoard.size)
            assertEquals(SudokuPuzzle.CELL_COUNT, puzzle.solution.size)
            assertTrue(SudokuBoardValidator.isValidCompleteBoard(puzzle.solution))
            assertTrue(SudokuBoardValidator.hasValidRows(puzzle.solution))
            assertTrue(SudokuBoardValidator.hasValidColumns(puzzle.solution))
            assertTrue(SudokuBoardValidator.hasValidBlocks(puzzle.solution))
            assertTrue(puzzle.initialBoard.all { it in 0..9 })
            assertTrue(puzzle.initialBoard.indices.all { index ->
                puzzle.initialBoard[index] == 0 || puzzle.initialBoard[index] == puzzle.solution[index]
            })
            assertEquals(puzzle.initialBoard.count { it != 0 }, puzzle.initialClueCount)
            assertTrue(puzzle.initialClueCount in puzzle.difficulty.provisionalTargetClues)
            assertEquals(1, solver.countSolutions(puzzle.initialBoard, limit = 2))
            assertTrue(validator.validate(puzzle).isValid)
        }
        assertTrue(puzzles.map { it.initialBoard }.distinct().size > 1)

        val elapsedMillis = elapsedNanos / 1_000_000.0
        println(
            "Generated $SAMPLE_SIZE unique-solution Sudokus in %.2f ms (%.2f ms/puzzle)"
                .format(elapsedMillis, elapsedMillis / SAMPLE_SIZE)
        )
    }

    private companion object {
        const val SAMPLE_SIZE = 20
    }
}
