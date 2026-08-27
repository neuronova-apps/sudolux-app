package com.example.sudoluxapp.ui.theme

import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.sudoluxapp.domain.settings.AppTheme
import com.example.sudoluxapp.domain.settings.UserSettings
import org.junit.Rule
import org.junit.Test

class SudoluxThemeApplicationTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun changingTheSelectedThemeReachesTheGlobalUiRootImmediately() {
        var settings by mutableStateOf(UserSettings.Default)
        composeRule.setContent {
            SudoluxAppTheme(settings) {
                SudoluxThemeBackground(SudoluxBackgroundRole.HOME) {
                    Text("Contenido global")
                }
            }
        }

        AppTheme.entries.forEach { theme ->
            composeRule.runOnIdle { settings = settings.copy(theme = theme) }
            composeRule.onNodeWithTag("sudolux_theme_root_${theme.name}").assertExists()
        }
    }
}
