package com.neuronovaapps.sudolux.domain.sudoku

class SudokuSolver {
    fun isValid(board: List<Int>): Boolean = SudokuBoardValidator.isValidPartialBoard(board)

    fun solve(board: List<Int>): List<Int>? {
        val search = SearchState.create(board) ?: return null
        return if (search.solveFirst()) search.cells.toList() else null
    }

    fun countSolutions(board: List<Int>, limit: Int = 2): Int {
        require(limit > 0) { "El límite debe ser mayor que cero." }
        val search = SearchState.create(board) ?: return 0
        return search.countSolutions(limit)
    }

    private class SearchState(
        val cells: IntArray,
        private val rowMasks: IntArray,
        private val columnMasks: IntArray,
        private val blockMasks: IntArray
    ) {
        fun solveFirst(): Boolean {
            val choice = chooseEmptyCell() ?: return true
            if (choice.candidateMask == 0) return false

            var candidates = choice.candidateMask
            while (candidates != 0) {
                val bit = candidates and -candidates
                val value = Integer.numberOfTrailingZeros(bit)
                place(choice.index, value, bit)
                if (solveFirst()) return true
                remove(choice.index, bit)
                candidates = candidates xor bit
            }
            return false
        }

        fun countSolutions(limit: Int): Int {
            val choice = chooseEmptyCell() ?: return 1
            if (choice.candidateMask == 0) return 0

            var total = 0
            var candidates = choice.candidateMask
            while (candidates != 0 && total < limit) {
                val bit = candidates and -candidates
                val value = Integer.numberOfTrailingZeros(bit)
                place(choice.index, value, bit)
                total += countSolutions(limit - total)
                remove(choice.index, bit)
                candidates = candidates xor bit
            }
            return total.coerceAtMost(limit)
        }

        private fun chooseEmptyCell(): CellChoice? {
            var bestIndex = -1
            var bestMask = 0
            var smallestCandidateCount = Int.MAX_VALUE

            for (index in cells.indices) {
                if (cells[index] != 0) continue
                val row = index / SudokuPuzzle.SIDE
                val column = index % SudokuPuzzle.SIDE
                val block = blockIndex(row, column)
                val mask = FULL_DIGIT_MASK and
                    (rowMasks[row] or columnMasks[column] or blockMasks[block]).inv()
                val candidateCount = Integer.bitCount(mask)
                if (candidateCount < smallestCandidateCount) {
                    bestIndex = index
                    bestMask = mask
                    smallestCandidateCount = candidateCount
                    if (candidateCount <= 1) break
                }
            }

            return if (bestIndex == -1) null else CellChoice(bestIndex, bestMask)
        }

        private fun place(index: Int, value: Int, bit: Int) {
            val row = index / SudokuPuzzle.SIDE
            val column = index % SudokuPuzzle.SIDE
            val block = blockIndex(row, column)
            cells[index] = value
            rowMasks[row] = rowMasks[row] or bit
            columnMasks[column] = columnMasks[column] or bit
            blockMasks[block] = blockMasks[block] or bit
        }

        private fun remove(index: Int, bit: Int) {
            val row = index / SudokuPuzzle.SIDE
            val column = index % SudokuPuzzle.SIDE
            val block = blockIndex(row, column)
            cells[index] = 0
            rowMasks[row] = rowMasks[row] xor bit
            columnMasks[column] = columnMasks[column] xor bit
            blockMasks[block] = blockMasks[block] xor bit
        }

        companion object {
            fun create(board: List<Int>): SearchState? {
                if (!SudokuBoardValidator.isValidPartialBoard(board)) return null

                val cells = board.toIntArray()
                val rowMasks = IntArray(SudokuPuzzle.SIDE)
                val columnMasks = IntArray(SudokuPuzzle.SIDE)
                val blockMasks = IntArray(SudokuPuzzle.SIDE)
                for (index in cells.indices) {
                    val value = cells[index]
                    if (value == 0) continue
                    val row = index / SudokuPuzzle.SIDE
                    val column = index % SudokuPuzzle.SIDE
                    val block = blockIndex(row, column)
                    val bit = 1 shl value
                    rowMasks[row] = rowMasks[row] or bit
                    columnMasks[column] = columnMasks[column] or bit
                    blockMasks[block] = blockMasks[block] or bit
                }
                return SearchState(cells, rowMasks, columnMasks, blockMasks)
            }
        }
    }

    private data class CellChoice(val index: Int, val candidateMask: Int)

    private companion object {
        const val FULL_DIGIT_MASK = 0x3FE

        fun blockIndex(row: Int, column: Int): Int = (row / 3) * 3 + column / 3
    }
}
