package com.example.sudoluxapp.ui.progress

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.disabled
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoluxapp.R
import com.example.sudoluxapp.domain.progression.BoardStyle
import com.example.sudoluxapp.domain.progression.Medal
import com.example.sudoluxapp.domain.progression.PlayerProgress
import com.example.sudoluxapp.domain.progression.ProfileFrame
import com.example.sudoluxapp.ui.components.PlayerLevelFrame
import com.example.sudoluxapp.ui.components.drawableRes
import com.example.sudoluxapp.ui.components.drawableResOrNull
import com.example.sudoluxapp.ui.components.masteryMilestoneDrawableRes
import com.example.sudoluxapp.ui.navigation.SudoluxBottomBar
import com.example.sudoluxapp.ui.navigation.SudoluxDestination
import com.example.sudoluxapp.ui.theme.SudoluxAppTheme
import com.example.sudoluxapp.ui.theme.sudoluxScreenContainerColor

@Composable
fun SudoluxProgressScreen(
    uiState: ProgressScreenUiState,
    onHome: () -> Unit,
    onPlay: () -> Unit,
    onBack: () -> Unit,
    onBoardStyleSelected: (BoardStyle) -> Unit = { },
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)
    var selectedSection by rememberSaveable { mutableStateOf(ProgressSection.SUMMARY) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sudoluxScreenContainerColor(),
        bottomBar = {
            SudoluxBottomBar(
                selectedDestination = SudoluxDestination.PROGRESS,
                onDestinationSelected = { destination ->
                    when (destination) {
                        SudoluxDestination.HOME -> onHome()
                        SudoluxDestination.PLAY -> onPlay()
                        SudoluxDestination.PROGRESS -> Unit
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
            val isLandscape = maxWidth > maxHeight
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = if (isLandscape) 24.dp else 18.dp,
                    top = 14.dp,
                    end = if (isLandscape) 24.dp else 18.dp,
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { ProgressHeader(onBack) }
                item {
                    ProgressSectionSelector(
                        selected = selectedSection,
                        onSelected = { selectedSection = it }
                    )
                }
                when (selectedSection) {
                    ProgressSection.SUMMARY -> {
                        item { LevelCard(uiState.level) }
                        item { PendingAchievementsSection(uiState.pendingBadges) }
                    }

                    ProgressSection.STATISTICS -> {
                        item { StatisticsSection(uiState.statistics) }
                    }

                    ProgressSection.ACHIEVEMENTS -> {
                        item { MedalsSection(uiState.medals) }
                        item {
                            MasterySection(
                                count = uiState.absoluteMasteryCount,
                                milestones = uiState.masteryMilestones
                            )
                        }
                        item {
                            BoardCustomizationSection(
                                boardStyles = uiState.boardStyles,
                                onBoardStyleSelected = onBoardStyleSelected
                            )
                        }
                        item {
                            ProfileFramesSection(
                                frames = uiState.profileFrames,
                                currentLevel = uiState.level.level
                            )
                        }
                        item { EarnedBadgesSection(uiState.earnedBadges) }
                        item { UpcomingUnlocksSection(uiState.upcomingUnlocks) }
                    }
                }
            }
        }
    }
}

private enum class ProgressSection(val label: String) {
    SUMMARY("Resumen"),
    STATISTICS("Estadísticas"),
    ACHIEVEMENTS("Logros")
}

@Composable
private fun ProgressSectionSelector(
    selected: ProgressSection,
    onSelected: (ProgressSection) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        ProgressSection.entries.forEach { section ->
            val isSelected = section == selected
            Surface(
                onClick = { onSelected(section) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 46.dp)
                    .semantics {
                        this.selected = isSelected
                        role = Role.Tab
                        contentDescription = "Sección ${section.label}"
                    },
                shape = RoundedCornerShape(14.dp),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surface
                },
                border = BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.outlineVariant
                    }
                )
            ) {
                Box(Modifier.padding(horizontal = 4.dp, vertical = 10.dp), contentAlignment = Alignment.Center) {
                    Text(
                        section.label,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_back),
                contentDescription = "Volver a Inicio",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(Modifier.width(4.dp))
        Column {
            Text(
                text = "Progreso",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Tu recorrido en Sudolux",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun LevelCard(level: ProgressLevelUiState) {
    val shape = RoundedCornerShape(22.dp)
    val colors = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.surface, shape)
            .border(1.dp, colors.outlineVariant, shape)
            .padding(18.dp)
            .semantics {
                contentDescription = if (level.isMaximum) {
                    "Nivel 100, Leyenda Sudolux, ${level.totalXp} XP total, nivel máximo"
                } else {
                    "Nivel ${level.level}, ${level.title}, ${level.totalXp} XP total, " +
                        "${level.xpInLevel} de ${level.xpForNextLevel} XP, " +
                        "faltan ${level.xpRemaining} XP"
                }
            }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PlayerLevelFrame(
                frame = level.profileFrame,
                level = level.level,
                size = 72.dp
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    level.title,
                    color = colors.onSurface,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    "${level.totalXp} XP total",
                    color = colors.onSurfaceVariant,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(Modifier.height(20.dp))
        if (level.isMaximum) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = colors.primaryContainer,
                border = BorderStroke(1.dp, colors.primary)
            ) {
                Text(
                    text = "Nivel máximo alcanzado",
                    modifier = Modifier.padding(14.dp),
                    color = colors.onPrimaryContainer,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            LinearProgressIndicator(
                progress = { level.fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape),
                color = colors.primary,
                trackColor = colors.surfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${level.xpInLevel} / ${level.xpForNextLevel} XP",
                    color = colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${level.xpRemaining} XP para Nivel ${level.level + 1}",
                    color = colors.onSurfaceVariant,
                    fontSize = 13.sp,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

@Composable
private fun PendingAchievementsSection(badges: List<AchievementBadgeUiState>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Logros por desbloquear",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            if (badges.isEmpty()) {
                Text(
                    text = "Todos los logros han sido obtenidos.",
                    fontWeight = FontWeight.Bold
                )
            } else {
                badges.forEach { badge ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .semantics {
                                contentDescription = "${badge.name}, ${badge.requirementLabel}, Pendiente"
                            },
                        shape = RoundedCornerShape(13.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(badge.name, fontWeight = FontWeight.Bold)
                                Text(
                                    badge.requirementLabel,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            }
                            Text(
                                "Pendiente",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MedalsSection(medals: List<MedalUiState>, modifier: Modifier = Modifier) {
    ProgressSectionCard(title = "Medallas", modifier = modifier) {
        medals.chunked(2).forEach { rowMedals ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowMedals.forEach { medal ->
                    MedalCard(medal, Modifier.weight(1f))
                }
                if (rowMedals.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MedalCard(medal: MedalUiState, modifier: Modifier = Modifier) {
    val isLegend = medal.medal == Medal.LEGEND
    val accent = if (isLegend) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.primary
    }
    Surface(
        modifier = modifier
            .heightIn(min = 118.dp)
            .semantics {
                contentDescription = "Medalla ${medal.medal.displayName}, cantidad ${medal.count}, " +
                    if (medal.isEarned) "activa" else "atenuada"
            },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(if (isLegend) 2.dp else 1.dp, accent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(medal.medal.drawableRes()),
                contentDescription = null,
                modifier = Modifier
                    .size(62.dp)
                    .aspectRatio(1f)
                    .alpha(medal.iconAlpha),
                contentScale = ContentScale.Fit
            )
            Text(
                medal.medal.displayName,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text("×${medal.count}", color = accent, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun MasterySection(
    count: Int,
    milestones: List<MasteryMilestoneUiState>,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    ProgressSectionCard(title = "Maestría absoluta", modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(colors.primaryContainer, CircleShape)
                    .border(1.dp, colors.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("×$count", color = colors.primary, fontSize = 21.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = "Completa Maestro sin pistas y sin errores.",
                modifier = Modifier.weight(1f),
                color = colors.onSurfaceVariant,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            milestones.forEach { milestone ->
                val status = if (milestone.reached) "alcanzado" else "bloqueado"
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 92.dp)
                        .semantics {
                            contentDescription = "Hito por ${milestone.target}, $status"
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = if (milestone.reached) {
                        colors.primaryContainer
                    } else {
                        colors.surfaceVariant
                    },
                    border = BorderStroke(
                        1.dp,
                        if (milestone.reached) colors.primary else colors.outlineVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 3.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(masteryMilestoneDrawableRes(milestone.target)),
                            contentDescription = null,
                            modifier = Modifier
                                .size(56.dp)
                                .aspectRatio(1f)
                                .alpha(if (milestone.reached) 1f else 0.3f),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            "×${milestone.target}",
                            color = if (milestone.reached) colors.primary else colors.onSurfaceVariant,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            if (milestone.reached) "Alcanzado" else "Bloqueado",
                            color = if (milestone.reached) {
                                colors.onPrimaryContainer
                            } else {
                                colors.onSurfaceVariant
                            },
                            fontSize = 9.sp,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardCustomizationSection(
    boardStyles: List<BoardStyleUiState>,
    onBoardStyleSelected: (BoardStyle) -> Unit
) {
    ProgressSectionCard(title = "Personalización del tablero") {
        boardStyles.forEach { boardStyle ->
            if (boardStyle.isUnlocked) {
                Surface(
                    onClick = { onBoardStyleSelected(boardStyle.style) },
                    modifier = boardStyle.modifier(),
                    shape = RoundedCornerShape(13.dp),
                    color = boardStyle.containerColor(),
                    border = boardStyle.borderStroke()
                ) {
                    BoardStyleContent(boardStyle)
                }
            } else {
                Surface(
                    modifier = boardStyle.modifier(),
                    shape = RoundedCornerShape(13.dp),
                    color = boardStyle.containerColor(),
                    border = boardStyle.borderStroke()
                ) {
                    BoardStyleContent(boardStyle)
                }
            }
        }
    }
}

private fun BoardStyleUiState.modifier(): Modifier = Modifier
    .fillMaxWidth()
    .semantics {
        selected = isSelected
        role = Role.RadioButton
        contentDescription = "$name, $requirementLabel, " + when {
            isSelected -> "Seleccionado"
            isUnlocked -> "Disponible"
            else -> "Bloqueado"
        }
        if (!isUnlocked) disabled()
    }

@Composable
private fun BoardStyleUiState.containerColor() = if (isSelected) {
    MaterialTheme.colorScheme.primaryContainer
} else {
    MaterialTheme.colorScheme.surfaceVariant
}

@Composable
private fun BoardStyleUiState.borderStroke() = BorderStroke(
    if (isSelected) 2.dp else 1.dp,
    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
)

@Composable
private fun BoardStyleContent(boardStyle: BoardStyleUiState) {
    Row(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BoardStylePreview(boardStyle.style, boardStyle.isUnlocked)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(boardStyle.name, fontWeight = FontWeight.Bold)
            Text(
                boardStyle.requirementLabel,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
        }
        Text(
            when {
                boardStyle.isSelected -> "Seleccionado"
                boardStyle.isUnlocked -> "Elegir"
                else -> "🔒 Bloqueado"
            },
            color = if (boardStyle.isUnlocked) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileFramesSection(
    frames: List<ProfileFrameUiState>,
    currentLevel: Int
) {
    ProgressSectionCard(title = "Marcos de perfil") {
        frames.forEach { frame ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics {
                        contentDescription = "${frame.name}, ${frame.requirementLabel}, " +
                            if (frame.isCurrent) "Actual" else "Alcanzado"
                    },
                shape = RoundedCornerShape(13.dp),
                color = if (frame.isCurrent) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                border = BorderStroke(
                    1.dp,
                    if (frame.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileFramePreview(
                        frame = frame.frame,
                        currentLevel = currentLevel
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(frame.name, fontWeight = FontWeight.Bold)
                        Text(
                            frame.requirementLabel,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        if (frame.isCurrent) "Actual" else "Alcanzado",
                        color = if (frame.isCurrent) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.secondary
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun EarnedBadgesSection(badges: List<AchievementBadgeUiState>) {
    ProgressSectionCard(title = "Insignias obtenidas") {
        if (badges.isEmpty()) {
            Text(
                "Aún no hay insignias obtenidas.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        } else {
            badges.chunked(2).forEach { rowBadges ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    rowBadges.forEach { badge ->
                        EarnedBadgeCard(badge, Modifier.weight(1f))
                    }
                    if (rowBadges.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun UpcomingUnlocksSection(unlocks: List<UpcomingUnlockUiState>) {
    ProgressSectionCard(title = "Próximos desbloqueos") {
        if (unlocks.isEmpty()) {
            Text(
                "No quedan recompensas pendientes.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp
            )
        } else {
            unlocks.forEach { unlock ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics {
                            contentDescription = "${unlock.name}, ${unlock.typeLabel}, " +
                                "${unlock.requirementLabel}, Bloqueado"
                        },
                    shape = RoundedCornerShape(13.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "🔒",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(unlock.name, fontWeight = FontWeight.Bold)
                            Text(
                                "${unlock.typeLabel} · ${unlock.requirementLabel}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            "Bloqueado",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoardStylePreview(boardStyle: BoardStyle, isUnlocked: Boolean) {
    val drawableRes = boardStyle.drawableResOrNull()
    if (drawableRes == null) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .aspectRatio(1f)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("9×9", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Black)
                Text("Tema", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
            }
        }
    } else {
        Image(
            painter = painterResource(drawableRes),
            contentDescription = null,
            modifier = Modifier
                .size(82.dp)
                .aspectRatio(1f)
                .alpha(if (isUnlocked) 1f else 0.32f)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
private fun ProfileFramePreview(frame: ProfileFrame, currentLevel: Int) {
    PlayerLevelFrame(frame = frame, level = currentLevel, size = 82.dp)
}

@Composable
private fun EarnedBadgeCard(
    badge: AchievementBadgeUiState,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .heightIn(min = 112.dp)
            .semantics {
                contentDescription = "${badge.name}, ${badge.requirementLabel}, Obtenida"
            },
        shape = RoundedCornerShape(13.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Image(
                painter = painterResource(badge.badge.drawableRes()),
                contentDescription = null,
                modifier = Modifier
                    .size(62.dp)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )
            Text(
                badge.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun StatisticsSection(statistics: GameStatisticsUiState) {
    ProgressSectionCard(title = "Estadísticas") {
        StatisticsHeader()
        statistics.difficulties.forEach { row ->
            StatisticsRow(
                label = row.difficulty.displayName,
                withoutHints = row.withoutHints,
                withHints = row.withHints,
                total = row.total
            )
        }
        StatisticsRow(
            label = "Total",
            withoutHints = statistics.withoutHints,
            withHints = statistics.withHints,
            total = statistics.totalCompleted,
            emphasized = true
        )
        if (statistics.historicalUnclassified > 0) {
            Text(
                text = "${statistics.historicalUnclassified} partidas históricas conservadas " +
                    "sin dificultad ni uso de pistas clasificables.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun StatisticsHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatisticsCell("Dificultad", 1.5f, TextAlign.Start, true)
        StatisticsCell("Sin\npistas", 1f, TextAlign.Center, true)
        StatisticsCell("Con\npistas", 1f, TextAlign.Center, true)
        StatisticsCell("Total", 0.8f, TextAlign.End, true)
    }
}

@Composable
private fun StatisticsRow(
    label: String,
    withoutHints: Int,
    withHints: Int,
    total: Int,
    emphasized: Boolean = false
) {
    val background = if (emphasized) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "$label, sin pistas $withoutHints, con pistas $withHints, total $total"
            },
        shape = RoundedCornerShape(12.dp),
        color = background,
        border = BorderStroke(
            1.dp,
            if (emphasized) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatisticsCell(label, 1.5f, TextAlign.Start, emphasized)
            StatisticsCell(withoutHints.toString(), 1f, TextAlign.Center, emphasized)
            StatisticsCell(withHints.toString(), 1f, TextAlign.Center, emphasized)
            StatisticsCell(total.toString(), 0.8f, TextAlign.End, true)
        }
    }
}

@Composable
private fun RowScope.StatisticsCell(
    text: String,
    weight: Float,
    alignment: TextAlign,
    bold: Boolean
) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 11.sp,
        lineHeight = 13.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium,
        textAlign = alignment
    )
}

@Composable
private fun ProgressSectionCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                title,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            content()
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun SudoluxProgressScreenPreview() {
    SudoluxAppTheme {
        SudoluxProgressScreen(
            uiState = ProgressScreenPresenter.present(PlayerProgress()),
            onHome = { },
            onPlay = { },
            onBack = { }
        )
    }
}
