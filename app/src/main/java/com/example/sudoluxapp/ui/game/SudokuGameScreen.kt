package com.example.sudoluxapp.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoluxapp.R
import com.example.sudoluxapp.domain.progression.ExtraHintConfirmation
import com.example.sudoluxapp.domain.progression.GameMode
import com.example.sudoluxapp.domain.progression.GameResult
import com.example.sudoluxapp.domain.progression.PlayerProgress
import com.example.sudoluxapp.domain.progression.ProgressUpdate
import com.example.sudoluxapp.domain.progression.ProgressionCalculator
import com.example.sudoluxapp.domain.sudoku.SudokuDifficulty
import com.example.sudoluxapp.domain.premium.AccessTier
import com.example.sudoluxapp.domain.settings.SudokuNumberSize
import com.example.sudoluxapp.domain.settings.UserSettings
import com.example.sudoluxapp.ui.theme.SudoluxAppTheme
import com.example.sudoluxapp.ui.theme.LocalSudoluxThemeConfig
import com.example.sudoluxapp.ui.theme.forDifficulty
import com.example.sudoluxapp.ui.theme.sudoluxScreenContainerColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val GamePrimary: Color
    @Composable get() = MaterialTheme.colorScheme.primary
private val GameDeepBlue: Color
    @Composable get() = MaterialTheme.colorScheme.surface
private val GameElevatedBlue: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceVariant
private val GameBorderBlue: Color
    @Composable get() = MaterialTheme.colorScheme.outlineVariant
private val GameMutedText: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val ErrorAccent = Color(0xFFFFB8C5)

@Composable
fun SudokuGameScreen(
    difficulty: String,
    mode: GameMode,
    game: SudokuGameState?,
    accessTier: AccessTier,
    onGameChange: (SudokuGameState) -> Unit,
    onGameCompleted: (String, GameResult) -> ProgressUpdate,
    settings: UserSettings,
    onProgress: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val requestedDifficulty = SudokuDifficulty.fromDisplayName(difficulty)
    val activeGame = game?.takeIf {
        it.puzzle.difficulty == requestedDifficulty && it.mode == mode
    }
    val gameFactory = remember { SudokuGameFactory() }
    var generationRequest by remember(difficulty) { mutableIntStateOf(0) }
    var isGenerating by remember(difficulty) { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val haptics = LocalHapticFeedback.current

    BackHandler(onBack = onBack)

    LaunchedEffect(requestedDifficulty, generationRequest) {
        if (activeGame == null || generationRequest > 0) {
            isGenerating = true
            val newGame = withContext(Dispatchers.Default) {
                gameFactory.newGame(requestedDifficulty, mode)
            }
            onGameChange(newGame)
            isGenerating = false
        }
    }

    if (activeGame == null || isGenerating) {
        SudokuLoadingScreen(
            difficulty = difficulty,
            reduceAnimations = settings.reduceAnimations,
            modifier = modifier
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sudoluxScreenContainerColor(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier.padding(16.dp),
                    shape = RoundedCornerShape(14.dp),
                    containerColor = GameElevatedBlue,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    actionColor = GamePrimary
                )
            }
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val onCellSelected: (Int) -> Unit = { index ->
                if (settings.hapticsEnabled) {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                onGameChange(activeGame.select(index))
            }
            val onNumber: (Int) -> Unit = { number ->
                val previousErrors = activeGame.errors
                var updatedGame = activeGame.enter(
                    number = number,
                    accessTier = accessTier,
                    autoCleanNotes = settings.autoCleanNotes,
                    showErrorsImmediately = settings.showErrorsImmediately
                )
                if (!activeGame.showVictory && updatedGame.showVictory) {
                    val result = ProgressionCalculator.result(updatedGame.performance(completed = true))
                    val progressUpdate = onGameCompleted(updatedGame.gameId, result)
                    updatedGame = updatedGame.copy(
                        completion = GameCompletion(
                            result = result,
                            levelReached = progressUpdate.progress.currentLevel
                                .takeIf { progressUpdate.leveledUp },
                            newlyUnlockedNames = progressUpdate.newlyUnlocked.map { it.name },
                            newAbsoluteMasteryAchievement =
                                progressUpdate.newAbsoluteMasteryAchievement
                        )
                    )
                }
                onGameChange(updatedGame)
                if (settings.hapticsEnabled) {
                    haptics.performHapticFeedback(
                        if (updatedGame.errors > previousErrors) HapticFeedbackType.LongPress
                        else HapticFeedbackType.TextHandleMove
                    )
                }
            }
            val performAction: ((SudokuGameState) -> SudokuGameState) -> Unit = { action ->
                if (settings.hapticsEnabled) {
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                }
                onGameChange(action(activeGame))
            }
            val isLandscape = maxWidth > maxHeight
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        horizontal = if (isLandscape) 8.dp else 14.dp,
                        vertical = if (isLandscape) 6.dp else 12.dp
                    )
            ) {
                GameContent(
                    difficulty = difficulty,
                    game = activeGame,
                    settings = settings,
                    isLandscape = isLandscape,
                    onBack = onBack,
                    onCellSelected = onCellSelected,
                    onNumber = onNumber,
                    onUndo = { performAction { it.undo() } },
                    onNotes = { performAction { it.toggleNotes() } },
                    onErase = { performAction { it.erase() } },
                    onHint = { performAction { it.hint() } },
                    onPause = { performAction { it.togglePause() } }
                )
            }
        }
    }

    if (activeGame.isPaused) {
        PauseDialog(onContinue = { onGameChange(activeGame.togglePause()) })
    }

    activeGame.pendingExtraHint?.let { confirmation ->
        ExtraHintDialog(
            confirmation = confirmation,
            onConfirm = { onGameChange(activeGame.confirmExtraHint()) },
            onCancel = { onGameChange(activeGame.cancelExtraHint()) }
        )
    }

    if (activeGame.premiumContinuationPending) {
        PremiumContinuationDialog(
            onContinue = { onGameChange(activeGame.continueWithPremiumPenalty()) },
            onFinish = { onGameChange(activeGame.finishPremiumAttempt()) }
        )
    } else if (activeGame.attemptFinished) {
        ErrorLimitDialog(
            onRetry = { onGameChange(gameFactory.retry(activeGame)) },
            onBack = onBack
        )
    }

    if (activeGame.showVictory) {
        val completion = activeGame.completion ?: GameCompletion(
            result = ProgressionCalculator.result(activeGame.performance(completed = true)),
            levelReached = null,
            newlyUnlockedNames = emptyList(),
            newAbsoluteMasteryAchievement = false
        )
        VictoryDialog(
            result = completion.result,
            levelReached = completion.levelReached,
            newlyUnlockedNames = completion.newlyUnlockedNames,
            newAbsoluteMasteryAchievement = completion.newAbsoluteMasteryAchievement,
            onNewGame = {
                isGenerating = true
                generationRequest++
            },
            onProgress = onProgress
        )
    }
}

@Composable
private fun GameContent(
    difficulty: String,
    game: SudokuGameState,
    settings: UserSettings,
    isLandscape: Boolean,
    onBack: () -> Unit,
    onCellSelected: (Int) -> Unit,
    onNumber: (Int) -> Unit,
    onUndo: () -> Unit,
    onNotes: () -> Unit,
    onErase: () -> Unit,
    onHint: () -> Unit,
    onPause: () -> Unit
) {
    if (isLandscape) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val panelMinimumWidth = 300.dp
            val contentGap = 12.dp
            val boardSize = minOf(maxHeight, (maxWidth - panelMinimumWidth - contentGap).coerceAtLeast(0.dp))
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(contentGap),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SudokuBoard(
                    game = game,
                    settings = settings,
                    onCellSelected = onCellSelected,
                    modifier = Modifier.size(boardSize)
                )
                LandscapeGamePanel(
                    difficulty = difficulty,
                    game = game,
                    onBack = onBack,
                    onNumber = onNumber,
                    onUndo = onUndo,
                    onNotes = onNotes,
                    onErase = onErase,
                    onHint = onHint,
                    onPause = onPause,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GameHeader(difficulty, game.errors, game.potentialXp, game.mode, game.hintLabel, false, onBack)
            Spacer(Modifier.height(12.dp))
            SudokuBoard(
                game = game,
                settings = settings,
                onCellSelected = onCellSelected,
                modifier = Modifier.fillMaxWidth().widthIn(max = 430.dp)
            )
            Spacer(Modifier.height(10.dp))
            FeedbackBanner(game.feedback)
            Spacer(Modifier.height(10.dp))
            GameControls(game, false, onNumber, onUndo, onNotes, onErase, onHint, onPause)
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun LandscapeGamePanel(
    difficulty: String,
    game: SudokuGameState,
    onBack: () -> Unit,
    onNumber: (Int) -> Unit,
    onUndo: () -> Unit,
    onNotes: () -> Unit,
    onErase: () -> Unit,
    onHint: () -> Unit,
    onPause: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        // 40 header + 4 + 42 stats + 4 + 152 keypad + 4 + 52 tools.
        val needsFallbackScroll = maxHeight < 298.dp
        val contentModifier = if (needsFallbackScroll) {
            Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
        } else {
            Modifier.fillMaxSize()
        }
        Column(
            modifier = contentModifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LandscapeHeader(difficulty = difficulty, onBack = onBack)
            Spacer(Modifier.height(4.dp))
            LandscapeStats(
                errors = game.errors,
                xp = game.potentialXp,
                mode = game.mode,
                hintStatus = game.hintLabel
            )
            Spacer(Modifier.height(4.dp))
            GameControls(game, true, onNumber, onUndo, onNotes, onErase, onHint, onPause)
        }
        if (game.feedback != null) {
            FeedbackBanner(
                message = game.feedback,
                compact = true,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun LandscapeHeader(difficulty: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = onBack,
            modifier = Modifier
                .size(40.dp)
                .semantics { contentDescription = "Volver a Inicio" },
            shape = CircleShape,
            color = GameDeepBlue,
            border = BorderStroke(1.dp, GameBorderBlue)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    tint = GamePrimary
                )
            }
        }
        Text(
            text = difficulty,
            modifier = Modifier.weight(1f),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = GamePrimary.copy(alpha = 0.14f),
            border = BorderStroke(1.dp, GamePrimary.copy(alpha = 0.65f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "9x9",
                    color = GamePrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    softWrap = false,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun LandscapeStats(
    errors: Int,
    xp: Int,
    mode: GameMode,
    hintStatus: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(42.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        HeaderStat("ERRORES", "$errors/3", errors > 0, true, Modifier.weight(1f))
        HeaderStat("XP POSIBLE", "$xp XP", false, true, Modifier.weight(1f))
        HeaderStat(
            if (mode == GameMode.NO_HINTS) "MODALIDAD" else "PISTAS",
            hintStatus,
            false,
            true,
            Modifier.weight(1f)
        )
    }
}

@Composable
private fun GameControls(
    game: SudokuGameState,
    isLandscape: Boolean,
    onNumber: (Int) -> Unit,
    onUndo: () -> Unit,
    onNotes: () -> Unit,
    onErase: () -> Unit,
    onHint: () -> Unit,
    onPause: () -> Unit
) {
    NumberPad(
        enabled = !game.isPaused && !game.attemptFinished &&
            !game.premiumContinuationPending && !game.showVictory,
        completedNumbers = game.completedNumbers,
        isLandscape = isLandscape,
        onNumber = onNumber
    )
    Spacer(Modifier.height(if (isLandscape) 4.dp else 12.dp))
    ToolRow(
        notesEnabled = game.notesMode,
        mode = game.mode,
        hintLabel = game.hintButtonLabel,
        isLandscape = isLandscape,
        onUndo = onUndo,
        onNotes = onNotes,
        onErase = onErase,
        onHint = onHint,
        onPause = onPause
    )
}

@Composable
private fun SudokuLoadingScreen(
    difficulty: String,
    reduceAnimations: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sudoluxScreenContainerColor(),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (reduceAnimations) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = GamePrimary.copy(alpha = 0.18f),
                    border = BorderStroke(2.dp, GamePrimary)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("9", color = GamePrimary, fontWeight = FontWeight.Black)
                    }
                }
            } else {
                CircularProgressIndicator(color = GamePrimary)
            }
            Spacer(Modifier.height(16.dp))
            Text("Generando Sudoku $difficulty…", color = GameMutedText)
        }
    }
}

@Composable
private fun GameHeader(
    difficulty: String,
    errors: Int,
    xp: Int,
    mode: GameMode,
    hintStatus: String,
    compact: Boolean,
    onBack: () -> Unit
) {
    val difficultyIcon = LocalSudoluxThemeConfig.current.drawables?.difficultyIcons
        ?.forDifficulty(SudokuDifficulty.fromDisplayName(difficulty))
    Column(verticalArrangement = Arrangement.spacedBy(if (compact) 6.dp else 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                onClick = onBack,
                modifier = Modifier
                    .size(if (compact) 40.dp else 48.dp)
                    .semantics { contentDescription = "Volver a Inicio" },
                shape = CircleShape,
                color = GameDeepBlue,
                border = BorderStroke(1.dp, GameBorderBlue)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "‹",
                        color = GamePrimary,
                        fontSize = if (compact) 28.sp else 34.sp,
                        fontWeight = FontWeight.Light
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = difficulty,
                    fontSize = if (compact) 18.sp else 21.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Sin límite de tiempo",
                    color = GameMutedText,
                    fontSize = if (compact) 11.sp else 13.sp
                )
            }
            Surface(
                modifier = Modifier.size(if (compact) 40.dp else 48.dp),
                shape = CircleShape,
                color = GamePrimary.copy(alpha = 0.14f),
                border = BorderStroke(1.dp, GamePrimary.copy(alpha = 0.65f))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (difficultyIcon == null) {
                        Text(
                            "9×9",
                            color = GamePrimary,
                            fontSize = if (compact) 11.sp else 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Image(
                            painter = painterResource(difficultyIcon),
                            contentDescription = null,
                            modifier = Modifier.size(if (compact) 34.dp else 42.dp)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HeaderStat("ERRORES", "$errors/3", errors > 0, compact, Modifier.weight(1f))
            HeaderStat("XP POSIBLE", "$xp XP", false, compact, Modifier.weight(1f))
            HeaderStat(
                if (mode == GameMode.NO_HINTS) "MODALIDAD" else "PISTAS",
                hintStatus,
                false,
                compact,
                Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HeaderStat(
    label: String,
    value: String,
    isError: Boolean,
    compact: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.heightIn(min = if (compact) 46.dp else 58.dp),
        shape = RoundedCornerShape(if (compact) 12.dp else 14.dp),
        color = GameDeepBlue,
        border = BorderStroke(1.dp, if (isError) ErrorAccent.copy(alpha = 0.7f) else GameBorderBlue)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = if (compact) 4.dp else 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                label,
                color = GameMutedText,
                fontSize = if (compact) 8.sp else 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                value,
                color = if (isError) ErrorAccent else GamePrimary,
                fontSize = if (compact) 13.sp else 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SudokuBoard(
    game: SudokuGameState,
    settings: UserSettings,
    onCellSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedIndex = game.selectedCell
    val activeNumber = game.activeNumber
    val thinLine = if (settings.highContrast) MaterialTheme.colorScheme.outline else GameBorderBlue
    val strongLine = if (settings.highContrast) MaterialTheme.colorScheme.onSurface else GamePrimary.copy(alpha = 0.9f)
    val selectionColor = GamePrimary

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(GameDeepBlue, RoundedCornerShape(4.dp))
            .border(if (settings.highContrast) 4.dp else 3.dp, strongLine, RoundedCornerShape(4.dp))
            .semantics { contentDescription = "Tablero de Sudoku de 9 filas por 9 columnas" }
    ) {
        Column(Modifier.fillMaxSize()) {
            repeat(9) { row ->
                Row(Modifier.weight(1f)) {
                    repeat(9) { column ->
                        val index = row * 9 + column
                        val selected = index == selectedIndex
                        val related = settings.highlightRelatedArea && selectedIndex?.let {
                            val selectedRow = it / 9
                            val selectedColumn = it % 9
                            row == selectedRow || column == selectedColumn ||
                                (row / 3 == selectedRow / 3 && column / 3 == selectedColumn / 3)
                        } == true
                        val sameNumber = settings.highlightMatchingNumbers &&
                            activeNumber != null && game.values[index] == activeNumber
                        SudokuCell(
                            index = index,
                            value = game.values[index],
                            notes = game.notes[index],
                            isGiven = game.isGiven(index),
                            isSelected = selected,
                            isRelated = related,
                            isSameNumber = sameNumber,
                            highlightedCandidate = activeNumber.takeIf {
                                settings.highlightMatchingNumbers && it in game.notes[index]
                            },
                            numberSize = settings.numberSize,
                            highContrast = settings.highContrast,
                            onClick = { onCellSelected(index) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                }
            }
        }
        Canvas(Modifier.fillMaxSize()) {
            val cell = size.width / 9f
            for (line in 0..9) {
                val coordinate = line * cell
                val strong = line % 3 == 0
                val stroke = when {
                    settings.highContrast && strong -> 4.dp.toPx()
                    settings.highContrast -> 1.5.dp.toPx()
                    strong -> 3.dp.toPx()
                    else -> 1.dp.toPx()
                }
                val color = if (strong) strongLine else thinLine
                drawLine(color, start = androidx.compose.ui.geometry.Offset(coordinate, 0f), end = androidx.compose.ui.geometry.Offset(coordinate, size.height), strokeWidth = stroke)
                drawLine(color, start = androidx.compose.ui.geometry.Offset(0f, coordinate), end = androidx.compose.ui.geometry.Offset(size.width, coordinate), strokeWidth = stroke)
            }
            selectedIndex?.let { index ->
                val selectionStroke = (if (settings.highContrast) 5.dp else 3.dp).toPx()
                val inset = selectionStroke / 2f
                val row = index / 9
                val column = index % 9
                drawRect(
                    color = selectionColor,
                    topLeft = androidx.compose.ui.geometry.Offset(
                        x = column * cell + inset,
                        y = row * cell + inset
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        width = cell - selectionStroke,
                        height = cell - selectionStroke
                    ),
                    style = Stroke(width = selectionStroke)
                )
            }
        }
    }
}

@Composable
private fun SudokuCell(
    index: Int,
    value: Int,
    notes: Set<Int>,
    isGiven: Boolean,
    isSelected: Boolean,
    isRelated: Boolean,
    isSameNumber: Boolean,
    highlightedCandidate: Int?,
    numberSize: SudokuNumberSize,
    highContrast: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = when {
        isSelected -> GamePrimary.copy(alpha = 0.28f)
        isSameNumber -> GamePrimary.copy(alpha = 0.20f)
        isRelated -> GameElevatedBlue
        else -> GameDeepBlue
    }
    val row = index / 9 + 1
    val column = index % 9 + 1
    val detail = when {
        value != 0 -> "número $value${if (isSameNumber) ", coincide con el número activo" else ""}"
        notes.isNotEmpty() -> buildString {
            append("notas ${notes.sorted().joinToString()}")
            highlightedCandidate?.let { append(", candidato activo $it resaltado") }
        }
        else -> "vacía"
    }

    Surface(
        onClick = onClick,
        modifier = modifier
            .semantics {
                contentDescription = "Fila $row, columna $column, $detail, ${if (isGiven) "original" else "editable"}"
                selected = isSelected
                role = Role.Button
            },
        color = background
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (value != 0) {
                Text(
                    text = value.toString(),
                    color = if (isGiven) MaterialTheme.colorScheme.onSurface else GamePrimary,
                    fontSize = when (numberSize) {
                        SudokuNumberSize.SMALL -> 17.sp
                        SudokuNumberSize.NORMAL -> 20.sp
                        SudokuNumberSize.LARGE -> 23.sp
                    },
                    fontWeight = when {
                        highContrast || isSameNumber -> FontWeight.Black
                        isGiven -> FontWeight.Bold
                        else -> FontWeight.ExtraBold
                    }
                )
            } else if (notes.isNotEmpty()) {
                CandidateGrid(notes, highlightedCandidate, numberSize, highContrast)
            }
        }
    }
}

@Composable
private fun CandidateGrid(
    notes: Set<Int>,
    highlightedNumber: Int?,
    numberSize: SudokuNumberSize,
    highContrast: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(2.dp),
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        repeat(3) { row ->
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { column ->
                    val number = row * 3 + column + 1
                    val isHighlighted = number == highlightedNumber && number in notes
                    Box(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (number in notes) number.toString() else "",
                            modifier = if (isHighlighted) {
                                Modifier
                                    .background(GamePrimary.copy(alpha = 0.18f), CircleShape)
                                    .padding(horizontal = 2.dp)
                            } else {
                                Modifier
                            },
                            color = if (isHighlighted) MaterialTheme.colorScheme.onSurface else GamePrimary,
                            fontSize = when (numberSize) {
                                SudokuNumberSize.SMALL -> 7.sp
                                SudokuNumberSize.NORMAL -> 9.sp
                                SudokuNumberSize.LARGE -> 10.sp
                            },
                            fontWeight = if (isHighlighted || highContrast) FontWeight.ExtraBold else FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            lineHeight = when (numberSize) {
                                SudokuNumberSize.SMALL -> 7.sp
                                SudokuNumberSize.NORMAL -> 9.sp
                                SudokuNumberSize.LARGE -> 10.sp
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedbackBanner(
    message: String?,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    val isError = message?.startsWith("Número incorrecto") == true
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = if (compact) 32.dp else 42.dp)
            .alpha(if (message == null) 0f else 1f),
        shape = RoundedCornerShape(12.dp),
        color = if (isError) {
            MaterialTheme.colorScheme.errorContainer
        } else {
            GamePrimary.copy(alpha = 0.12f)
        },
        border = BorderStroke(
            1.dp,
            if (isError) MaterialTheme.colorScheme.error else GamePrimary.copy(alpha = 0.55f)
        )
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = if (compact) 5.dp else 9.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = message ?: "",
                color = if (isError) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
                fontSize = if (compact) 11.sp else 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun NumberPad(
    enabled: Boolean,
    completedNumbers: Set<Int>,
    isLandscape: Boolean,
    onNumber: (Int) -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxWidth().widthIn(max = 430.dp)) {
        val columns = when {
            isLandscape -> 3
            maxWidth >= 480.dp -> 9
            else -> 5
        }
        val gap = if (isLandscape) 4.dp else 6.dp
        val buttonWidth = (maxWidth - gap * (columns - 1)) / columns
        Column(verticalArrangement = Arrangement.spacedBy(gap)) {
            (1..9).chunked(columns).forEach { rowNumbers ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(gap, Alignment.CenterHorizontally)
                ) {
                    rowNumbers.forEach { number ->
                        NumberButton(
                            number = number,
                            enabled = enabled && number !in completedNumbers,
                            completed = number in completedNumbers,
                            onClick = { onNumber(number) },
                            minimumHeight = if (isLandscape) 48.dp else 52.dp,
                            modifier = Modifier.width(buttonWidth)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberButton(
    number: Int,
    enabled: Boolean,
    completed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    minimumHeight: androidx.compose.ui.unit.Dp = 52.dp
) {
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .heightIn(min = minimumHeight)
            .alpha(if (enabled) 1f else 0.45f)
            .semantics {
                contentDescription = if (completed) {
                    "Número $number completado"
                } else {
                    "Introducir número $number"
                }
                if (!enabled) disabled()
            },
        shape = RoundedCornerShape(12.dp),
        color = GameElevatedBlue,
        border = BorderStroke(1.dp, GameBorderBlue)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(number.toString(), color = GamePrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ToolRow(
    notesEnabled: Boolean,
    mode: GameMode,
    hintLabel: String,
    isLandscape: Boolean,
    onUndo: () -> Unit,
    onNotes: () -> Unit,
    onErase: () -> Unit,
    onHint: () -> Unit,
    onPause: () -> Unit
) {
    val tools = buildList {
        add(ToolSpec("↶", "Deshacer", false, onUndo))
        add(ToolSpec("·9", "Notas", notesEnabled, onNotes))
        add(ToolSpec("⌫", "Borrar", false, onErase))
        if (mode == GameMode.WITH_HINTS) add(ToolSpec("?", hintLabel, false, onHint))
        add(ToolSpec("Ⅱ", "Pausa", false, onPause))
    }
    val columns = tools.size
    Column(
        modifier = Modifier.fillMaxWidth().widthIn(max = 430.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        tools.chunked(columns).forEach { rowTools ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                rowTools.forEach { tool ->
                    ToolButton(
                        tool.symbol,
                        tool.label,
                        tool.active,
                        tool.onClick,
                        Modifier.weight(1f),
                        compact = isLandscape
                    )
                }
                repeat(columns - rowTools.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

private data class ToolSpec(
    val symbol: String,
    val label: String,
    val active: Boolean,
    val onClick: () -> Unit
)

@Composable
private fun ToolButton(
    symbol: String,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = if (compact) 52.dp else 62.dp)
            .semantics {
                contentDescription = if (label == "Notas") {
                    "Notas, ${if (active) "activadas" else "desactivadas"}"
                } else label
                selected = active
            },
        shape = RoundedCornerShape(14.dp),
        color = if (active) GamePrimary.copy(alpha = 0.18f) else GameDeepBlue,
        border = BorderStroke(if (active) 2.dp else 1.dp, if (active) GamePrimary else GameBorderBlue)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 2.dp, vertical = if (compact) 3.dp else 7.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                symbol,
                color = GamePrimary,
                fontSize = if (compact) 16.sp else 19.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (active && label == "Notas") "Notas ✓" else label,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = if (compact) 9.sp else 10.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun PauseDialog(onContinue: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Partida en pausa", fontWeight = FontWeight.Bold) },
        text = { Text("Sin prisa. Sudolux no utiliza el tiempo para puntuar.") },
        confirmButton = {
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = GamePrimary)
            ) { Text("Continuar") }
        },
        containerColor = GameDeepBlue
    )
}

@Composable
private fun ErrorLimitDialog(onRetry: () -> Unit, onBack: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Has alcanzado el límite de 3 errores.", fontWeight = FontWeight.Bold) },
        text = { Text("Puedes reintentar el mismo tablero o volver a Inicio.") },
        confirmButton = {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = GamePrimary)
            ) { Text("Reintentar") }
        },
        dismissButton = { TextButton(onClick = onBack) { Text("Volver al inicio") } },
        containerColor = GameDeepBlue
    )
}

@Composable
private fun PremiumContinuationDialog(onContinue: () -> Unit, onFinish: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Has alcanzado el límite de 3 errores", fontWeight = FontWeight.Bold) },
        text = {
            Text(
                "Puedes continuar la partida, pero tu recompensa de XP se reducirá y ya no " +
                    "podrás obtener las medallas de mayor rendimiento."
            )
        },
        confirmButton = {
            Button(
                onClick = onContinue,
                colors = ButtonDefaults.buttonColors(containerColor = GamePrimary)
            ) { Text("Continuar con penalización") }
        },
        dismissButton = { TextButton(onClick = onFinish) { Text("Finalizar intento") } },
        containerColor = GameDeepBlue
    )
}

@Composable
private fun ExtraHintDialog(
    confirmation: ExtraHintConfirmation,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Pista extra", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Has utilizado las 3 pistas incluidas.")
                Text("XP actual: ${confirmation.currentXp}")
                Text("Esta pista descontará: ${confirmation.actualXpDiscount} XP")
                Text("XP restante: ${confirmation.xpAfterHint}")
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = GamePrimary)
            ) { Text("Usar pista") }
        },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancelar") } },
        containerColor = GameDeepBlue
    )
}

@Composable
private fun VictoryDialog(
    result: GameResult,
    levelReached: Int?,
    newlyUnlockedNames: List<String>,
    newAbsoluteMasteryAchievement: Boolean,
    onNewGame: () -> Unit,
    onProgress: () -> Unit
) {
    val rewardBadge = LocalSudoluxThemeConfig.current.drawables?.badgeReward
    AlertDialog(
        onDismissRequest = { },
        title = { Text("Sudoku completado", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (rewardBadge != null) {
                    Image(
                        painter = painterResource(rewardBadge),
                        contentDescription = null,
                        modifier = Modifier
                            .size(68.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
                Text(result.performance.difficulty.displayName, fontWeight = FontWeight.Bold)
                Text(result.performance.mode.displayName)
                Text("${result.performance.errors} errores")
                Text("${result.performance.hintsUsed} pistas utilizadas")
                if (result.performance.premiumContinuationUsed) {
                    Text("Continuación Premium utilizada", color = ErrorAccent, fontWeight = FontWeight.Bold)
                }
                Text("+${result.xpEarned} XP", color = GamePrimary, fontWeight = FontWeight.ExtraBold)
                result.medal?.let { medal ->
                    Text("Medalla ${medal.displayName}", fontWeight = FontWeight.Bold)
                }
                if (result.performance.mode == GameMode.NO_HINTS && result.performance.errors == 0) {
                    Text("Bonificación especial sin pistas", color = GamePrimary)
                }
                if (newAbsoluteMasteryAchievement) {
                    Text("Nuevo logro: Maestría absoluta", color = GamePrimary, fontWeight = FontWeight.Bold)
                }
                levelReached?.let { level ->
                    Text("Subiste al Nivel $level", color = GamePrimary, fontWeight = FontWeight.Bold)
                }
                newlyUnlockedNames.forEach { name ->
                    Text("Nuevo desbloqueo: $name", color = GamePrimary, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onNewGame,
                colors = ButtonDefaults.buttonColors(containerColor = GamePrimary)
            ) { Text("Nueva partida") }
        },
        dismissButton = { TextButton(onClick = onProgress) { Text("Ver progreso") } },
        containerColor = GameDeepBlue
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF081421, widthDp = 390, heightDp = 844)
@Composable
private fun SudokuGameScreenPreview() {
    SudoluxAppTheme {
        SudokuGameScreen(
            difficulty = "Difícil",
            mode = GameMode.WITH_HINTS,
            game = null,
            accessTier = AccessTier.FREE,
            settings = UserSettings.Default,
            onGameChange = { },
            onGameCompleted = { gameId, result ->
                ProgressionCalculator.applyResult(PlayerProgress(), gameId, result)
            },
            onProgress = { },
            onBack = { }
        )
    }
}
