package com.example.sudoluxapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.example.sudoluxapp.domain.premium.PremiumFeatureFlags
import com.example.sudoluxapp.domain.progression.GameMode
import com.example.sudoluxapp.ui.game.SudokuGameScreen
import com.example.sudoluxapp.ui.home.SudoluxHomeScreen
import com.example.sudoluxapp.ui.home.SudoluxIntroScreen
import com.example.sudoluxapp.ui.navigation.SudoluxAppScreen
import com.example.sudoluxapp.ui.progress.PlayerProgressViewModel
import com.example.sudoluxapp.ui.progress.ProgressScreenPresenter
import com.example.sudoluxapp.ui.progress.SudoluxProgressScreen
import com.example.sudoluxapp.ui.settings.SettingsViewModel
import com.example.sudoluxapp.ui.settings.SudoluxAboutScreen
import com.example.sudoluxapp.ui.settings.SudoluxSettingsScreen
import com.example.sudoluxapp.ui.theme.SudoluxAppTheme
import com.example.sudoluxapp.ui.theme.SudoluxBackgroundRole
import com.example.sudoluxapp.ui.theme.SudoluxThemeBackground

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val appViewModel = ViewModelProvider(this)[PlayerProgressViewModel::class.java]
        val settingsViewModel = ViewModelProvider(this)[SettingsViewModel::class.java]
        setContent {
            val settings by settingsViewModel.settings
            SudoluxAppTheme(settings = settings) {
                var showIntro by rememberSaveable { mutableStateOf(true) }
                var currentScreen by rememberSaveable { mutableStateOf(SudoluxAppScreen.HOME) }
                var playRequest by rememberSaveable { mutableIntStateOf(0) }
                var selectedDifficulty by rememberSaveable { mutableStateOf("Difícil") }
                var selectedMode by rememberSaveable { mutableStateOf(GameMode.WITH_HINTS) }
                val playerProgress by appViewModel.progress
                val currentGame by appViewModel.activeGame
                val progressUiState = remember(playerProgress) {
                    ProgressScreenPresenter.present(playerProgress)
                }
                val backgroundRole = when {
                    showIntro || currentScreen == SudoluxAppScreen.HOME -> SudoluxBackgroundRole.HOME
                    currentScreen == SudoluxAppScreen.GAME -> SudoluxBackgroundRole.GAME
                    else -> SudoluxBackgroundRole.SECONDARY
                }

                SudoluxThemeBackground(role = backgroundRole) {
                    if (showIntro) {
                        SudoluxIntroScreen(
                            onStart = {
                                showIntro = false
                                currentScreen = SudoluxAppScreen.HOME
                            }
                        )
                    } else {
                        when (currentScreen) {
                        SudoluxAppScreen.GAME -> {
                            SudokuGameScreen(
                                difficulty = currentGame?.puzzle?.difficulty?.displayName ?: selectedDifficulty,
                                mode = currentGame?.mode ?: selectedMode,
                                game = currentGame,
                                accessTier = PremiumFeatureFlags.currentTier,
                                onGameChange = appViewModel::updateGame,
                                onGameCompleted = appViewModel::applyResult,
                                settings = settings,
                                boardStyle = playerProgress.selectedBoardStyle,
                                onProgress = { currentScreen = SudoluxAppScreen.PROGRESS },
                                onBack = { currentScreen = SudoluxAppScreen.HOME }
                            )
                        }

                        SudoluxAppScreen.PROGRESS -> {
                            SudoluxProgressScreen(
                                uiState = progressUiState,
                                onHome = { currentScreen = SudoluxAppScreen.HOME },
                                onPlay = {
                                    playRequest++
                                    currentScreen = SudoluxAppScreen.HOME
                                },
                                onBoardStyleSelected = appViewModel::selectBoardStyle,
                                onBack = { currentScreen = SudoluxAppScreen.HOME }
                            )
                        }

                        SudoluxAppScreen.HOME -> {
                            val resumableGame = currentGame?.takeUnless {
                                it.showVictory || it.attemptFinished
                            }
                            SudoluxHomeScreen(
                                playerProgress = playerProgress,
                                activeGame = resumableGame,
                                playRequest = playRequest,
                                onPlayRequestHandled = { playRequest = 0 },
                                onStartGame = { difficulty, mode ->
                                    appViewModel.discardActiveGame()
                                    selectedDifficulty = difficulty
                                    selectedMode = mode
                                    currentScreen = SudoluxAppScreen.GAME
                                },
                                onContinueGame = {
                                    resumableGame?.let { game ->
                                        selectedDifficulty = game.puzzle.difficulty.displayName
                                        selectedMode = game.mode
                                        currentScreen = SudoluxAppScreen.GAME
                                    }
                                },
                                onProgress = {
                                    currentScreen = SudoluxAppScreen.PROGRESS
                                },
                                onSettings = {
                                    currentScreen = SudoluxAppScreen.SETTINGS
                                }
                            )
                        }

                        SudoluxAppScreen.SETTINGS -> {
                            SudoluxSettingsScreen(
                                settings = settings,
                                playerProgress = playerProgress,
                                onThemeChange = { theme ->
                                    settingsViewModel.setTheme(theme, playerProgress)
                                },
                                onHighContrastChange = settingsViewModel::setHighContrast,
                                onNumberSizeChange = settingsViewModel::setNumberSize,
                                onReduceAnimationsChange = settingsViewModel::setReduceAnimations,
                                onSoundChange = settingsViewModel::setSoundEnabled,
                                onHapticsChange = settingsViewModel::setHapticsEnabled,
                                onAutoCleanNotesChange = settingsViewModel::setAutoCleanNotes,
                                onShowErrorsChange = settingsViewModel::setShowErrorsImmediately,
                                onHighlightMatchingChange = settingsViewModel::setHighlightMatchingNumbers,
                                onHighlightRelatedChange = settingsViewModel::setHighlightRelatedArea,
                                onReset = settingsViewModel::reset,
                                onAbout = { currentScreen = SudoluxAppScreen.ABOUT },
                                onBack = { currentScreen = SudoluxAppScreen.HOME }
                            )
                        }

                        SudoluxAppScreen.ABOUT -> {
                            SudoluxAboutScreen(
                                onBack = { currentScreen = SudoluxAppScreen.SETTINGS }
                            )
                        }
                        }
                    }
                }
            }
        }
    }
}
