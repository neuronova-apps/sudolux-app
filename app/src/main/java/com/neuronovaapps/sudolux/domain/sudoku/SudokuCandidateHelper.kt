package com.neuronovaapps.sudolux.domain.sudoku

enum class CandidateConflict {
    ROW,
    COLUMN,
    BLOCK
}

object SudokuCandidateHelper {
    fun isCandidateAllowed(values: List<Int>, cellIndex: Int, number: Int): Boolean =
        values.size == SudokuPuzzle.CELL_COUNT &&
            cellIndex in values.indices &&
            number in 1..SudokuPuzzle.SIDE &&
            values[cellIndex] == 0 &&
            candidateConflict(values, cellIndex, number) == null

    fun candidateConflict(
        values: List<Int>,
        cellIndex: Int,
        number: Int
    ): CandidateConflict? {
        if (values.size != SudokuPuzzle.CELL_COUNT || cellIndex !in values.indices ||
            number !in 1..SudokuPuzzle.SIDE || values[cellIndex] != 0
        ) {
            return null
        }

        val row = cellIndex / SudokuPuzzle.SIDE
        val column = cellIndex % SudokuPuzzle.SIDE
        if ((0 until SudokuPuzzle.SIDE).any { currentColumn ->
                values[row * SudokuPuzzle.SIDE + currentColumn] == number
            }
        ) {
            return CandidateConflict.ROW
        }
        if ((0 until SudokuPuzzle.SIDE).any { currentRow ->
                values[currentRow * SudokuPuzzle.SIDE + column] == number
            }
        ) {
            return CandidateConflict.COLUMN
        }

        val blockRow = row / 3 * 3
        val blockColumn = column / 3 * 3
        if ((blockRow until blockRow + 3).any { currentRow ->
                (blockColumn until blockColumn + 3).any { currentColumn ->
                    values[currentRow * SudokuPuzzle.SIDE + currentColumn] == number
                }
            }
        ) {
            return CandidateConflict.BLOCK
        }
        return null
    }

    fun cleanNotesAfterPlacement(
        notes: List<Set<Int>>,
        cellIndex: Int,
        number: Int
    ): List<Set<Int>> {
        require(notes.size == SudokuPuzzle.CELL_COUNT) { "Debe haber 81 conjuntos de notas." }
        require(cellIndex in notes.indices) { "La celda debe pertenecer al tablero." }
        require(number in 1..SudokuPuzzle.SIDE) { "El candidato debe estar entre 1 y 9." }

        return notes.mapIndexed { index, candidates ->
            when {
                index == cellIndex -> emptySet()
                arePeers(index, cellIndex) -> candidates - number
                else -> candidates
            }
        }
    }

    fun matchingCandidateCells(notes: List<Set<Int>>, number: Int?): Set<Int> {
        if (number !in 1..SudokuPuzzle.SIDE) return emptySet()
        return notes.indices.filterTo(mutableSetOf()) { index -> number in notes[index] }
    }

    private fun arePeers(firstIndex: Int, secondIndex: Int): Boolean {
        val firstRow = firstIndex / SudokuPuzzle.SIDE
        val firstColumn = firstIndex % SudokuPuzzle.SIDE
        val secondRow = secondIndex / SudokuPuzzle.SIDE
        val secondColumn = secondIndex % SudokuPuzzle.SIDE
        return firstRow == secondRow ||
            firstColumn == secondColumn ||
            (firstRow / 3 == secondRow / 3 && firstColumn / 3 == secondColumn / 3)
    }
}
