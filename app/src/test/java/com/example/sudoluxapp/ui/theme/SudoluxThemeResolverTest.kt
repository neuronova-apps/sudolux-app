package com.example.sudoluxapp.ui.theme

import com.example.sudoluxapp.domain.settings.AppTheme
import com.example.sudoluxapp.domain.settings.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SudoluxThemeResolverTest {
    @Test
    fun everySelectedThemeResolvesToItsOwnGlobalVisualConfiguration() {
        val resolved = AppTheme.entries.associateWith { theme ->
            resolveSudoluxTheme(UserSettings.Default.copy(theme = theme))
        }

        assertFalse(resolved.getValue(AppTheme.CLASSIC).config.palette.isDark)
        assertTrue(resolved.getValue(AppTheme.NIGHT).config.palette.isDark)
        assertEquals(SudoluxBackground, resolved.getValue(AppTheme.NIGHT).colors.background)
        listOf(AppTheme.OCEAN, AppTheme.FOREST, AppTheme.AMBAR, AppTheme.MASTER).forEach { theme ->
            val themed = resolved.getValue(theme)
            assertNotNull(themed.config.drawables)
            assertNotEquals(resolved.getValue(AppTheme.CLASSIC).colors.primary, themed.colors.primary)
            assertNotEquals(resolved.getValue(AppTheme.CLASSIC).colors.surface, themed.colors.surface)
        }
        assertEquals(AppTheme.entries.size, resolved.values.map { it.colors.primary }.toSet().size)
    }

    @Test
    fun highContrastKeepsTheSelectedCustomThemeIdentity() {
        val regular = resolveSudoluxTheme(UserSettings.Default.copy(theme = AppTheme.FOREST))
        val highContrast = resolveSudoluxTheme(
            UserSettings.Default.copy(theme = AppTheme.FOREST, highContrast = true)
        )

        assertEquals(regular.colors.primary, highContrast.colors.primary)
        assertEquals(regular.colors.surface, highContrast.colors.surface)
        assertNotEquals(regular.colors.outline, highContrast.colors.outline)
    }
}
