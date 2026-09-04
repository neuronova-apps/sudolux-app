package com.neuronovaapps.sudolux.domain.sudoku

import kotlin.random.Random

class SudokuGenerator(
    private val solver: SudokuSolver = SudokuSolver(),
    private val validator: SudokuPuzzleValidator = SudokuPuzzleValidator(solver)
) {
    fun generate(difficulty: SudokuDifficulty): SudokuPuzzle =
        generate(difficulty, Random.Default.nextLong())

    fun generate(difficulty: SudokuDifficulty, seed: Long): SudokuPuzzle {
        var attemptSeed = seed
        var lastValidationErrors: List<String> = emptyList()

        repeat(MAX_GENERATION_ATTEMPTS) {
            val random = Random(attemptSeed)
            val solution = generateCompleteSolution(random)
            val targetClues = random.nextInt(
                difficulty.provisionalTargetClues.first,
                difficulty.provisionalTargetClues.last + 1
            )
            val initialBoard = removeClues(solution, targetClues, random)
            val puzzle = SudokuPuzzle(
                initialBoard = initialBoard.toList(),
                solution = solution.toList(),
                difficulty = difficulty,
                seed = attemptSeed
            )
            val validation = validator.validate(puzzle)
            if (validation.isValid) return puzzle
            lastValidationErrors = validation.errors
            attemptSeed = random.nextLong()
        }

        error("No se pudo generar un Sudoku válido: ${lastValidationErrors.joinToString()}")
    }

    internal fun generateCompleteSolution(seed: Long): List<Int> =
        generateCompleteSolution(Random(seed)).toList()

    private fun generateCompleteSolution(random: Random): IntArray {
        val rows = shuffledGroups(random)
        val columns = shuffledGroups(random)
        val digits = (1..SudokuPuzzle.SIDE).shuffled(random)
        return IntArray(SudokuPuzzle.CELL_COUNT) { index ->
            val row = rows[index / SudokuPuzzle.SIDE]
            val column = columns[index % SudokuPuzzle.SIDE]
            digits[pattern(row, column)]
        }
    }

    private fun removeClues(solution: IntArray, targetClues: Int, random: Random): IntArray {
        val puzzle = solution.copyOf()
        var clueCount = SudokuPuzzle.CELL_COUNT

        for (index in puzzle.indices.shuffled(random)) {
            if (clueCount <= targetClues) break
            val clue = puzzle[index]
            puzzle[index] = 0
            if (solver.countSolutions(puzzle.toList(), limit = 2) == 1) {
                clueCount--
            } else {
                puzzle[index] = clue
            }
        }
        return puzzle
    }

    private fun shuffledGroups(random: Random): List<Int> =
        (0 until 3).shuffled(random).flatMap { group ->
            (0 until 3).shuffled(random).map { offset -> group * 3 + offset }
        }

    private fun pattern(row: Int, column: Int): Int =
        (3 * (row % 3) + row / 3 + column) % SudokuPuzzle.SIDE

    private companion object {
        const val MAX_GENERATION_ATTEMPTS = 4
    }
}
