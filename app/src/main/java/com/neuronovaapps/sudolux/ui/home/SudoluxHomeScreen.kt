package com.neuronovaapps.sudolux.ui.home

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neuronovaapps.sudolux.R
import com.neuronovaapps.sudolux.domain.progression.GameMode
import com.neuronovaapps.sudolux.domain.progression.PlayerProgress
import com.neuronovaapps.sudolux.domain.progression.XpCalculator
import com.neuronovaapps.sudolux.domain.sudoku.SudokuDifficulty
import com.neuronovaapps.sudolux.ui.components.PlayerLevelFrame
import com.neuronovaapps.sudolux.ui.game.SudokuGameState
import com.neuronovaapps.sudolux.ui.navigation.SudoluxBottomBar
import com.neuronovaapps.sudolux.ui.navigation.SudoluxDestination
import com.neuronovaapps.sudolux.ui.theme.SudoluxAppTheme
import com.neuronovaapps.sudolux.ui.theme.LocalSudoluxThemeConfig
import com.neuronovaapps.sudolux.ui.theme.forDifficulty
import com.neuronovaapps.sudolux.ui.theme.sudoluxScreenContainerColor
import kotlinx.coroutines.launch

private val Primary: Color
    @Composable get() = MaterialTheme.colorScheme.primary
private val SurfaceColor: Color
    @Composable get() = MaterialTheme.colorScheme.surface
private val ElevatedSurface: Color
    @Composable get() = MaterialTheme.colorScheme.surfaceVariant
private val BorderColor: Color
    @Composable get() = MaterialTheme.colorScheme.outlineVariant
private val MutedText: Color
    @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant
private val difficulties = SudokuDifficulty.entries.map(SudokuDifficulty::displayName)

@Composable
fun SudoluxHomeScreen(
    modifier: Modifier = Modifier,
    playerProgress: PlayerProgress = PlayerProgress(),
    activeGame: SudokuGameState? = null,
    onStartGame: (String, GameMode) -> Unit = { _, _ -> },
    onContinueGame: () -> Unit = { },
    onProgress: () -> Unit = { },
    onSettings: () -> Unit = { },
    playRequest: Int = 0,
    onPlayRequestHandled: () -> Unit = { }
) {
    var selectedDifficulty by rememberSaveable { mutableStateOf(SudokuDifficulty.HARD.displayName) }
    var pendingDifficulty by remember { mutableStateOf<String?>(null) }
    var selectedMode by remember { mutableStateOf(GameMode.WITH_HINTS) }
    var showActiveGameWarning by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    fun showUpcoming(message: String) {
        coroutineScope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(message)
        }
    }

    fun requestStart() {
        if (activeGame != null) {
            showActiveGameWarning = true
        } else {
            selectedMode = GameMode.WITH_HINTS
            pendingDifficulty = selectedDifficulty
        }
    }

    LaunchedEffect(playRequest) {
        if (playRequest > 0) {
            requestStart()
            onPlayRequestHandled()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sudoluxScreenContainerColor(),
        contentWindowInsets = WindowInsets.safeDrawing,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    containerColor = ElevatedSurface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                    actionColor = Primary
                )
            }
        },
        bottomBar = {
            SudoluxBottomBar(
                selectedDestination = SudoluxDestination.HOME,
                onDestinationSelected = { destination ->
                    when (destination) {
                        SudoluxDestination.HOME -> Unit
                        SudoluxDestination.PLAY -> requestStart()
                        SudoluxDestination.PROGRESS -> onProgress()
                    }
                }
            )
        }
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val useTwoColumns = maxWidth >= 700.dp && maxWidth > maxHeight
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = if (useTwoColumns) 24.dp else 18.dp,
                    top = 12.dp,
                    end = if (useTwoColumns) 24.dp else 18.dp,
                    bottom = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { HomeHeader(onSettings) }
                if (useTwoColumns) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                PlayerProgressCard(playerProgress)
                                activeGame?.let {
                                    ContinueCard(game = it, onClick = onContinueGame)
                                }
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                LearnSudokuCard {
                                    showUpcoming("Próximamente: Aprende Sudoku")
                                }
                                GameLaunchSection(
                                    selectedDifficulty = selectedDifficulty,
                                    onDifficultySelected = { selectedDifficulty = it },
                                    onNewGame = ::requestStart
                                )
                            }
                        }
                    }
                } else {
                    item { PlayerProgressCard(playerProgress) }
                    activeGame?.let { game ->
                        item { ContinueCard(game = game, onClick = onContinueGame) }
                    }
                    item {
                        LearnSudokuCard {
                            showUpcoming("Próximamente: Aprende Sudoku")
                        }
                    }
                    item {
                        GameLaunchSection(
                            selectedDifficulty = selectedDifficulty,
                            onDifficultySelected = { selectedDifficulty = it },
                            onNewGame = ::requestStart
                        )
                    }
                }
            }
        }
    }

    pendingDifficulty?.let { difficulty ->
        ModeSelectionDialog(
            difficulty = difficulty,
            selectedMode = selectedMode,
            onModeSelected = { selectedMode = it },
            onStart = {
                pendingDifficulty = null
                onStartGame(difficulty, selectedMode)
            },
            onCancel = { pendingDifficulty = null }
        )
    }

    if (showActiveGameWarning) {
        AlertDialog(
            onDismissRequest = { showActiveGameWarning = false },
            title = { Text("Ya tienes una partida en curso", fontWeight = FontWeight.ExtraBold) },
            text = { Text("Puedes retomarla o descartarla para iniciar un Sudoku nuevo.") },
            confirmButton = {
                Button(
                    onClick = {
                        showActiveGameWarning = false
                        onContinueGame()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) { Text("Continuar partida") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showActiveGameWarning = false
                        selectedMode = GameMode.WITH_HINTS
                        pendingDifficulty = selectedDifficulty
                    }
                ) { Text("Iniciar nueva") }
            },
            containerColor = SurfaceColor
        )
    }
}

@Composable
private fun HomeHeader(onSettings: () -> Unit) {
    val themeIcon = LocalSudoluxThemeConfig.current.drawables?.icon
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Primary, MaterialTheme.colorScheme.secondary)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (themeIcon == null) {
                Text(
                    text = "S",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black
                )
            } else {
                Image(
                    painter = painterResource(themeIcon),
                    contentDescription = null,
                    modifier = Modifier.size(38.dp)
                )
            }
        }
        Spacer(Modifier.width(11.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "SUDOLUX",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
            Text(
                text = "Entrena tu lógica",
                color = MutedText,
                fontSize = 13.sp
            )
        }
        Surface(
            onClick = onSettings,
            modifier = Modifier
                .size(48.dp)
                .semantics {
                    contentDescription = "Abrir Configuración"
                    role = Role.Button
                },
            shape = CircleShape,
            color = SurfaceColor,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_settings),
                    contentDescription = null,
                    modifier = Modifier.size(23.dp),
                    tint = Primary
                )
            }
        }
    }
}

@Composable
private fun PlayerProgressCard(progress: PlayerProgress) {
    val level = progress.level
    val shape = RoundedCornerShape(20.dp)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(listOf(ElevatedSurface, SurfaceColor)),
                shape = shape
            )
            .border(1.dp, BorderColor, shape)
            .padding(15.dp)
            .semantics {
                contentDescription = if (level.level == 100) {
                    "Nivel 100, ${level.title}, ${level.totalXp} XP total, nivel máximo"
                } else {
                    "Nivel ${level.level}, ${level.title}, ${level.totalXp} XP total, " +
                        "faltan ${level.xpRemaining} XP"
                }
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PlayerLevelFrame(
                frame = progress.currentProfileFrame,
                level = level.level,
                size = 64.dp
            )
            Spacer(Modifier.width(11.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(level.title, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
                Text("${level.totalXp} XP total", color = MutedText, fontSize = 13.sp)
                Text(
                    "${progress.completedSudokus} Sudokus completados",
                    color = MutedText,
                    fontSize = 12.sp
                )
            }
            if (level.level < 100) {
                Text(
                    text = "${level.xpRemaining} XP\nrestantes",
                    color = MutedText,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.End
                )
            }
        }
        Spacer(Modifier.height(13.dp))
        LinearProgressIndicator(
            progress = { level.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(7.dp)
                .clip(CircleShape),
            color = Primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = if (level.level == 100) {
                "Nivel máximo alcanzado"
            } else {
                "${level.xpInLevel} / ${level.xpForNextLevel} XP hacia Nivel ${level.level + 1}"
            },
            color = MutedText,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ContinueCard(game: SudokuGameState, onClick: () -> Unit) {
    val hintSummary = if (game.mode == GameMode.WITH_HINTS) {
        "${game.hintsRemaining}/3 pistas disponibles"
    } else {
        null
    }
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Continuar partida activa" },
        shape = RoundedCornerShape(17.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Primary.copy(alpha = 0.12f), RoundedCornerShape(13.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("9×9", color = Primary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Continuar", color = Primary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(game.puzzle.difficulty.displayName, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = listOfNotNull(game.mode.displayName, hintSummary).joinToString(" · "),
                    color = MutedText,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
            Text(
                text = "${game.errors}/3 errores",
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LearnSudokuCard(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 66.dp)
            .semantics { contentDescription = "Aprende Sudoku. Próximamente" },
        shape = RoundedCornerShape(17.dp),
        color = SurfaceColor,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(Primary.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("123", color = Primary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Aprende Sudoku", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("Técnicas y estrategias", color = MutedText, fontSize = 12.sp)
            }
            Text(
                "Próximamente",
                color = Primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun GameLaunchSection(
    selectedDifficulty: String,
    onDifficultySelected: (String) -> Unit,
    onNewGame: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        DifficultySection(
            selectedDifficulty = selectedDifficulty,
            onDifficultySelected = onDifficultySelected
        )
        Button(
            onClick = onNewGame,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Nueva partida", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DifficultySection(
    selectedDifficulty: String,
    onDifficultySelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
        SectionTitle(title = "Dificultad", subtitle = "Seleccionada: $selectedDifficulty")
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            contentPadding = PaddingValues(end = 4.dp)
        ) {
            items(difficulties) { difficulty ->
                val isSelected = difficulty == selectedDifficulty
                val icon = LocalSudoluxThemeConfig.current.drawables?.difficultyIcons
                    ?.forDifficulty(SudokuDifficulty.fromDisplayName(difficulty))
                Surface(
                    onClick = { onDifficultySelected(difficulty) },
                    modifier = Modifier
                        .heightIn(min = 48.dp)
                        .semantics {
                            selected = isSelected
                            role = Role.RadioButton
                            contentDescription = "Dificultad $difficulty"
                        },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) Primary.copy(alpha = 0.14f) else SurfaceColor,
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) Primary else BorderColor
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (icon != null) {
                            Image(
                                painter = painterResource(icon),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                        }
                        Text(
                            text = if (isSelected) "✓ $difficulty" else difficulty,
                            color = if (isSelected) Primary else MaterialTheme.colorScheme.onSurface,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeSelectionDialog(
    difficulty: String,
    selectedMode: GameMode,
    onModeSelected: (GameMode) -> Unit,
    onStart: () -> Unit,
    onCancel: () -> Unit
) {
    val sudokuDifficulty = SudokuDifficulty.fromDisplayName(difficulty)
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("¿Cómo quieres jugar?", fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Dificultad: $difficulty",
                    color = MutedText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                GameMode.entries.forEach { mode ->
                    val isSelected = mode == selectedMode
                    val xpPossible = XpCalculator.potentialXp(
                        difficulty = sudokuDifficulty,
                        mode = mode,
                        hintsUsed = 0,
                        errors = 0
                    )
                    Surface(
                        onClick = { onModeSelected(mode) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 96.dp)
                            .semantics {
                                selected = isSelected
                                role = Role.RadioButton
                                contentDescription = "${mode.displayName}, $xpPossible XP posible"
                            },
                        shape = RoundedCornerShape(15.dp),
                        color = if (isSelected) Primary.copy(alpha = 0.14f) else ElevatedSurface,
                        border = BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) Primary else BorderColor
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(13.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isSelected) "✓ ${mode.displayName}" else mode.displayName,
                                    modifier = Modifier.weight(1f),
                                    color = if (isSelected) Primary else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$xpPossible XP",
                                    color = Primary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Text(
                                text = when (mode) {
                                    GameMode.WITH_HINTS ->
                                        "Hasta 3 pistas incluidas. Usarlas reduce el XP."
                                    GameMode.NO_HINTS ->
                                        "Sin ayudas durante la partida. Mayor recompensa de XP."
                                },
                                color = MutedText,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = Primary)
            ) { Text("Comenzar") }
        },
        dismissButton = { TextButton(onClick = onCancel) { Text("Cancelar") } },
        containerColor = SurfaceColor
    )
}

@Composable
private fun SectionTitle(title: String, subtitle: String? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        subtitle?.let {
            Text(text = it, color = MutedText, fontSize = 11.sp)
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SudoluxHomeScreenPreview() {
    SudoluxAppTheme {
        SudoluxHomeScreen()
    }
}
