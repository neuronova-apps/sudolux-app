package com.example.sudoluxapp.ui.game

import com.example.sudoluxapp.domain.progression.GameMode
import com.example.sudoluxapp.domain.sudoku.SudokuDifficulty
import com.example.sudoluxapp.domain.sudoku.SudokuGenerator

class SudokuGameFactory(
    private val generator: SudokuGenerator = SudokuGenerator()
) {
    fun newGame(
        difficulty: SudokuDifficulty,
        mode: GameMode = GameMode.WITH_HINTS
    ): SudokuGameState = SudokuGameState(puzzle = generator.generate(difficulty), mode = mode)

    fun newGame(
        difficulty: SudokuDifficulty,
        seed: Long,
        mode: GameMode = GameMode.WITH_HINTS
    ): SudokuGameState = SudokuGameState(puzzle = generator.generate(difficulty, seed), mode = mode)

    fun retry(game: SudokuGameState): SudokuGameState = game.retry()
}
