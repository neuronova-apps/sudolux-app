package com.example.sudoluxapp.ui.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.sudoluxapp.domain.progression.GameMode
import com.example.sudoluxapp.domain.progression.PlayerProgress
import com.example.sudoluxapp.domain.sudoku.SudokuDifficulty
import com.example.sudoluxapp.domain.sudoku.SudokuPuzzle
import com.example.sudoluxapp.ui.game.SudokuGameState
import com.example.sudoluxapp.ui.navigation.SudoluxDestination
import com.example.sudoluxapp.ui.progress.ProgressScreenPresenter
import com.example.sudoluxapp.ui.progress.SudoluxProgressScreen
import com.example.sudoluxapp.ui.theme.SudoluxAppTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SudoluxHomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun settingsGearUsesRealSettingsCallback() {
        var settingsOpened = false
        composeRule.setContent {
            SudoluxAppTheme {
                SudoluxHomeScreen(onSettings = { settingsOpened = true })
            }
        }

        composeRule.onNodeWithContentDescription("Abrir Configuración").performClick()

        composeRule.runOnIdle { assertTrue(settingsOpened) }
    }

    @Test
    fun continueCardIsOnlyShownForAnActiveGame() {
        composeRule.setContent {
            SudoluxAppTheme {
                SudoluxHomeScreen(activeGame = null)
            }
        }

        composeRule.onNodeWithContentDescription("Continuar partida activa").assertDoesNotExist()
    }

    @Test
    fun activeGameShowsContinueSummary() {
        composeRule.setContent {
            SudoluxAppTheme {
                SudoluxHomeScreen(activeGame = activeGame())
            }
        }

        composeRule.onNodeWithContentDescription("Continuar partida activa")
            .assertTextContains("Fácil")
        composeRule.onNodeWithText("1/3 errores").assertExists()
    }

    @Test
    fun newGameModalKeepsDifficultyAndSelectedMode() {
        var startedDifficulty: String? = null
        var startedMode: GameMode? = null
        composeRule.setContent {
            SudoluxAppTheme {
                SudoluxHomeScreen(
                    onStartGame = { difficulty, mode ->
                        startedDifficulty = difficulty
                        startedMode = mode
                    }
                )
            }
        }

        composeRule.onNodeWithContentDescription("Dificultad Fácil")
            .performScrollTo()
            .performClick()
        composeRule.onNodeWithText("Nueva partida")
            .performScrollTo()
            .performClick()

        composeRule.onNodeWithText("¿Cómo quieres jugar?").assertExists()
        composeRule.onNodeWithText("Dificultad: Fácil").assertExists()
        composeRule.onNodeWithContentDescription("Sin pistas", substring = true).performClick()
        composeRule.onNodeWithText("Comenzar").performClick()

        composeRule.runOnIdle {
            assertEquals("Fácil", startedDifficulty)
            assertEquals(GameMode.NO_HINTS, startedMode)
        }
    }

    @Test
    fun homeHasNoWeeklyChallengesAndBottomBarHasExactlyThreeDestinations() {
        composeRule.setContent {
            SudoluxAppTheme {
                SudoluxHomeScreen()
            }
        }

        composeRule.onNodeWithText("Retos semanales").assertDoesNotExist()
        SudoluxDestination.entries.forEach { destination ->
            composeRule.onNodeWithText(destination.label).assertExists()
        }
        assertEquals(
            listOf("Inicio", "Jugar", "Progreso"),
            SudoluxDestination.entries.map(SudoluxDestination::label)
        )
    }

    @Test
    fun progressScreenStillRenders() {
        composeRule.setContent {
            SudoluxAppTheme {
                SudoluxProgressScreen(
                    uiState = ProgressScreenPresenter.present(PlayerProgress()),
                    onHome = { },
                    onPlay = { },
                    onBack = { }
                )
            }
        }

        composeRule.onNodeWithContentDescription("Progreso, navegación principal").assertExists()
        composeRule.onNodeWithContentDescription("Nivel 1", substring = true).assertExists()
    }

    private fun activeGame(): SudokuGameState = SudokuGameState(
        puzzle = SudokuPuzzle(
            initialBoard = List(81) { 0 },
            solution = List(81) { index -> (index % 9) + 1 },
            difficulty = SudokuDifficulty.EASY,
            seed = 7L
        ),
        mode = GameMode.WITH_HINTS,
        errors = 1,
        hintsUsed = 2
    )
}
