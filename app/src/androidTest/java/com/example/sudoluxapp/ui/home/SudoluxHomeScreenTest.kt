package com.example.sudoluxapp.ui.home

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.sudoluxapp.domain.progression.BoardStyle
import com.example.sudoluxapp.domain.progression.GameMode
import com.example.sudoluxapp.domain.progression.PlayerLevelCalculator
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

    @Test
    fun homeAndSummaryUseTheSameDerivedProfileFrame() {
        val progress = PlayerProgress(totalXp = xpToReach(15))
        var showProgress by mutableStateOf(false)
        composeRule.setContent {
            SudoluxAppTheme {
                if (showProgress) {
                    SudoluxProgressScreen(
                        uiState = ProgressScreenPresenter.present(progress),
                        onHome = { },
                        onPlay = { },
                        onBack = { }
                    )
                } else {
                    SudoluxHomeScreen(playerProgress = progress)
                }
            }
        }

        composeRule.onNodeWithContentDescription("Marco avanzado I, nivel 15").assertExists()
        composeRule.runOnIdle { showProgress = true }
        composeRule.onNodeWithContentDescription("Marco avanzado I, nivel 15").assertExists()
    }

    @Test
    fun summaryShowsOnlyPendingAchievementsAndRemovesLegacyNextUnlock() {
        val progress = PlayerProgress(
            totalXp = xpToReach(15),
            medalCounts = com.example.sudoluxapp.domain.progression.Medal.entries
                .associateWith { medal ->
                    if (medal == com.example.sudoluxapp.domain.progression.Medal.BRONZE) 1 else 0
                }
        )
        composeRule.setContent {
            SudoluxAppTheme {
                SudoluxProgressScreen(
                    uiState = ProgressScreenPresenter.present(progress),
                    onHome = { },
                    onPlay = { },
                    onBack = { }
                )
            }
        }

        composeRule.onNodeWithText("Logros por desbloquear").performScrollTo().assertExists()
        composeRule.onNodeWithContentDescription("Avanzado, Alcanzar nivel 30, Pendiente")
            .assertExists()
        composeRule.onNodeWithText("Primer paso").assertDoesNotExist()
        composeRule.onNodeWithText("Ascenso").assertDoesNotExist()
        composeRule.onNodeWithText("Próximo desbloqueo").assertDoesNotExist()
        composeRule.onNodeWithText("Estilo de números 1").assertDoesNotExist()
    }

    @Test
    fun boardCustomizationShowsAllStylesAndLockedStylesCannotBeSelected() {
        val progress = PlayerProgress(totalXp = xpToReach(30))
        var selectedStyle: BoardStyle? = null
        composeRule.setContent {
            SudoluxAppTheme {
                SudoluxProgressScreen(
                    uiState = ProgressScreenPresenter.present(progress),
                    onHome = { },
                    onPlay = { },
                    onBack = { },
                    onBoardStyleSelected = { selectedStyle = it }
                )
            }
        }

        composeRule.onNodeWithContentDescription("Sección Logros").performClick()
        val scrollable = composeRule.onNode(hasScrollAction())
        val expectedStates = listOf(
            Triple("Predeterminado del tema", "Disponible desde el inicio", "Seleccionado"),
            Triple("Tablero alternativo", "Nivel 5", "Disponible"),
            Triple("Tablero avanzado", "Nivel 25", "Disponible"),
            Triple("Tablero experto", "Nivel 40", "Bloqueado"),
            Triple("Tablero Gran maestro", "Nivel 60", "Bloqueado"),
            Triple("Tablero exclusivo", "Maestría absoluta ×10", "Bloqueado")
        )
        expectedStates.forEach { (name, requirement, state) ->
            scrollable.performScrollToNode(hasContentDescription("$name,", substring = true))
            composeRule.onNodeWithContentDescription("$name, $requirement, $state").assertExists()
        }

        scrollable.performScrollToNode(hasContentDescription("Tablero experto,", substring = true))
        composeRule.onNodeWithContentDescription("Tablero experto,", substring = true)
            .assertIsNotEnabled()
        composeRule.runOnIdle { assertEquals(null, selectedStyle) }

        scrollable.performScrollToNode(hasContentDescription("Tablero avanzado,", substring = true))
        composeRule.onNodeWithContentDescription("Tablero avanzado,", substring = true).performClick()
        composeRule.runOnIdle { assertEquals(BoardStyle.ADVANCED, selectedStyle) }
    }

    @Test
    fun progressStatisticsSectionShowsEveryDifficultyAndBothHintColumns() {
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

        composeRule.onNodeWithContentDescription("Sección Estadísticas").performClick()

        SudokuDifficulty.entries.forEach { difficulty ->
            composeRule.onNodeWithContentDescription(
                "${difficulty.displayName}, sin pistas 0, con pistas 0, total 0"
            ).assertExists()
        }
        composeRule.onNodeWithContentDescription("Total, sin pistas 0, con pistas 0, total 0")
            .assertExists()
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

    private fun xpToReach(level: Int): Int =
        (1 until level).sumOf(PlayerLevelCalculator::xpRequiredForNextLevel)
}
