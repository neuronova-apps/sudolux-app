package com.neuronovaapps.sudolux.ui.components

import com.neuronovaapps.sudolux.R
import com.neuronovaapps.sudolux.domain.progression.BoardStyle
import com.neuronovaapps.sudolux.domain.progression.PlayerProgress
import com.neuronovaapps.sudolux.domain.settings.AppTheme
import com.neuronovaapps.sudolux.domain.settings.UserSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProgressVisualsTest {
    @Test
    fun everySpecialBoardStyleMapsToItsDefinitiveDrawable() {
        assertNull(BoardStyle.DEFAULT.drawableResOrNull())
        assertEquals(R.drawable.board_alternative, BoardStyle.ALTERNATIVE.drawableResOrNull())
        assertEquals(R.drawable.board_advanced, BoardStyle.ADVANCED.drawableResOrNull())
        assertEquals(R.drawable.board_expert, BoardStyle.EXPERT.drawableResOrNull())
        assertEquals(R.drawable.board_grand_master, BoardStyle.GRAND_MASTER.drawableResOrNull())
        assertEquals(R.drawable.board_exclusive, BoardStyle.EXCLUSIVE.drawableResOrNull())
    }

    @Test
    fun changingThemeDoesNotChangeTheSelectedSpecialBoard() {
        val progress = PlayerProgress(selectedBoardStyle = BoardStyle.ADVANCED)

        AppTheme.entries.forEach { theme ->
            val settings = UserSettings(theme = theme)

            assertEquals(theme, settings.theme)
            assertEquals(BoardStyle.ADVANCED, progress.selectedBoardStyle)
            assertEquals(R.drawable.board_advanced, progress.selectedBoardStyle.drawableResOrNull())
        }
    }
}
