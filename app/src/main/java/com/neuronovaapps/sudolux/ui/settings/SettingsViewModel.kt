package com.neuronovaapps.sudolux.ui.settings

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.neuronovaapps.sudolux.data.SettingsRepository
import com.neuronovaapps.sudolux.data.SharedPreferencesKeyValueStorage
import com.neuronovaapps.sudolux.domain.progression.PlayerProgress
import com.neuronovaapps.sudolux.domain.settings.AppTheme
import com.neuronovaapps.sudolux.domain.settings.SudokuNumberSize
import com.neuronovaapps.sudolux.domain.settings.ThemeSelectionPolicy
import com.neuronovaapps.sudolux.domain.settings.UserSettings

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository(SharedPreferencesKeyValueStorage(application))
    private val mutableSettings = mutableStateOf(repository.load())
    val settings: State<UserSettings> = mutableSettings

    private fun update(transform: (UserSettings) -> UserSettings) {
        val updated = transform(mutableSettings.value)
        if (updated == mutableSettings.value) return
        mutableSettings.value = updated
        repository.save(updated)
    }

    fun setTheme(value: AppTheme, progress: PlayerProgress) =
        update { ThemeSelectionPolicy.select(it, value, progress) }
    fun setHighContrast(value: Boolean) = update { it.copy(highContrast = value) }
    fun setNumberSize(value: SudokuNumberSize) = update { it.copy(numberSize = value) }
    fun setReduceAnimations(value: Boolean) = update { it.copy(reduceAnimations = value) }
    fun setSoundEnabled(value: Boolean) = update { it.copy(soundEnabled = value) }
    fun setHapticsEnabled(value: Boolean) = update { it.copy(hapticsEnabled = value) }
    fun setAutoCleanNotes(value: Boolean) = update { it.copy(autoCleanNotes = value) }
    fun setShowErrorsImmediately(value: Boolean) = update { it.copy(showErrorsImmediately = value) }
    fun setHighlightMatchingNumbers(value: Boolean) = update { it.copy(highlightMatchingNumbers = value) }
    fun setHighlightRelatedArea(value: Boolean) = update { it.copy(highlightRelatedArea = value) }

    fun reset() {
        mutableSettings.value = repository.reset()
    }
}
