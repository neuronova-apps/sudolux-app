package com.neuronovaapps.sudolux.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class SudoluxNavigationContractTest {
    @Test
    fun bottomNavigationKeepsOnlyHomePlayAndProgress() {
        assertEquals(
            listOf("Inicio", "Jugar", "Progreso"),
            SudoluxDestination.entries.map(SudoluxDestination::label)
        )
        assertFalse(SudoluxDestination.entries.any { it.label == "Configuración" })
    }
}
