package com.example.sudoluxapp.ui.navigation

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.sudoluxapp.R

enum class SudoluxAppScreen { HOME, GAME, PROGRESS, SETTINGS, ABOUT }

enum class SudoluxDestination(
    val label: String,
    @param:DrawableRes val icon: Int
) {
    HOME("Inicio", R.drawable.ic_home),
    PLAY("Jugar", R.drawable.ic_play),
    PROGRESS("Progreso", R.drawable.ic_progress)
}

@Composable
fun SudoluxBottomBar(
    selectedDestination: SudoluxDestination,
    onDestinationSelected: (SudoluxDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        SudoluxDestination.entries.forEach { destination ->
            val isSelected = selectedDestination == destination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onDestinationSelected(destination) },
                modifier = Modifier.semantics {
                    contentDescription = "${destination.label}, navegación principal"
                },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clearAndSetSemantics { },
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = CircleShape,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            },
                            contentColor = if (isSelected) {
                                MaterialTheme.colorScheme.onPrimaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.outlineVariant
                                }
                            )
                        ) {
                            Icon(
                                painter = painterResource(destination.icon),
                                contentDescription = null,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        if (isSelected) {
                            Text(
                                text = "✓",
                                modifier = Modifier.align(Alignment.TopEnd),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                label = {
                    Text(
                        text = destination.label,
                        style = if (isSelected) {
                            MaterialTheme.typography.labelLarge
                        } else {
                            MaterialTheme.typography.labelMedium
                        }
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}
