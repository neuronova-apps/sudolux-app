package com.neuronovaapps.sudolux.ui.progress

import com.neuronovaapps.sudolux.domain.progression.AchievementBadge
import com.neuronovaapps.sudolux.domain.progression.BoardStyle
import com.neuronovaapps.sudolux.domain.progression.Medal
import com.neuronovaapps.sudolux.domain.progression.PlayerLevelCalculator
import com.neuronovaapps.sudolux.domain.progression.PlayerProgress
import com.neuronovaapps.sudolux.domain.progression.ProfileFrame
import com.neuronovaapps.sudolux.domain.progression.ProgressionCalculator
import com.neuronovaapps.sudolux.domain.progression.UnlockRequirement
import com.neuronovaapps.sudolux.domain.progression.Unlockable
import com.neuronovaapps.sudolux.domain.progression.UnlockableCatalog
import com.neuronovaapps.sudolux.domain.progression.UnlockableType
import com.neuronovaapps.sudolux.domain.sudoku.SudokuDifficulty

data class ProgressLevelUiState(
    val level: Int,
    val profileFrame: ProfileFrame,
    val title: String,
    val totalXp: Int,
    val xpInLevel: Int,
    val xpForNextLevel: Int?,
    val xpRemaining: Int,
    val fraction: Float,
    val isMaximum: Boolean
)

data class MedalUiState(
    val medal: Medal,
    val count: Int,
    val requirementLabel: String
) {
    val isEarned: Boolean get() = count > 0
    val iconAlpha: Float get() = if (isEarned) 1f else 0.3f
}

data class MasteryMilestoneUiState(val target: Int, val reached: Boolean)

data class BoardStyleUiState(
    val style: BoardStyle,
    val name: String,
    val requirementLabel: String,
    val isUnlocked: Boolean,
    val isSelected: Boolean
)

data class ProfileFrameUiState(
    val frame: ProfileFrame,
    val name: String,
    val requirementLabel: String,
    val isCurrent: Boolean
)

data class AchievementBadgeUiState(
    val badge: AchievementBadge,
    val name: String,
    val requirementLabel: String
)

data class UpcomingUnlockUiState(
    val id: String,
    val name: String,
    val typeLabel: String,
    val requirementLabel: String
)

data class UnlockableUiState(
    val id: String,
    val name: String,
    val typeLabel: String,
    val requirementLabel: String,
    val isUnlocked: Boolean
)

data class DifficultyStatisticsUiState(
    val difficulty: SudokuDifficulty,
    val withoutHints: Int,
    val withHints: Int,
    val total: Int
)

data class GameStatisticsUiState(
    val difficulties: List<DifficultyStatisticsUiState>,
    val withoutHints: Int,
    val withHints: Int,
    val totalCompleted: Int,
    val historicalUnclassified: Int
)

data class ProgressScreenUiState(
    val level: ProgressLevelUiState,
    val medals: List<MedalUiState>,
    val absoluteMasteryCount: Int,
    val masteryMilestones: List<MasteryMilestoneUiState>,
    val boardStyles: List<BoardStyleUiState>,
    val profileFrames: List<ProfileFrameUiState>,
    val pendingBadges: List<AchievementBadgeUiState>,
    val earnedBadges: List<AchievementBadgeUiState>,
    val upcomingUnlocks: List<UpcomingUnlockUiState>,
    val statistics: GameStatisticsUiState,
    val unlocked: List<UnlockableUiState>,
    val locked: List<UnlockableUiState>
)

/** Convierte el dominio persistido en texto y estados listos para representar. */
object ProgressScreenPresenter {
    fun present(progress: PlayerProgress): ProgressScreenUiState {
        val level = progress.level
        val unlocks = UnlockableCatalog.all.map { it.toUiState(progress) }
        val locked = unlocks.filterNot { it.isUnlocked }
        val selectedBoardStyle = progress.selectedBoardStyle
            .takeIf { it.isUnlocked(progress) }
            ?: BoardStyle.DEFAULT
        val currentFrame = progress.currentProfileFrame
        val masteryMilestones = ProgressionCalculator.absoluteMasteryMilestones.map { target ->
            MasteryMilestoneUiState(target, progress.legendMedalCount >= target)
        }

        val statistics = progress.statistics
        return ProgressScreenUiState(
            level = ProgressLevelUiState(
                level = level.level,
                profileFrame = currentFrame,
                title = level.title,
                totalXp = level.totalXp,
                xpInLevel = level.xpInLevel,
                xpForNextLevel = level.xpForNextLevel,
                xpRemaining = level.xpRemaining,
                fraction = level.progress,
                isMaximum = level.level == PlayerLevelCalculator.MAX_LEVEL
            ),
            medals = Medal.entries.map { medal ->
                MedalUiState(medal, progress.medalCount(medal), medal.requirementLabel())
            },
            absoluteMasteryCount = progress.legendMedalCount,
            masteryMilestones = masteryMilestones,
            boardStyles = BoardStyle.entries.map { style ->
                val isUnlocked = style.isUnlocked(progress)
                BoardStyleUiState(
                    style = style,
                    name = style.displayName,
                    requirementLabel = style.requirementLabel,
                    isUnlocked = isUnlocked,
                    isSelected = isUnlocked && style == selectedBoardStyle
                )
            },
            profileFrames = ProfileFrame.entries
                .filter { it.isUnlocked(progress) }
                .map { frame ->
                    ProfileFrameUiState(
                        frame = frame,
                        name = frame.displayName,
                        requirementLabel = frame.requirementLabel,
                        isCurrent = frame == currentFrame
                    )
                },
            pendingBadges = AchievementBadge.entries
                .filterNot { it.isUnlocked(progress) }
                .map { it.toUiState() },
            earnedBadges = AchievementBadge.entries
                .filter { it.isUnlocked(progress) }
                .map { it.toUiState() },
            upcomingUnlocks = upcomingUnlocks(progress, masteryMilestones),
            statistics = GameStatisticsUiState(
                difficulties = SudokuDifficulty.entries.map { difficulty ->
                    val counts = statistics.forDifficulty(difficulty)
                    DifficultyStatisticsUiState(
                        difficulty = difficulty,
                        withoutHints = counts.withoutHints,
                        withHints = counts.withHints,
                        total = counts.total
                    )
                },
                withoutHints = statistics.withoutHints,
                withHints = statistics.withHints,
                totalCompleted = statistics.totalCompleted,
                historicalUnclassified = statistics.historicalUnclassified
            ),
            unlocked = unlocks.filter { it.isUnlocked },
            locked = locked
        )
    }

    private fun upcomingUnlocks(
        progress: PlayerProgress,
        masteryMilestones: List<MasteryMilestoneUiState>
    ): List<UpcomingUnlockUiState> = buildList {
        ProfileFrame.entries.firstOrNull { !it.isUnlocked(progress) }?.let { frame ->
            add(
                UpcomingUnlockUiState(
                    id = "frame_${frame.name.lowercase()}",
                    name = frame.displayName,
                    typeLabel = "Próximo marco",
                    requirementLabel = frame.requirementLabel
                )
            )
        }
        BoardStyle.entries.firstOrNull { !it.isUnlocked(progress) }?.let { boardStyle ->
            add(
                UpcomingUnlockUiState(
                    id = "board_${boardStyle.name.lowercase()}",
                    name = boardStyle.displayName,
                    typeLabel = "Próximo tablero",
                    requirementLabel = boardStyle.requirementLabel
                )
            )
        }
        masteryMilestones.firstOrNull { !it.reached }?.let { milestone ->
            add(
                UpcomingUnlockUiState(
                    id = "mastery_${milestone.target}",
                    name = "Maestría absoluta ×${milestone.target}",
                    typeLabel = "Próximo hito",
                    requirementLabel = "Obtener ${milestone.target} Medallas Leyenda"
                )
            )
        }
    }

    private fun AchievementBadge.toUiState() = AchievementBadgeUiState(
        badge = this,
        name = displayName,
        requirementLabel = requirementLabel
    )

    private fun Medal.requirementLabel(): String = when (this) {
        Medal.BRONZE -> "Completar Sudoku Fácil"
        Medal.SILVER -> "Completar Sudoku Medio"
        Medal.GOLD -> "Completar Sudoku Difícil"
        Medal.PLATINUM -> "Completar Sudoku Experto"
        Medal.DIAMOND -> "Completar Sudoku Maestro"
        Medal.LEGEND -> "Completar Sudoku Maestro sin pistas y sin errores"
    }

    private fun Unlockable.toUiState(progress: PlayerProgress): UnlockableUiState =
        UnlockableUiState(
            id = id,
            name = name,
            typeLabel = type.displayLabel(),
            requirementLabel = requirement.displayLabel(),
            isUnlocked = id in progress.unlockedIds || isUnlocked(progress)
        )

    private fun UnlockableType.displayLabel(): String = when (this) {
        UnlockableType.BACKGROUND -> "Fondo"
        UnlockableType.BOARD_STYLE -> "Estilo de tablero"
        UnlockableType.THEME -> "Tema visual"
        UnlockableType.PROFILE_FRAME -> "Marco de perfil"
        UnlockableType.NUMBER_STYLE -> "Estilo de números"
        UnlockableType.BADGE -> "Insignia"
    }

    private fun UnlockRequirement.displayLabel(): String = when (this) {
        is UnlockRequirement.Level -> "Nivel $requiredLevel"
        is UnlockRequirement.AbsoluteMastery -> "Maestría absoluta ×$requiredCount"
        UnlockRequirement.FirstLegendMedal -> "Primera Medalla Leyenda"
    }

}
