package com.example.sudoluxapp.domain.sudoku

enum class SudokuDifficulty(
    val displayName: String,
    val provisionalTargetClues: IntRange,
    val provisionalComplexityRank: Int
) {
    EASY("Fácil", 42..46, 1),
    MEDIUM("Medio", 36..41, 2),
    HARD("Difícil", 32..35, 3),
    EXPERT("Experto", 28..31, 4),
    MASTER("Maestro", 24..27, 5);

    companion object {
        fun fromDisplayName(value: String): SudokuDifficulty =
            entries.firstOrNull { it.displayName.equals(value, ignoreCase = true) } ?: EASY
    }
}

data class SudokuPuzzle(
    val initialBoard: List<Int>,
    val solution: List<Int>,
    val difficulty: SudokuDifficulty,
    val seed: Long,
    val initialClueCount: Int = initialBoard.count { it != 0 }
) {
    init {
        require(initialBoard.size == CELL_COUNT) { "El tablero inicial debe tener 81 celdas." }
        require(solution.size == CELL_COUNT) { "La solución debe tener 81 celdas." }
        require(initialBoard.all { it in 0..SIDE }) { "El tablero inicial solo admite valores de 0 a 9." }
        require(solution.all { it in 1..SIDE }) { "La solución solo admite valores de 1 a 9." }
        require(initialClueCount == initialBoard.count { it != 0 }) {
            "La cantidad de pistas no coincide con el tablero inicial."
        }
    }

    fun isGiven(index: Int): Boolean = index in initialBoard.indices && initialBoard[index] != 0

    companion object {
        const val SIDE = 9
        const val CELL_COUNT = SIDE * SIDE
    }
}
