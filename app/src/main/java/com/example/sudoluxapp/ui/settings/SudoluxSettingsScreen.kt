package com.example.sudoluxapp.ui.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoluxapp.R
import com.example.sudoluxapp.domain.progression.PlayerProgress
import com.example.sudoluxapp.domain.settings.AppTheme
import com.example.sudoluxapp.domain.settings.SudokuNumberSize
import com.example.sudoluxapp.domain.settings.UserSettings
import com.example.sudoluxapp.ui.theme.SudoluxThemeCatalog
import com.example.sudoluxapp.ui.theme.SudoluxThemeConfig
import com.example.sudoluxapp.ui.theme.sudoluxScreenContainerColor

@Composable
fun SudoluxSettingsScreen(
    settings: UserSettings,
    playerProgress: PlayerProgress,
    onThemeChange: (AppTheme) -> Unit,
    onHighContrastChange: (Boolean) -> Unit,
    onNumberSizeChange: (SudokuNumberSize) -> Unit,
    onReduceAnimationsChange: (Boolean) -> Unit,
    onSoundChange: (Boolean) -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onAutoCleanNotesChange: (Boolean) -> Unit,
    onShowErrorsChange: (Boolean) -> Unit,
    onHighlightMatchingChange: (Boolean) -> Unit,
    onHighlightRelatedChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    onAbout: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showResetConfirmation by remember { mutableStateOf(false) }
    BackHandler(onBack = onBack)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = sudoluxScreenContainerColor(),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        BoxWithConstraints(
            Modifier.fillMaxSize().padding(innerPadding)
        ) {
            val useTwoColumns = maxWidth >= 700.dp && maxWidth > maxHeight
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { SettingsHeader(onBack) }
                if (useTwoColumns) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                AppearanceSection(settings, playerProgress, onThemeChange)
                                AccessibilitySection(
                                    settings,
                                    onHighContrastChange,
                                    onNumberSizeChange,
                                    onReduceAnimationsChange
                                )
                                FeedbackSection(settings, onSoundChange, onHapticsChange)
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                GameSection(
                                    settings,
                                    onAutoCleanNotesChange,
                                    onShowErrorsChange,
                                    onHighlightMatchingChange,
                                    onHighlightRelatedChange
                                )
                                ResetSection { showResetConfirmation = true }
                                InformationSection(onAbout)
                            }
                        }
                    }
                } else {
                    item { AppearanceSection(settings, playerProgress, onThemeChange) }
                    item {
                        AccessibilitySection(
                            settings,
                            onHighContrastChange,
                            onNumberSizeChange,
                            onReduceAnimationsChange
                        )
                    }
                    item { FeedbackSection(settings, onSoundChange, onHapticsChange) }
                    item {
                        GameSection(
                            settings,
                            onAutoCleanNotesChange,
                            onShowErrorsChange,
                            onHighlightMatchingChange,
                            onHighlightRelatedChange
                        )
                    }
                    item { ResetSection { showResetConfirmation = true } }
                    item { InformationSection(onAbout) }
                }
                item { Spacer(Modifier.height(12.dp)) }
            }
        }
    }

    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            title = { Text("Restablecer configuración", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Se restaurarán las preferencias visuales y de juego. " +
                        "Tu progreso, medallas y partidas no se eliminarán."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReset()
                        showResetConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) { Text("Restablecer") }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmation = false }) { Text("Cancelar") }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

@Composable
private fun SettingsHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            onClick = onBack,
            modifier = Modifier
                .size(48.dp)
                .semantics { contentDescription = "Volver a Inicio" },
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
        Column(Modifier.padding(start = 14.dp)) {
            Text("Configuración", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                "Personaliza Sudolux a tu ritmo",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun AppearanceSection(
    settings: UserSettings,
    playerProgress: PlayerProgress,
    onThemeChange: (AppTheme) -> Unit
) {
    SettingsCard("Apariencia") {
        SettingLabel("Tema de la app", "Elige un tema disponible según tu progreso")
        ThemePicker(
            selected = settings.theme,
            playerProgress = playerProgress,
            onSelected = onThemeChange
        )
    }
}

@Composable
private fun ThemePicker(
    selected: AppTheme,
    playerProgress: PlayerProgress,
    onSelected: (AppTheme) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SudoluxThemeCatalog.all.chunked(2).forEach { rowThemes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowThemes.forEach { config ->
                    ThemeOptionCard(
                        config = config,
                        selected = config.theme == selected,
                        unlocked = config.theme.isUnlocked(playerProgress),
                        onSelected = { onSelected(config.theme) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (rowThemes.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ThemeOptionCard(
    config: SudoluxThemeConfig,
    selected: Boolean,
    unlocked: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = when {
        !unlocked -> "Bloqueado. ${config.theme.unlockDescription}"
        selected -> "Tema activo"
        else -> "Disponible"
    }
    Surface(
        onClick = onSelected,
        enabled = unlocked,
        modifier = modifier.semantics {
            this.selected = selected
            role = Role.RadioButton
            contentDescription = config.theme.displayName
            stateDescription = state
        },
        shape = RoundedCornerShape(16.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
        },
        border = BorderStroke(
            if (selected) 2.dp else 1.dp,
            if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.6f)
                    .clip(RoundedCornerShape(11.dp))
                    .background(config.previewBackground),
                contentAlignment = Alignment.Center
            ) {
                val drawables = config.drawables
                if (drawables != null) {
                    Image(
                        painter = painterResource(drawables.thumbnail),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = if (config.theme == AppTheme.CLASSIC) "S" else "S✦",
                        color = config.previewForeground,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                when {
                    !unlocked && drawables != null -> {
                        Box(Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.28f)))
                        Image(
                            painter = painterResource(drawables.locked),
                            contentDescription = null,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                    selected && drawables != null -> {
                        Image(
                            painter = painterResource(drawables.active),
                            contentDescription = null,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .size(34.dp)
                        )
                    }
                }
            }
            Text(
                text = config.theme.displayName,
                fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = config.theme.unlockDescription,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
            if (selected && config.drawables == null) {
                Text(
                    text = "✓ Activo",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun AccessibilitySection(
    settings: UserSettings,
    onHighContrastChange: (Boolean) -> Unit,
    onNumberSizeChange: (SudokuNumberSize) -> Unit,
    onReduceAnimationsChange: (Boolean) -> Unit
) {
    SettingsCard("Accesibilidad") {
        SettingToggle(
            title = "Alto contraste",
            description = "Refuerza bordes, textos, controles y selección",
            checked = settings.highContrast,
            onCheckedChange = onHighContrastChange
        )
        SettingLabel("Tamaño de números", "Ajusta valores y candidatos del tablero")
        ChoiceRow(
            values = SudokuNumberSize.entries,
            selected = settings.numberSize,
            label = SudokuNumberSize::displayName,
            onSelected = onNumberSizeChange
        )
        SettingToggle(
            title = "Reducir animaciones",
            description = "Evita movimientos decorativos innecesarios",
            checked = settings.reduceAnimations,
            onCheckedChange = onReduceAnimationsChange
        )
    }
}

@Composable
private fun FeedbackSection(
    settings: UserSettings,
    onSoundChange: (Boolean) -> Unit,
    onHapticsChange: (Boolean) -> Unit
) {
    SettingsCard("Respuesta") {
        SettingToggle(
            title = "Sonido",
            description = "Preparado para futuros efectos de juego",
            checked = settings.soundEnabled,
            onCheckedChange = onSoundChange
        )
        SettingToggle(
            title = "Vibración",
            description = "Respuesta háptica al jugar e interactuar",
            checked = settings.hapticsEnabled,
            onCheckedChange = onHapticsChange
        )
    }
}

@Composable
private fun GameSection(
    settings: UserSettings,
    onAutoCleanNotesChange: (Boolean) -> Unit,
    onShowErrorsChange: (Boolean) -> Unit,
    onHighlightMatchingChange: (Boolean) -> Unit,
    onHighlightRelatedChange: (Boolean) -> Unit
) {
    SettingsCard("Juego") {
        SettingToggle(
            title = "Limpieza automática de notas",
            description = "Quita el candidato colocado de fila, columna y bloque",
            checked = settings.autoCleanNotes,
            onCheckedChange = onAutoCleanNotesChange
        )
        SettingToggle(
            title = "Mostrar errores inmediatamente",
            description = "Muestra un aviso visual al introducir un valor incorrecto",
            checked = settings.showErrorsImmediately,
            onCheckedChange = onShowErrorsChange
        )
        SettingToggle(
            title = "Resaltar números iguales",
            description = "Destaca valores y candidatos que coinciden",
            checked = settings.highlightMatchingNumbers,
            onCheckedChange = onHighlightMatchingChange
        )
        SettingToggle(
            title = "Resaltar zona relacionada",
            description = "Sombrea la fila, columna y bloque seleccionados",
            checked = settings.highlightRelatedArea,
            onCheckedChange = onHighlightRelatedChange
        )
    }
}

@Composable
private fun ResetSection(onReset: () -> Unit) {
    SettingsCard("Preferencias") {
        Surface(
            onClick = onReset,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 52.dp)
                .semantics { contentDescription = "Restablecer configuración" },
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Box(Modifier.padding(14.dp), contentAlignment = Alignment.CenterStart) {
                Text("Restablecer configuración", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun InformationSection(onAbout: () -> Unit) {
    SettingsCard("Información") {
        Surface(
            onClick = onAbout,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 58.dp)
                .semantics { contentDescription = "Acerca de Sudolux" },
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Acerca de", fontWeight = FontWeight.Bold)
                    Text(
                        "Aplicación, propósito y equipo",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
                Text("›", color = MaterialTheme.colorScheme.primary, fontSize = 28.sp)
            }
        }
    }
}

@Composable
private fun SettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            title,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(
                if (MaterialTheme.colorScheme.outline == MaterialTheme.colorScheme.onSurface) 2.dp else 1.dp,
                MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                content = content
            )
        }
    }
}

@Composable
private fun SettingLabel(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(title, fontWeight = FontWeight.Bold)
        Text(description, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
    }
}

@Composable
private fun SettingToggle(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .semantics {
                contentDescription = title
                stateDescription = if (checked) "Activado" else "Desactivado"
                role = Role.Switch
            },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(
                    description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            Switch(checked = checked, onCheckedChange = null)
        }
    }
}

@Composable
private fun <T> ChoiceRow(
    values: List<T>,
    selected: T,
    label: (T) -> String,
    onSelected: (T) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        values.forEach { value ->
            val isSelected = value == selected
            Surface(
                onClick = { onSelected(value) },
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp)
                    .semantics {
                        this.selected = isSelected
                        role = Role.RadioButton
                        contentDescription = label(value)
                    },
                shape = RoundedCornerShape(13.dp),
                color = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                border = BorderStroke(
                    if (isSelected) 2.dp else 1.dp,
                    if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Box(Modifier.padding(horizontal = 5.dp, vertical = 10.dp), contentAlignment = Alignment.Center) {
                    Text(
                        if (isSelected) "✓ ${label(value)}" else label(value),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
