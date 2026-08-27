package com.example.sudoluxapp.ui.progress

import com.example.sudoluxapp.domain.progression.Medal
import com.example.sudoluxapp.domain.progression.PlayerLevelCalculator
import com.example.sudoluxapp.domain.progression.PlayerProgress
import com.example.sudoluxapp.domain.progression.ProgressionCalculator
import com.example.sudoluxapp.domain.progression.UnlockRequirement
import com.example.sudoluxapp.domain.progression.Unlockable
import com.example.sudoluxapp.domain.progression.UnlockableCatalog
import com.example.sudoluxapp.domain.progression.UnlockableType
import com.example.sudoluxapp.domain.sudoku.SudokuDifficulty

data class ProgressLevelUiState(
    val level: Int,
    val title: String,
    val totalXp: Int,
    val xpInLevel: Int,
    val xpForNextLevel: Int?,
    val xpRemaining: Int,
    val fraction: Float,
    val isMaximum: Boolean
)

data class MedalUiState(val medal: Medal, val count: Int)

data class MasteryMilestoneUiState(val target: Int, val reached: Boolean)

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
    val statistics: GameStatisticsUiState,
    val unlocked: List<UnlockableUiState>,
    val locked: List<UnlockableUiState>,
    val nextUnlock: UnlockableUiState?
)

/** Convierte el dominio persistido en texto y estados listos para representar. */
object ProgressScreenPresenter {
    fun present(progress: PlayerProgress): ProgressScreenUiState {
        val level = progress.level
        val unlocks = UnlockableCatalog.all.mapIndexed { index, unlockable ->
            PresentedUnlockable(
                catalogIndex = index,
                value = unlockable.toUiState(progress),
                completion = unlockable.requirementCompletion(progress)
            )
        }
        val locked = unlocks.filterNot { it.value.isUnlocked }
        val next = locked.maxWithOrNull(
            compareBy<PresentedUnlockable> { it.completion }
                .thenBy { -it.catalogIndex }
        )

        val statistics = progress.statistics
        return ProgressScreenUiState(
            level = ProgressLevelUiState(
                level = level.level,
                title = level.title,
                totalXp = level.totalXp,
                xpInLevel = level.xpInLevel,
                xpForNextLevel = level.xpForNextLevel,
                xpRemaining = level.xpRemaining,
                fraction = level.progress,
                isMaximum = level.level == PlayerLevelCalculator.MAX_LEVEL
            ),
            medals = Medal.entries.map { MedalUiState(it, progress.medalCount(it)) },
            absoluteMasteryCount = progress.absoluteMasteryCount,
            masteryMilestones = ProgressionCalculator.absoluteMasteryMilestones.map { target ->
                MasteryMilestoneUiState(target, progress.absoluteMasteryCount >= target)
            },
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
            unlocked = unlocks.filter { it.value.isUnlocked }.map { it.value },
            locked = locked.map { it.value },
            nextUnlock = next?.value
        )
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

    private fun Unlockable.requirementCompletion(progress: PlayerProgress): Float =
        when (val condition = requirement) {
            is UnlockRequirement.Level ->
                progress.currentLevel.toFloat() / condition.requiredLevel.coerceAtLeast(1)

            is UnlockRequirement.AbsoluteMastery ->
                progress.absoluteMasteryCount.toFloat() / condition.requiredCount.coerceAtLeast(1)

            UnlockRequirement.FirstLegendMedal -> progress.legendMedalCount.toFloat()
        }.coerceIn(0f, 1f)

    private data class PresentedUnlockable(
        val catalogIndex: Int,
        val value: UnlockableUiState,
        val completion: Float
    )
}
