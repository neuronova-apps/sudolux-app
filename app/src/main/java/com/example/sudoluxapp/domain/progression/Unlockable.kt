package com.example.sudoluxapp.domain.progression

enum class UnlockableType { BACKGROUND, BOARD_STYLE, THEME, PROFILE_FRAME, NUMBER_STYLE, BADGE }

enum class RewardOrigin { EARNED_PLAYING, PREMIUM, SPECIAL }

enum class UnlockStatus { LOCKED, UNLOCKED }

sealed interface UnlockRequirement {
    data class Level(val requiredLevel: Int) : UnlockRequirement
    data class AbsoluteMastery(val requiredCount: Int) : UnlockRequirement
    data object FirstLegendMedal : UnlockRequirement
}

data class Unlockable(
    val id: String,
    val name: String,
    val type: UnlockableType,
    val requirement: UnlockRequirement,
    val origin: RewardOrigin
) {
    fun isUnlocked(progress: PlayerProgress): Boolean = when (val condition = requirement) {
        is UnlockRequirement.Level -> progress.level.level >= condition.requiredLevel
        is UnlockRequirement.AbsoluteMastery -> progress.absoluteMasteryCount >= condition.requiredCount
        UnlockRequirement.FirstLegendMedal -> progress.legendMedalCount >= 1
    }

    fun status(progress: PlayerProgress): UnlockStatus =
        if (isUnlocked(progress)) UnlockStatus.UNLOCKED else UnlockStatus.LOCKED
}

object UnlockableCatalog {
    val levelRewards = listOf(
        level("background_1", "Fondo 1", UnlockableType.BACKGROUND, 3),
        level("alternate_board", "Estilo alternativo de tablero", UnlockableType.BOARD_STYLE, 5),
        level("visual_theme_1", "Tema visual 1", UnlockableType.THEME, 10),
        level("profile_frame", "Marco de perfil", UnlockableType.PROFILE_FRAME, 15),
        level("number_style_1", "Estilo de números 1", UnlockableType.NUMBER_STYLE, 20),
        level("special_background", "Fondo especial", UnlockableType.BACKGROUND, 25),
        level("visual_badge", "Insignia visual", UnlockableType.BADGE, 30),
        level("advanced_theme", "Tema avanzado", UnlockableType.THEME, 40),
        level("master_background", "Fondo Maestro", UnlockableType.BACKGROUND, 50),
        level("grandmaster_board", "Tablero Gran maestro", UnlockableType.BOARD_STYLE, 60),
        level("elite_theme", "Tema Élite", UnlockableType.THEME, 70),
        level("advanced_special_background", "Fondo especial avanzado", UnlockableType.BACKGROUND, 80),
        level("legend_theme", "Tema Leyenda", UnlockableType.THEME, 90),
        level("sudolux_legend_theme", "Leyenda Sudolux", UnlockableType.THEME, 100)
    )

    val achievementRewards = listOf(
        Unlockable(
            id = "legend_frame",
            name = "Marco Leyenda",
            type = UnlockableType.PROFILE_FRAME,
            requirement = UnlockRequirement.FirstLegendMedal,
            origin = RewardOrigin.SPECIAL
        ),
        mastery("special_master_background", "Fondo Maestro especial", UnlockableType.BACKGROUND, 5),
        mastery("exclusive_board", "Tablero exclusivo", UnlockableType.BOARD_STYLE, 10),
        mastery("absolute_sudolux_legend_theme", "Leyenda Sudolux", UnlockableType.THEME, 25)
    )

    val all: List<Unlockable> = levelRewards + achievementRewards

    fun unlocked(progress: PlayerProgress): List<Unlockable> = all.filter { it.isUnlocked(progress) }

    private fun level(id: String, name: String, type: UnlockableType, level: Int) = Unlockable(
        id = id,
        name = name,
        type = type,
        requirement = UnlockRequirement.Level(level),
        origin = RewardOrigin.EARNED_PLAYING
    )

    private fun mastery(id: String, name: String, type: UnlockableType, count: Int) = Unlockable(
        id = id,
        name = name,
        type = type,
        requirement = UnlockRequirement.AbsoluteMastery(count),
        origin = RewardOrigin.SPECIAL
    )
}
