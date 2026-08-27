package com.example.sudoluxapp.domain.settings

import com.example.sudoluxapp.domain.progression.PlayerProgress

sealed interface ThemeUnlockRequirement {
    val description: String

    fun isMetBy(progress: PlayerProgress): Boolean

    data object AvailableFromStart : ThemeUnlockRequirement {
        override val description = "Disponible desde el inicio"

        override fun isMetBy(progress: PlayerProgress) = true
    }

    data class CompletedSudokus(val requiredCount: Int) : ThemeUnlockRequirement {
        override val description = "Completa $requiredCount Sudokus"

        override fun isMetBy(progress: PlayerProgress): Boolean =
            progress.completedSudokus >= requiredCount
    }

    data class Level(val requiredLevel: Int) : ThemeUnlockRequirement {
        override val description = "Alcanza nivel $requiredLevel"

        override fun isMetBy(progress: PlayerProgress): Boolean =
            progress.currentLevel >= requiredLevel
    }
}

enum class AppTheme(
    val displayName: String,
    val unlockRequirement: ThemeUnlockRequirement
) {
    CLASSIC("Clásico claro", ThemeUnlockRequirement.AvailableFromStart),
    NIGHT("Noche", ThemeUnlockRequirement.AvailableFromStart),
    OCEAN("Océano", ThemeUnlockRequirement.CompletedSudokus(10)),
    FOREST("Bosque", ThemeUnlockRequirement.Level(10)),
    AMBAR("Ámbar", ThemeUnlockRequirement.Level(25)),
    MASTER("Maestro", ThemeUnlockRequirement.Level(50));

    val unlockDescription: String get() = unlockRequirement.description

    fun isUnlocked(progress: PlayerProgress): Boolean = unlockRequirement.isMetBy(progress)
}

enum class SudokuNumberSize(val displayName: String) {
    SMALL("Pequeño"),
    NORMAL("Normal"),
    LARGE("Grande")
}

/** Preferencias del usuario. No contiene progreso ni estado de una partida. */
data class UserSettings(
    val theme: AppTheme = AppTheme.CLASSIC,
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

object ThemeSelectionPolicy {
    fun select(
        settings: UserSettings,
        requestedTheme: AppTheme,
        progress: PlayerProgress
    ): UserSettings = if (requestedTheme.isUnlocked(progress)) {
        settings.copy(theme = requestedTheme)
    } else {
        settings
    }
}
