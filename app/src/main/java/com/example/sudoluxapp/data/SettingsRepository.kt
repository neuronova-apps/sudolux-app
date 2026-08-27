package com.example.sudoluxapp.data

import com.example.sudoluxapp.domain.settings.AppTheme
import com.example.sudoluxapp.domain.settings.SudokuNumberSize
import com.example.sudoluxapp.domain.settings.UserSettings

/** Persistencia exclusiva de preferencias; nunca escribe progreso ni partida activa. */
class SettingsRepository(private val storage: KeyValueStorage) {
    fun load(): UserSettings = UserSettings(
        theme = themeValueOrDefault(storage.get(THEME)),
        highContrast = booleanValueOrDefault(storage.get(HIGH_CONTRAST), false),
        numberSize = enumValueOrDefault(storage.get(NUMBER_SIZE), SudokuNumberSize.NORMAL),
        reduceAnimations = booleanValueOrDefault(storage.get(REDUCE_ANIMATIONS), false),
        soundEnabled = booleanValueOrDefault(storage.get(SOUND), true),
        hapticsEnabled = booleanValueOrDefault(storage.get(HAPTICS), true),
        autoCleanNotes = booleanValueOrDefault(storage.get(AUTO_CLEAN_NOTES), true),
        showErrorsImmediately = booleanValueOrDefault(storage.get(SHOW_ERRORS), true),
        highlightMatchingNumbers = booleanValueOrDefault(storage.get(HIGHLIGHT_MATCHES), true),
        highlightRelatedArea = booleanValueOrDefault(storage.get(HIGHLIGHT_RELATED), true)
    )

    fun save(settings: UserSettings) {
        storage.replace(
            emptySet(),
            mapOf(
                THEME to settings.theme.name,
                HIGH_CONTRAST to settings.highContrast.toString(),
                NUMBER_SIZE to settings.numberSize.name,
                REDUCE_ANIMATIONS to settings.reduceAnimations.toString(),
                SOUND to settings.soundEnabled.toString(),
                HAPTICS to settings.hapticsEnabled.toString(),
                AUTO_CLEAN_NOTES to settings.autoCleanNotes.toString(),
                SHOW_ERRORS to settings.showErrorsImmediately.toString(),
                HIGHLIGHT_MATCHES to settings.highlightMatchingNumbers.toString(),
                HIGHLIGHT_RELATED to settings.highlightRelatedArea.toString()
            )
        )
    }

    fun reset(): UserSettings = UserSettings.Default.also(::save)

    private inline fun <reified T : Enum<T>> enumValueOrDefault(value: String?, default: T): T =
        enumValues<T>().firstOrNull { it.name == value } ?: default

    private fun themeValueOrDefault(value: String?): AppTheme = when (value) {
        "LIGHT" -> AppTheme.CLASSIC
        "DARK" -> AppTheme.NIGHT
        "SYSTEM" -> AppTheme.CLASSIC
        else -> enumValueOrDefault(value, AppTheme.CLASSIC)
    }

    private fun booleanValueOrDefault(value: String?, default: Boolean): Boolean = when (value) {
        "true" -> true
        "false" -> false
        else -> default
    }

    private companion object {
        const val THEME = "settings.theme"
        const val HIGH_CONTRAST = "settings.high_contrast"
        const val NUMBER_SIZE = "settings.number_size"
        const val REDUCE_ANIMATIONS = "settings.reduce_animations"
        const val SOUND = "settings.sound"
        const val HAPTICS = "settings.haptics"
        const val AUTO_CLEAN_NOTES = "settings.auto_clean_notes"
        const val SHOW_ERRORS = "settings.show_errors"
        const val HIGHLIGHT_MATCHES = "settings.highlight_matches"
        const val HIGHLIGHT_RELATED = "settings.highlight_related"
    }
}
