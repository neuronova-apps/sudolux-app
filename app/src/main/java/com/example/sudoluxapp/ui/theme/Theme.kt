package com.example.sudoluxapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.sudoluxapp.domain.settings.AppTheme
import com.example.sudoluxapp.domain.settings.UserSettings

private val DarkColorScheme = darkColorScheme(
    primary = SudoluxPrimary,
    onPrimary = Color(0xFF003353),
    primaryContainer = SudoluxPrimaryContainer,
    onPrimaryContainer = Color(0xFFD6ECFF),
    secondary = SudoluxSecondary,
    onSecondary = Color(0xFF00354D),
    error = Color(0xFFFFB8C5),
    onError = Color(0xFF5B1127),
    errorContainer = Color(0xFF3A2631),
    onErrorContainer = Color(0xFFFFD9E0),
    background = SudoluxBackground,
    onBackground = SudoluxOnBackground,
    surface = SudoluxSurface,
    onSurface = SudoluxOnBackground,
    surfaceVariant = SudoluxSurfaceHigh,
    onSurfaceVariant = SudoluxOnSurfaceVariant,
    outline = SudoluxOutline,
    outlineVariant = SudoluxOutlineVariant
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF00649A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCDE9FF),
    onPrimaryContainer = Color(0xFF002033),
    secondary = Color(0xFF006683),
    onSecondary = Color.White,
    error = Color(0xFFB3263E),
    onError = Color.White,
    errorContainer = Color(0xFFFFE8EC),
    onErrorContainer = Color(0xFF7A1F32),
    background = Color(0xFFF5FAFF),
    onBackground = Color(0xFF0B1F2E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0B1F2E),
    surfaceVariant = Color(0xFFE3EEF7),
    onSurfaceVariant = Color(0xFF405464),
    outline = Color(0xFF607786),
    outlineVariant = Color(0xFFB9C9D5)
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

@Composable
fun SudoluxAppTheme(
    settings: UserSettings = UserSettings.Default,
    content: @Composable () -> Unit
) {
    val darkTheme = when (settings.theme) {
        AppTheme.NIGHT -> true
        AppTheme.CLASSIC,
        AppTheme.OCEAN,
        AppTheme.FOREST,
        AppTheme.AMBAR,
        AppTheme.MASTER -> false
    }
    val colors = when {
        settings.highContrast && darkTheme -> HighContrastDarkColorScheme
        settings.highContrast -> HighContrastLightColorScheme
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
