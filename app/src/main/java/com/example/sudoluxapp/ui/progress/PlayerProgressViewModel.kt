package com.example.sudoluxapp.ui.progress

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.sudoluxapp.data.SharedPreferencesKeyValueStorage
import com.example.sudoluxapp.data.SudoluxRepository
import com.example.sudoluxapp.domain.progression.GameResult
import com.example.sudoluxapp.domain.progression.PlayerProgress
import com.example.sudoluxapp.domain.progression.ProgressUpdate
import com.example.sudoluxapp.domain.progression.ProgressionCalculator
import com.example.sudoluxapp.ui.game.SudokuGameState

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

    fun applyResult(gameId: String, result: GameResult): ProgressUpdate =
        ProgressionCalculator.applyResult(mutableProgress.value, gameId, result).also { update ->
            mutableProgress.value = update.progress
            repository.saveProgress(update.progress)
            repository.clearActiveGame()
        }
}
