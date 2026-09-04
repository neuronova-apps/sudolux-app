package com.neuronovaapps.sudolux.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.annotation.DrawableRes
import com.neuronovaapps.sudolux.R
import com.neuronovaapps.sudolux.domain.settings.AppTheme
import com.neuronovaapps.sudolux.domain.sudoku.SudokuDifficulty

data class ThemeBackgroundResources(
    val home: Int,
    val game: Int,
    val secondary: Int,
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

@DrawableRes
fun ThemeDifficultyResources.forDifficulty(difficulty: SudokuDifficulty): Int = when (difficulty) {
    SudokuDifficulty.EASY -> easy
    SudokuDifficulty.MEDIUM -> medium
    SudokuDifficulty.HARD -> hard
    SudokuDifficulty.EXPERT -> expert
    SudokuDifficulty.MASTER -> master
}

data class SudoluxThemeConfig(
    val theme: AppTheme,
    val previewBackground: Color,
    val previewForeground: Color,
    val palette: SudoluxThemePalette,
    val drawables: ThemeDrawableResources? = null
)

data class SudoluxThemePalette(
    val isDark: Boolean,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
    val backgroundScrim: Color = Color.Transparent
)

/** Registro único de temas y recursos; las pantallas consumen esta tabla en vez de ramificarse. */
object SudoluxThemeCatalog {
    val all: List<SudoluxThemeConfig> = listOf(
        SudoluxThemeConfig(
            theme = AppTheme.CLASSIC,
            previewBackground = Color(0xFFF5FAFF),
            previewForeground = Color(0xFF00649A),
            palette = SudoluxThemePalette(
                isDark = false,
                primary = Color(0xFF00649A),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFCDE9FF),
                onPrimaryContainer = Color(0xFF002033),
                secondary = Color(0xFF006683),
                onSecondary = Color.White,
                background = Color(0xFFF5FAFF),
                onBackground = Color(0xFF0B1F2E),
                surface = Color.White,
                onSurface = Color(0xFF0B1F2E),
                surfaceVariant = Color(0xFFE3EEF7),
                onSurfaceVariant = Color(0xFF405464),
                outline = Color(0xFF607786),
                outlineVariant = Color(0xFFB9C9D5)
            )
        ),
        SudoluxThemeConfig(
            theme = AppTheme.NIGHT,
            previewBackground = Color(0xFF081421),
            previewForeground = Color(0xFF8CC8FF),
            palette = SudoluxThemePalette(
                isDark = true,
                primary = SudoluxPrimary,
                onPrimary = Color(0xFF003353),
                primaryContainer = SudoluxPrimaryContainer,
                onPrimaryContainer = Color(0xFFD6ECFF),
                secondary = SudoluxSecondary,
                onSecondary = Color(0xFF00354D),
                background = SudoluxBackground,
                onBackground = SudoluxOnBackground,
                surface = SudoluxSurface,
                onSurface = SudoluxOnBackground,
                surfaceVariant = SudoluxSurfaceHigh,
                onSurfaceVariant = SudoluxOnSurfaceVariant,
                outline = SudoluxOutline,
                outlineVariant = SudoluxOutlineVariant
            )
        ),
        SudoluxThemeConfig(
            theme = AppTheme.OCEAN,
            previewBackground = Color(0xFF073B5C),
            previewForeground = Color(0xFFBCEBFF),
            palette = SudoluxThemePalette(
                isDark = true,
                primary = Color(0xFF79D7FF),
                onPrimary = Color(0xFF003548),
                primaryContainer = Color(0xFF075578),
                onPrimaryContainer = Color(0xFFD5F3FF),
                secondary = Color(0xFF9DE4E2),
                onSecondary = Color(0xFF003736),
                background = Color(0xFF021B35),
                onBackground = Color(0xFFF1FAFF),
                surface = Color(0xF2082D48),
                onSurface = Color(0xFFF1FAFF),
                surfaceVariant = Color(0xF21A4963),
                onSurfaceVariant = Color(0xFFD0E7F2),
                outline = Color(0xFF86BBD2),
                outlineVariant = Color(0xFF3F7088),
                backgroundScrim = Color(0x5200152B)
            ),
            drawables = ThemeDrawableResources(
                backgrounds = ThemeBackgroundResources(
                    home = R.drawable.theme_ocean_bg_home,
                    game = R.drawable.theme_ocean_bg_game,
                    secondary = R.drawable.theme_ocean_bg_second,
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
            palette = SudoluxThemePalette(
                isDark = false,
                primary = Color(0xFF16633C),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFCDEED8),
                onPrimaryContainer = Color(0xFF073D22),
                secondary = Color(0xFF607B16),
                onSecondary = Color.White,
                background = Color(0xFFEAF6E9),
                onBackground = Color(0xFF102719),
                surface = Color(0xF2F8FFF7),
                onSurface = Color(0xFF102719),
                surfaceVariant = Color(0xF2DCEBDA),
                onSurfaceVariant = Color(0xFF365442),
                outline = Color(0xFF547661),
                outlineVariant = Color(0xFF9DBAA4),
                backgroundScrim = Color(0x38F5FFF3)
            ),
            drawables = ThemeDrawableResources(
                backgrounds = ThemeBackgroundResources(
                    home = R.drawable.theme_forest_bg_home,
                    game = R.drawable.theme_forest_bg_game,
                    secondary = R.drawable.theme_forest_bg_second,
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
            palette = SudoluxThemePalette(
                isDark = false,
                primary = Color(0xFF8B4D00),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFFFDDA8),
                onPrimaryContainer = Color(0xFF4A2700),
                secondary = Color(0xFF7D5700),
                onSecondary = Color.White,
                background = Color(0xFFFFF5E3),
                onBackground = Color(0xFF2C1C09),
                surface = Color(0xF2FFF9EE),
                onSurface = Color(0xFF2C1C09),
                surfaceVariant = Color(0xF2F5E3C3),
                onSurfaceVariant = Color(0xFF624B2E),
                outline = Color(0xFF826A4B),
                outlineVariant = Color(0xFFC6AA7B),
                backgroundScrim = Color(0x32FFF2D4)
            ),
            drawables = ThemeDrawableResources(
                backgrounds = ThemeBackgroundResources(
                    home = R.drawable.theme_ambar_bg_home,
                    game = R.drawable.theme_ambar_bg_game,
                    secondary = R.drawable.theme_ambar_bg_second,
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
            palette = SudoluxThemePalette(
                isDark = false,
                primary = Color(0xFF624694),
                onPrimary = Color.White,
                primaryContainer = Color(0xFFE9DDFF),
                onPrimaryContainer = Color(0xFF2A154F),
                secondary = Color(0xFF806000),
                onSecondary = Color.White,
                background = Color(0xFFFFF9F2),
                onBackground = Color(0xFF241B2D),
                surface = Color(0xF5FFFCFA),
                onSurface = Color(0xFF241B2D),
                surfaceVariant = Color(0xF2F2EAF5),
                onSurfaceVariant = Color(0xFF55495D),
                outline = Color(0xFF796C80),
                outlineVariant = Color(0xFFC8BACD),
                backgroundScrim = Color(0x24FFFDF9)
            ),
            drawables = ThemeDrawableResources(
                backgrounds = ThemeBackgroundResources(
                    home = R.drawable.theme_master_bg_home,
                    game = R.drawable.theme_master_bg_game,
                    secondary = R.drawable.theme_master_bg_second,
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
