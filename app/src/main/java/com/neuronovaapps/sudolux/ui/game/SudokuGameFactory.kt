package com.neuronovaapps.sudolux.ui.game

import com.neuronovaapps.sudolux.domain.progression.GameMode
import com.neuronovaapps.sudolux.domain.sudoku.SudokuDifficulty
import com.neuronovaapps.sudolux.domain.sudoku.SudokuGenerator

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
