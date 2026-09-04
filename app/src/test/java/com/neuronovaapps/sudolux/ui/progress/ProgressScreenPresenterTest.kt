package com.neuronovaapps.sudolux.ui.progress

import com.neuronovaapps.sudolux.domain.progression.Medal
import com.neuronovaapps.sudolux.domain.progression.AchievementBadge
import com.neuronovaapps.sudolux.domain.progression.BoardStyle
import com.neuronovaapps.sudolux.domain.progression.PlayerLevelCalculator
import com.neuronovaapps.sudolux.domain.progression.PlayerProgress
import com.neuronovaapps.sudolux.domain.progression.ProfileFrame
import com.neuronovaapps.sudolux.domain.progression.CompletedGameRecord
import com.neuronovaapps.sudolux.domain.sudoku.SudokuDifficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProgressScreenPresenterTest {
    @Test
    fun newPlayerShowsLevelOneAndZeroXp() {
        val state = ProgressScreenPresenter.present(PlayerProgress())

        assertEquals(1, state.level.level)
        assertEquals("Novato", state.level.title)
        assertEquals(0, state.level.totalXp)
        assertEquals(0, state.level.xpInLevel)
        assertEquals(135, state.level.xpForNextLevel)
        assertEquals(135, state.level.xpRemaining)
        assertEquals(0f, state.level.fraction)
        assertFalse(state.level.isMaximum)
        assertEquals(ProfileFrame.INITIAL, state.level.profileFrame)
        assertEquals(AchievementBadge.entries, state.pendingBadges.map { it.badge })
        assertTrue(state.earnedBadges.isEmpty())
    }

    @Test
    fun accumulatedXpIsPresentedInsideItsRealLevel() {
        val state = ProgressScreenPresenter.present(PlayerProgress(totalXp = 420))

        assertEquals(3, state.level.level)
        assertEquals(420, state.level.totalXp)
        assertEquals(115, state.level.xpInLevel)
        assertEquals(205, state.level.xpForNextLevel)
        assertEquals(90, state.level.xpRemaining)
        assertEquals(115f / 205f, state.level.fraction)
    }

    @Test
    fun everyMedalShowsItsPersistedCount() {
        val counts = Medal.entries.associateWith { medal -> medal.ordinal * 3 + 1 }
        val state = ProgressScreenPresenter.present(PlayerProgress(medalCounts = counts))

        assertEquals(Medal.entries.size, state.medals.size)
        state.medals.forEach { medal ->
            assertEquals(counts.getValue(medal.medal), medal.count)
            assertTrue(medal.isEarned)
            assertEquals(1f, medal.iconAlpha)
        }
    }

    @Test
    fun everyMedalIsDimmedAtZeroAndActiveFromItsOwnFirstUnit() {
        val emptyState = ProgressScreenPresenter.present(PlayerProgress())

        assertEquals(Medal.entries, emptyState.medals.map { it.medal })
        emptyState.medals.forEach { medal ->
            assertEquals(0, medal.count)
            assertFalse(medal.isEarned)
            assertEquals(0.3f, medal.iconAlpha)
        }

        Medal.entries.forEach { earnedMedal ->
            val state = ProgressScreenPresenter.present(
                PlayerProgress(medalCounts = mapOf(earnedMedal to 1))
            )

            state.medals.forEach { medal ->
                if (medal.medal == earnedMedal) {
                    assertEquals(1, medal.count)
                    assertTrue(medal.isEarned)
                    assertEquals(1f, medal.iconAlpha)
                } else {
                    assertEquals(0, medal.count)
                    assertFalse(medal.isEarned)
                    assertEquals(0.3f, medal.iconAlpha)
                }
            }
        }
    }

    @Test
    fun medalCountersAreIndependentAndHaveNoPresentationLimit() {
        val counts = mapOf(
            Medal.BRONZE to 3,
            Medal.SILVER to 0,
            Medal.GOLD to 0,
            Medal.PLATINUM to 17,
            Medal.DIAMOND to 0,
            Medal.LEGEND to 42
        )
        val state = ProgressScreenPresenter.present(PlayerProgress(medalCounts = counts))

        state.medals.forEach { medal ->
            assertEquals(counts.getValue(medal.medal), medal.count)
            assertEquals(medal.count > 0, medal.isEarned)
        }
    }

    @Test
    fun absoluteMasteryShowsCountAndReachedMilestones() {
        val state = ProgressScreenPresenter.present(
            PlayerProgress(medalCounts = mapOf(Medal.LEGEND to 7))
        )

        assertEquals(7, state.absoluteMasteryCount)
        assertEquals(listOf(1, 5, 10, 25), state.masteryMilestones.map { it.target })
        assertEquals(listOf(true, true, false, false), state.masteryMilestones.map { it.reached })
    }

    @Test
    fun persistedUnlocksArePresentedAsUnlocked() {
        val state = ProgressScreenPresenter.present(
            PlayerProgress(unlockedIds = setOf("background_1", "legend_frame"))
        )

        assertTrue(state.unlocked.any { it.id == "background_1" && it.isUnlocked })
        assertTrue(state.unlocked.any { it.id == "legend_frame" && it.isUnlocked })
        assertFalse(state.locked.any { it.id == "background_1" || it.id == "legend_frame" })
    }

    @Test
    fun pendingAndEarnedBadgesAreDerivedAsDisjointSets() {
        val state = ProgressScreenPresenter.present(
            PlayerProgress(
                totalXp = xpToReach(15),
                medalCounts = mapOf(Medal.BRONZE to 1)
            )
        )

        assertTrue(state.earnedBadges.any { it.badge == AchievementBadge.FIRST_STEP })
        assertTrue(state.earnedBadges.any { it.badge == AchievementBadge.ASCENT })
        assertFalse(state.pendingBadges.any { it.badge == AchievementBadge.FIRST_STEP })
        assertFalse(state.pendingBadges.any { it.badge == AchievementBadge.ASCENT })
        assertTrue(state.pendingBadges.any { it.badge == AchievementBadge.ADVANCED })
        assertTrue(
            state.pendingBadges.map { it.badge }.intersect(state.earnedBadges.map { it.badge }.toSet())
                .isEmpty()
        )
    }

    @Test
    fun levelOneHundredShowsMaximumWithoutNextLevel() {
        val state = ProgressScreenPresenter.present(
            PlayerProgress(totalXp = xpToReach(PlayerLevelCalculator.MAX_LEVEL))
        )

        assertEquals(100, state.level.level)
        assertEquals("Leyenda Sudolux", state.level.title)
        assertTrue(state.level.isMaximum)
        assertNull(state.level.xpForNextLevel)
        assertEquals(0, state.level.xpRemaining)
        assertEquals(1f, state.level.fraction)
        assertEquals(ProfileFrame.LEGEND, state.level.profileFrame)
    }

    @Test
    fun pendingBadgesAreEmptyWhenEveryAchievementIsObtained() {
        val state = ProgressScreenPresenter.present(
            PlayerProgress(
                totalXp = xpToReach(PlayerLevelCalculator.MAX_LEVEL),
                medalCounts = mapOf(
                    Medal.GOLD to 1,
                    Medal.DIAMOND to 1,
                    Medal.LEGEND to 25
                )
            )
        )

        assertTrue(state.locked.isEmpty())
        assertTrue(state.pendingBadges.isEmpty())
        assertEquals(AchievementBadge.entries, state.earnedBadges.map { it.badge })
    }

    @Test
    fun statisticsPresentAllDifficultiesAndPreserveHistoricalUnclassifiedTotal() {
        val progress = PlayerProgress(
            medalCounts = mapOf(Medal.BRONZE to 4),
            completedGameRecords = mapOf(
                "easy-clean" to CompletedGameRecord(SudokuDifficulty.EASY, 0, 10L, 75),
                "easy-helped" to CompletedGameRecord(SudokuDifficulty.EASY, 1, 20L, 60)
            )
        )

        val statistics = ProgressScreenPresenter.present(progress).statistics
        val easy = statistics.difficulties.single { it.difficulty == SudokuDifficulty.EASY }

        assertEquals(SudokuDifficulty.entries, statistics.difficulties.map { it.difficulty })
        assertEquals(1, easy.withoutHints)
        assertEquals(1, easy.withHints)
        assertEquals(2, easy.total)
        assertEquals(4, statistics.totalCompleted)
        assertEquals(2, statistics.historicalUnclassified)
    }

    @Test
    fun achievementSectionsContainOnlyRewardsThatBelongInEachSection() {
        val progress = PlayerProgress(
            totalXp = xpToReach(30),
            medalCounts = mapOf(
                Medal.BRONZE to 1,
                Medal.GOLD to 1,
                Medal.DIAMOND to 1,
                Medal.LEGEND to 1
            ),
            selectedBoardStyle = com.neuronovaapps.sudolux.domain.progression.BoardStyle.ADVANCED
        )

        val state = ProgressScreenPresenter.present(progress)

        assertEquals(
            listOf(
                "Predeterminado del tema",
                "Tablero alternativo",
                "Tablero avanzado",
                "Tablero experto",
                "Tablero Gran maestro",
                "Tablero exclusivo"
            ),
            state.boardStyles.map { it.name }
        )
        assertEquals(BoardStyle.entries, state.boardStyles.map { it.style })
        assertTrue(state.boardStyles.single { it.style == BoardStyle.DEFAULT }.isUnlocked)
        assertTrue(state.boardStyles.single { it.style == BoardStyle.ADVANCED }.isUnlocked)
        assertFalse(state.boardStyles.single { it.style == BoardStyle.EXPERT }.isUnlocked)
        assertFalse(state.boardStyles.single { it.style == BoardStyle.EXCLUSIVE }.isUnlocked)
        assertTrue(state.boardStyles.single { it.name == "Tablero avanzado" }.isSelected)
        assertEquals(
            listOf("Marco inicial", "Marco avanzado I", "Marco avanzado II"),
            state.profileFrames.map { it.name }
        )
        assertTrue(state.profileFrames.single { it.name == "Marco avanzado II" }.isCurrent)
        assertTrue(state.earnedBadges.any { it.name == "Primer paso" })
        assertTrue(state.earnedBadges.any { it.name == "Desafío superado" })
        assertTrue(state.earnedBadges.any { it.name == "Maestro" })
        assertTrue(state.earnedBadges.any { it.name == "Primera Leyenda" })
        assertFalse(state.upcomingUnlocks.any { it.name == "Marco avanzado II" })
        assertFalse(state.upcomingUnlocks.any { it.name == "Tablero avanzado" })
        assertFalse(state.upcomingUnlocks.any { it.name == "Primera Leyenda" })
        assertFalse(state.upcomingUnlocks.any { it.name == "Maestría absoluta ×1" })
        assertFalse(state.upcomingUnlocks.any { upcoming ->
            AchievementBadge.entries.any { it.displayName == upcoming.name }
        })
    }

    private fun xpToReach(level: Int): Int =
        (1 until level).sumOf(PlayerLevelCalculator::xpRequiredForNextLevel)
}
