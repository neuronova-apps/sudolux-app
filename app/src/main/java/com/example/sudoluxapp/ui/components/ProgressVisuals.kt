package com.example.sudoluxapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sudoluxapp.R
import com.example.sudoluxapp.domain.progression.AchievementBadge
import com.example.sudoluxapp.domain.progression.BoardStyle
import com.example.sudoluxapp.domain.progression.Medal
import com.example.sudoluxapp.domain.progression.ProfileFrame

@Composable
fun PlayerLevelFrame(
    frame: ProfileFrame,
    level: Int,
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .semantics {
                contentDescription = "${frame.displayName}, nivel $level"
            },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(frame.drawableRes()),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )
        Box(
            modifier = Modifier
                .size(size * 0.42f)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.88f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                level.toString(),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = when {
                    size <= 60.dp -> 12.sp
                    size <= 74.dp -> 14.sp
                    else -> 15.sp
                },
                fontWeight = FontWeight.Black
            )
        }
    }
}

fun Medal.drawableRes(): Int = when (this) {
    Medal.BRONZE -> R.drawable.medal_bronze
    Medal.SILVER -> R.drawable.medal_silver
    Medal.GOLD -> R.drawable.medal_gold
    Medal.PLATINUM -> R.drawable.medal_platinum
    Medal.DIAMOND -> R.drawable.medal_diamond
    Medal.LEGEND -> R.drawable.medal_legend
}

fun masteryMilestoneDrawableRes(target: Int): Int = when (target) {
    1 -> R.drawable.absolute_mastery_1
    5 -> R.drawable.absolute_mastery_5
    10 -> R.drawable.absolute_mastery_10
    25 -> R.drawable.absolute_mastery_25
    else -> error("Hito de Maestría absoluta sin recurso: $target")
}

fun ProfileFrame.drawableRes(): Int = when (this) {
    ProfileFrame.INITIAL -> R.drawable.profile_frame_initial
    ProfileFrame.ADVANCED_1 -> R.drawable.profile_frame_advanced_1
    ProfileFrame.ADVANCED_2 -> R.drawable.profile_frame_advanced_2
    ProfileFrame.MASTER -> R.drawable.profile_frame_master
    ProfileFrame.ELITE -> R.drawable.profile_frame_elite
    ProfileFrame.LEGEND -> R.drawable.profile_frame_legend
}

fun AchievementBadge.drawableRes(): Int = when (this) {
    AchievementBadge.FIRST_STEP -> R.drawable.achievement_first_step
    AchievementBadge.ASCENT -> R.drawable.achievement_ascenso
    AchievementBadge.ADVANCED -> R.drawable.achievement_avanzado
    AchievementBadge.CHALLENGE_COMPLETE -> R.drawable.achievement_desafio_superado
    AchievementBadge.MASTER -> R.drawable.achievement_maestro
    AchievementBadge.FIRST_LEGEND -> R.drawable.achievement_first_legend
    AchievementBadge.GRAND_MASTER -> R.drawable.achievement_grand_master
    AchievementBadge.SUDOLUX_ELITE -> R.drawable.achievement_elite_sudolux
    AchievementBadge.SUDOLUX_LEGEND -> R.drawable.achievement_legend_sudolux
}

fun BoardStyle.drawableResOrNull(): Int? = when (this) {
    BoardStyle.DEFAULT -> null
    BoardStyle.ALTERNATIVE -> R.drawable.board_alternative
    BoardStyle.ADVANCED -> R.drawable.board_advanced
    BoardStyle.EXPERT -> R.drawable.board_expert
    BoardStyle.GRAND_MASTER -> R.drawable.board_grand_master
    BoardStyle.EXCLUSIVE -> R.drawable.board_exclusive
}
