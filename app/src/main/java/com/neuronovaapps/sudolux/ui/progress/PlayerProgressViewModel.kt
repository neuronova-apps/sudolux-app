package com.neuronovaapps.sudolux.ui.progress

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.neuronovaapps.sudolux.data.SharedPreferencesKeyValueStorage
import com.neuronovaapps.sudolux.data.SudoluxRepository
import com.neuronovaapps.sudolux.domain.progression.BoardStyle
import com.neuronovaapps.sudolux.domain.progression.GameResult
import com.neuronovaapps.sudolux.domain.progression.PlayerProgress
import com.neuronovaapps.sudolux.domain.progression.ProgressUpdate
import com.neuronovaapps.sudolux.domain.progression.ProgressionCalculator
import com.neuronovaapps.sudolux.ui.game.SudokuGameState

/** Fuente de verdad observable, hidratada y respaldada por almacenamiento permanente. */
class PlayerProgressViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SudoluxRepository(SharedPreferencesKeyValueStorage(application))

    private val mutableProgress = mutableStateOf(repository.loadProgress())
    val progress: State<PlayerProgress> = mutableProgress

    private val mutableActiveGame = mutableStateOf(repository.loadActiveGame())
    val activeGame: State<SudokuGameState?> = mutableActiveGame

    fun updateGame(game: SudokuGameState) {
        mutableActiveGame.value = game
        repository.saveActiveGame(game)
    }

    fun discardActiveGame() {
        mutableActiveGame.value = null
        repository.clearActiveGame()
    }

    fun selectBoardStyle(boardStyle: BoardStyle) {
        val updated = mutableProgress.value.selectBoardStyle(boardStyle)
        if (updated == mutableProgress.value) return
        mutableProgress.value = updated
        repository.saveProgress(updated)
    }

    fun applyResult(gameId: String, result: GameResult): ProgressUpdate =
        ProgressionCalculator.applyResult(mutableProgress.value, gameId, result).also { update ->
            mutableProgress.value = update.progress
            repository.saveProgress(update.progress)
            repository.clearActiveGame()
        }
}
