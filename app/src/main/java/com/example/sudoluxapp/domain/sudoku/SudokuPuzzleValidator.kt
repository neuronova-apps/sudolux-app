package com.example.sudoluxapp.domain.sudoku

data class SudokuPuzzleValidation(
    val isValid: Boolean,
    val errors: List<String>
)

class SudokuPuzzleValidator(
    private val solver: SudokuSolver = SudokuSolver()
) {
    fun validate(puzzle: SudokuPuzzle): SudokuPuzzleValidation {
        val errors = buildList {
            if (!SudokuBoardValidator.isValidPartialBoard(puzzle.initialBoard)) {
                add("El tablero inicial viola las reglas del Sudoku.")
            }
            if (!SudokuBoardValidator.isValidCompleteBoard(puzzle.solution)) {
                add("La solución no es una cuadrícula completa válida.")
            }
            if (puzzle.initialBoard.indices.any { index ->
                    val clue = puzzle.initialBoard[index]
                    clue != 0 && clue != puzzle.solution[index]
                }
            ) {
                add("Hay pistas que no coinciden con la solución.")
            }
            if (puzzle.initialClueCount != puzzle.initialBoard.count { it != 0 }) {
                add("La cantidad declarada de pistas es incorrecta.")
            }
            if (solver.countSolutions(puzzle.initialBoard, limit = 2) != 1) {
                add("El puzzle no tiene exactamente una solución.")
            }
        }
        return SudokuPuzzleValidation(errors.isEmpty(), errors)
    }
}
