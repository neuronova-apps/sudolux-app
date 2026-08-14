package com.example.sudoluxapp.domain.sudoku

object SudokuBoardValidator {
    fun isValidPartialBoard(board: List<Int>): Boolean =
        board.size == SudokuPuzzle.CELL_COUNT &&
            board.all { it in 0..SudokuPuzzle.SIDE } &&
            hasValidRows(board) &&
            hasValidColumns(board) &&
            hasValidBlocks(board)

    fun isValidCompleteBoard(board: List<Int>): Boolean =
        board.size == SudokuPuzzle.CELL_COUNT &&
            board.all { it in 1..SudokuPuzzle.SIDE } &&
            hasValidRows(board) &&
            hasValidColumns(board) &&
            hasValidBlocks(board)

    fun hasValidRows(board: List<Int>): Boolean {
        if (board.size != SudokuPuzzle.CELL_COUNT) return false
        return (0 until SudokuPuzzle.SIDE).all { row ->
            hasNoRepeatedValues((0 until SudokuPuzzle.SIDE).map { column -> board[row * 9 + column] })
        }
    }

    fun hasValidColumns(board: List<Int>): Boolean {
        if (board.size != SudokuPuzzle.CELL_COUNT) return false
        return (0 until SudokuPuzzle.SIDE).all { column ->
            hasNoRepeatedValues((0 until SudokuPuzzle.SIDE).map { row -> board[row * 9 + column] })
        }
    }

    fun hasValidBlocks(board: List<Int>): Boolean {
        if (board.size != SudokuPuzzle.CELL_COUNT) return false
        return (0 until SudokuPuzzle.SIDE step 3).all { firstRow ->
            (0 until SudokuPuzzle.SIDE step 3).all { firstColumn ->
                val values = buildList {
                    repeat(3) { rowOffset ->
                        repeat(3) { columnOffset ->
                            add(board[(firstRow + rowOffset) * 9 + firstColumn + columnOffset])
                        }
                    }
                }
                hasNoRepeatedValues(values)
            }
        }
    }

    private fun hasNoRepeatedValues(values: List<Int>): Boolean {
        var seen = 0
        for (value in values) {
            if (value == 0) continue
            if (value !in 1..SudokuPuzzle.SIDE) return false
            val bit = 1 shl value
            if (seen and bit != 0) return false
            seen = seen or bit
        }
        return true
    }
}
