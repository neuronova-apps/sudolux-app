package com.neuronovaapps.sudolux.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.neuronovaapps.sudolux.domain.settings.AppTheme
import com.neuronovaapps.sudolux.domain.settings.UserSettings

private val CommonDarkErrors = darkColorScheme(
    error = Color(0xFFFFB8C5),
    onError = Color(0xFF5B1127),
    errorContainer = Color(0xFF3A2631),
    onErrorContainer = Color(0xFFFFD9E0)
)

private val CommonLightErrors = lightColorScheme(
    error = Color(0xFFB3263E),
    onError = Color.White,
    errorContainer = Color(0xFFFFE8EC),
    onErrorContainer = Color(0xFF7A1F32)
)

private val HighContrastDarkColorScheme = darkColorScheme(
    primary = Color(0xFFB9E2FF),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF00466B),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFB9E2FF),
    onSecondary = Color.Black,
    error = Color(0xFFFFC2CC),
    onError = Color(0xFF4D0019),
    errorContainer = Color(0xFF470018),
    onErrorContainer = Color.White,
    background = Color(0xFF02070C),
    onBackground = Color.White,
    surface = Color(0xFF081827),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF142F46),
    onSurfaceVariant = Color(0xFFEAF5FF),
    outline = Color.White,
    outlineVariant = Color(0xFFB9D3E8)
)

private val HighContrastLightColorScheme = lightColorScheme(
    primary = Color(0xFF003F63),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFC7E8FF),
    onPrimaryContainer = Color.Black,
    secondary = Color(0xFF004E65),
    onSecondary = Color.White,
    error = Color(0xFF6B001E),
    onError = Color.White,
    errorContainer = Color(0xFFFFDDE4),
    onErrorContainer = Color(0xFF540016),
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color(0xFFE1F1FC),
    onSurfaceVariant = Color(0xFF172A37),
    outline = Color.Black,
    outlineVariant = Color(0xFF344B5A)
)

enum class SudoluxBackgroundRole { HOME, GAME, SECONDARY }

data class ResolvedSudoluxTheme(
    val config: SudoluxThemeConfig,
    val colors: ColorScheme
)

val LocalSudoluxThemeConfig = staticCompositionLocalOf {
    SudoluxThemeCatalog[AppTheme.CLASSIC]
}

@Composable
fun sudoluxScreenContainerColor(): Color =
    if (LocalSudoluxThemeConfig.current.drawables == null) {
        MaterialTheme.colorScheme.background
    } else {
        Color.Transparent
    }

fun resolveSudoluxTheme(settings: UserSettings): ResolvedSudoluxTheme {
    val config = SudoluxThemeCatalog[settings.theme]
    val base = config.palette.toColorScheme()
    val colors = when {
        settings.highContrast && settings.theme == AppTheme.NIGHT -> HighContrastDarkColorScheme
        settings.highContrast && settings.theme == AppTheme.CLASSIC -> HighContrastLightColorScheme
        settings.highContrast -> base.highContrast(config.palette.isDark)
        else -> base
    }
    return ResolvedSudoluxTheme(config, colors)
}

private fun SudoluxThemePalette.toColorScheme(): ColorScheme {
    val errors = if (isDark) CommonDarkErrors else CommonLightErrors
    return if (isDark) {
        darkColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            secondary = secondary,
            onSecondary = onSecondary,
            error = errors.error,
            onError = errors.onError,
            errorContainer = errors.errorContainer,
            onErrorContainer = errors.onErrorContainer,
            background = background,
            onBackground = onBackground,
            surface = surface,
            onSurface = onSurface,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = onSurfaceVariant,
            outline = outline,
            outlineVariant = outlineVariant
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = onPrimary,
            primaryContainer = primaryContainer,
            onPrimaryContainer = onPrimaryContainer,
            secondary = secondary,
            onSecondary = onSecondary,
            error = errors.error,
            onError = errors.onError,
            errorContainer = errors.errorContainer,
            onErrorContainer = errors.onErrorContainer,
            background = background,
            onBackground = onBackground,
            surface = surface,
            onSurface = onSurface,
            surfaceVariant = surfaceVariant,
            onSurfaceVariant = onSurfaceVariant,
            outline = outline,
            outlineVariant = outlineVariant
        )
    }
}

private fun ColorScheme.highContrast(dark: Boolean): ColorScheme = copy(
    onBackground = if (dark) Color.White else Color.Black,
    onSurface = if (dark) Color.White else Color.Black,
    onSurfaceVariant = if (dark) Color.White else Color.Black,
    outline = if (dark) Color.White else Color.Black,
    outlineVariant = if (dark) Color(0xFFD6E6F0) else Color(0xFF27352C)
)

@Composable
fun SudoluxAppTheme(
    settings: UserSettings = UserSettings.Default,
    content: @Composable () -> Unit
) {
    val resolved = resolveSudoluxTheme(settings)
    CompositionLocalProvider(LocalSudoluxThemeConfig provides resolved.config) {
        MaterialTheme(
            colorScheme = resolved.colors,
            typography = Typography,
            content = content
        )
    }
}

@Composable
fun SudoluxThemeBackground(
    role: SudoluxBackgroundRole,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val config = LocalSudoluxThemeConfig.current
    val drawable = config.drawables?.backgrounds?.let { backgrounds ->
        when (role) {
            SudoluxBackgroundRole.HOME -> backgrounds.home
            SudoluxBackgroundRole.GAME -> backgrounds.game
            SudoluxBackgroundRole.SECONDARY -> backgrounds.secondary
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("sudolux_theme_root_${config.theme.name}")
            .semantics {
                contentDescription = "Tema visual ${config.theme.displayName}, fondo ${role.name.lowercase()}"
            }
    ) {
        if (drawable != null) {
            Image(
                painter = painterResource(drawable),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(config.palette.backgroundScrim)
            )
        }
        content()
    }
}
