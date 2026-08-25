package com.example.sudoluxapp.ui.progress

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoluxapp.R
import com.example.sudoluxapp.domain.progression.Medal
import com.example.sudoluxapp.domain.progression.PlayerProgress
import com.example.sudoluxapp.ui.navigation.SudoluxBottomBar
import com.example.sudoluxapp.ui.navigation.SudoluxDestination
import com.example.sudoluxapp.ui.theme.SudoluxAppTheme

@Composable
fun SudoluxProgressScreen(
    uiState: ProgressScreenUiState,
    onHome: () -> Unit,
    onPlay: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
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
                item { LevelCard(uiState.level) }
                item { NextUnlockCard(uiState.nextUnlock) }
                item {
                    if (isLandscape) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            MedalsSection(uiState.medals, Modifier.weight(1f))
                            MasterySection(
                                count = uiState.absoluteMasteryCount,
                                milestones = uiState.masteryMilestones,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            MedalsSection(uiState.medals)
                            MasterySection(
                                count = uiState.absoluteMasteryCount,
                                milestones = uiState.masteryMilestones
                            )
                        }
                    }
                }
                item {
                    PersonalizationSection(
                        unlocked = uiState.unlocked,
                        locked = uiState.locked
                    )
                }
                item { StatisticsPlaceholder() }
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
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(colors.primaryContainer, CircleShape)
                    .border(1.dp, colors.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "NIVEL",
                        color = colors.onPrimaryContainer,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        level.level.toString(),
                        color = colors.primary,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
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
private fun NextUnlockCard(nextUnlock: UnlockableUiState?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "Próximo desbloqueo",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            if (nextUnlock == null) {
                Text(
                    text = "Todos los desbloqueos disponibles conseguidos.",
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(nextUnlock.name, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = "${nextUnlock.typeLabel} · ${nextUnlock.requirementLabel}",
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 14.sp
                )
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
            .heightIn(min = 76.dp)
            .semantics {
                contentDescription = "Medalla ${medal.medal.displayName}, cantidad ${medal.count}"
            },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(if (isLegend) 2.dp else 1.dp, accent)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                    .border(1.dp, accent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = medal.medal.displayName.take(1),
                    color = accent,
                    fontWeight = FontWeight.Black
                )
            }
            Spacer(Modifier.width(9.dp))
            Column {
                Text(
                    medal.medal.displayName,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("×${medal.count}", color = accent, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
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
                        .heightIn(min = 50.dp)
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
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
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
private fun PersonalizationSection(
    unlocked: List<UnlockableUiState>,
    locked: List<UnlockableUiState>
) {
    ProgressSectionCard(title = "Personalización") {
        UnlockGroup("Desbloqueados", unlocked, true)
        UnlockGroup("Próximos", locked, false)
    }
}

@Composable
private fun UnlockGroup(
    title: String,
    unlocks: List<UnlockableUiState>,
    unlocked: Boolean
) {
    Text(
        text = "$title · ${unlocks.size}",
        color = if (unlocked) {
            MaterialTheme.colorScheme.secondary
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
    if (unlocks.isEmpty()) {
        Text(
            text = if (unlocked) "Aún no hay desbloqueos obtenidos." else "No quedan desbloqueos pendientes.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp
        )
    } else {
        unlocks.forEach { unlock -> UnlockableRow(unlock) }
    }
}

@Composable
private fun UnlockableRow(unlock: UnlockableUiState) {
    val statusLabel = if (unlock.isUnlocked) "Desbloqueado" else "Bloqueado"
    val accent = if (unlock.isUnlocked) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                contentDescription = "${unlock.name}, ${unlock.typeLabel}, " +
                    "${unlock.requirementLabel}, $statusLabel"
            },
        shape = RoundedCornerShape(13.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            1.dp,
            if (unlock.isUnlocked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (unlock.isUnlocked) "✓" else "—",
                color = accent,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    unlock.name,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${unlock.typeLabel} · ${unlock.requirementLabel}",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
            Text(statusLabel, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatisticsPlaceholder() {
    ProgressSectionCard(title = "Estadísticas") {
        Text(
            text = "Las estadísticas de partidas aparecerán aquí cuando existan datos persistidos fiables.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
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
