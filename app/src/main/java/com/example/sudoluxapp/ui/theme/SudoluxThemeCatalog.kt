package com.example.sudoluxapp.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.sudoluxapp.R
import com.example.sudoluxapp.domain.settings.AppTheme

data class ThemeBackgroundResources(
    val home: Int,
    val game: Int,
    val dialog: Int,
    val popup: Int
)

data class ThemeDrawableResources(
    val backgrounds: ThemeBackgroundResources,
    val difficultyIcons: ThemeDifficultyResources,
    val badgeReward: Int,
    val active: Int,
    val icon: Int,
    val locked: Int,
    val thumbnail: Int
)

data class ThemeDifficultyResources(
    val easy: Int,
    val medium: Int,
    val hard: Int,
    val expert: Int,
    val master: Int,
    val extreme: Int
)

data class SudoluxThemeConfig(
    val theme: AppTheme,
    val previewBackground: Color,
    val previewForeground: Color,
    val drawables: ThemeDrawableResources? = null
)

/** Registro único de temas y recursos; las pantallas consumen esta tabla en vez de ramificarse. */
object SudoluxThemeCatalog {
    val all: List<SudoluxThemeConfig> = listOf(
        SudoluxThemeConfig(
            theme = AppTheme.CLASSIC,
            previewBackground = Color(0xFFF5FAFF),
            previewForeground = Color(0xFF00649A)
        ),
        SudoluxThemeConfig(
            theme = AppTheme.NIGHT,
            previewBackground = Color(0xFF081421),
            previewForeground = Color(0xFF8CC8FF)
        ),
        SudoluxThemeConfig(
            theme = AppTheme.OCEAN,
            previewBackground = Color(0xFF073B5C),
            previewForeground = Color(0xFFBCEBFF),
            drawables = ThemeDrawableResources(
                backgrounds = ThemeBackgroundResources(
                    home = R.drawable.theme_ocean_bg_home,
                    game = R.drawable.theme_ocean_bg_game,
                    dialog = R.drawable.theme_ocean_bg_dialog,
                    popup = R.drawable.theme_ocean_bg_popup
                ),
                difficultyIcons = ThemeDifficultyResources(
                    easy = R.drawable.ocean_difficulty_easy,
                    medium = R.drawable.ocean_difficulty_medium,
                    hard = R.drawable.ocean_difficulty_hard,
                    expert = R.drawable.ocean_difficulty_expert,
                    master = R.drawable.ocean_difficulty_master,
                    extreme = R.drawable.ocean_difficulty_extreme
                ),
                badgeReward = R.drawable.ocean_badge_reward,
                active = R.drawable.ocean_theme_active,
                icon = R.drawable.ocean_theme_icon,
                locked = R.drawable.ocean_theme_locked,
                thumbnail = R.drawable.ocean_theme_thumbnail
            )
        ),
        SudoluxThemeConfig(
            theme = AppTheme.FOREST,
            previewBackground = Color(0xFF173F2A),
            previewForeground = Color(0xFFC8F2D7),
            drawables = ThemeDrawableResources(
                backgrounds = ThemeBackgroundResources(
                    home = R.drawable.theme_forest_bg_home,
                    game = R.drawable.theme_forest_bg_game,
                    dialog = R.drawable.theme_forest_bg_dialog,
                    popup = R.drawable.theme_forest_bg_popup
                ),
                difficultyIcons = ThemeDifficultyResources(
                    easy = R.drawable.forest_difficulty_easy,
                    medium = R.drawable.forest_difficulty_medium,
                    hard = R.drawable.forest_difficulty_hard,
                    expert = R.drawable.forest_difficulty_expert,
                    master = R.drawable.forest_difficulty_master,
                    extreme = R.drawable.forest_difficulty_extreme
                ),
                badgeReward = R.drawable.forest_badge_reward,
                active = R.drawable.forest_theme_active,
                icon = R.drawable.forest_theme_icon,
                locked = R.drawable.forest_theme_locked,
                thumbnail = R.drawable.forest_theme_thumbnail
            )
        ),
        SudoluxThemeConfig(
            theme = AppTheme.AMBAR,
            previewBackground = Color(0xFF543811),
            previewForeground = Color(0xFFFFE0A3),
            drawables = ThemeDrawableResources(
                backgrounds = ThemeBackgroundResources(
                    home = R.drawable.theme_ambar_bg_home,
                    game = R.drawable.theme_ambar_bg_game,
                    dialog = R.drawable.theme_ambar_bg_dialog,
                    popup = R.drawable.theme_ambar_bg_popup
                ),
                difficultyIcons = ThemeDifficultyResources(
                    easy = R.drawable.ambar_difficulty_easy,
                    medium = R.drawable.ambar_difficulty_medium,
                    hard = R.drawable.ambar_difficulty_hard,
                    expert = R.drawable.ambar_difficulty_expert,
                    master = R.drawable.ambar_difficulty_master,
                    extreme = R.drawable.ambar_difficulty_extreme
                ),
                badgeReward = R.drawable.ambar_badge_reward,
                active = R.drawable.ambar_theme_active,
                icon = R.drawable.ambar_theme_icon,
                locked = R.drawable.ambar_theme_locked,
                thumbnail = R.drawable.ambar_theme_thumbnail
            )
        ),
        SudoluxThemeConfig(
            theme = AppTheme.MASTER,
            previewBackground = Color(0xFF251A46),
            previewForeground = Color(0xFFE7D9FF),
            drawables = ThemeDrawableResources(
                backgrounds = ThemeBackgroundResources(
                    home = R.drawable.theme_master_bg_home,
                    game = R.drawable.theme_master_bg_game,
                    dialog = R.drawable.theme_master_bg_dialog,
                    popup = R.drawable.theme_master_bg_popup
                ),
                difficultyIcons = ThemeDifficultyResources(
                    easy = R.drawable.master_difficulty_easy,
                    medium = R.drawable.master_difficulty_medium,
                    hard = R.drawable.master_difficulty_hard,
                    expert = R.drawable.master_difficulty_expert,
                    master = R.drawable.master_difficulty_master,
                    extreme = R.drawable.master_difficulty_extreme
                ),
                badgeReward = R.drawable.master_badge_reward,
                active = R.drawable.master_theme_active,
                icon = R.drawable.master_theme_icon,
                locked = R.drawable.master_theme_locked,
                thumbnail = R.drawable.master_theme_thumbnail
            )
        )
    )

    private val byTheme = all.associateBy(SudoluxThemeConfig::theme)

    operator fun get(theme: AppTheme): SudoluxThemeConfig = requireNotNull(byTheme[theme])
}
