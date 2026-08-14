package com.example.sudoluxapp.domain.settings

enum class AppTheme(val displayName: String) {
    DARK("Oscuro"),
    LIGHT("Claro"),
    SYSTEM("Sistema")
}

enum class SudokuNumberSize(val displayName: String) {
    SMALL("Pequeño"),
    NORMAL("Normal"),
    LARGE("Grande")
}

/** Preferencias del usuario. No contiene progreso ni estado de una partida. */
data class UserSettings(
    val theme: AppTheme = AppTheme.LIGHT,
    val highContrast: Boolean = false,
    val numberSize: SudokuNumberSize = SudokuNumberSize.NORMAL,
    val reduceAnimations: Boolean = false,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true,
    val autoCleanNotes: Boolean = true,
    val showErrorsImmediately: Boolean = true,
    val highlightMatchingNumbers: Boolean = true,
    val highlightRelatedArea: Boolean = true
) {
    companion object {
        val Default = UserSettings()
    }
}
