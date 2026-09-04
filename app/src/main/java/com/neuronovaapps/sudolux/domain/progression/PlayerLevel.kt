package com.neuronovaapps.sudolux.domain.progression

data class PlayerLevel(
    val level: Int,
    val title: String,
    val totalXp: Int,
    val xpInLevel: Int,
    val xpForNextLevel: Int?,
    val progress: Float
) {
    val xpRemaining: Int get() = (xpForNextLevel?.minus(xpInLevel) ?: 0).coerceAtLeast(0)
}

object PlayerLevelCalculator {
    const val MAX_LEVEL = 100

    fun xpRequiredForNextLevel(currentLevel: Int): Int {
        require(currentLevel in 1 until MAX_LEVEL) { "El nivel debe estar entre 1 y 99." }
        return 100 + currentLevel * 35
    }

    fun calculate(totalXp: Int): PlayerLevel {
        val safeTotalXp = totalXp.coerceAtLeast(0)
        var level = 1
        var xpInLevel = safeTotalXp
        while (level < MAX_LEVEL) {
            val required = xpRequiredForNextLevel(level)
            if (xpInLevel < required) break
            xpInLevel -= required
            level++
        }
        val required = if (level == MAX_LEVEL) null else xpRequiredForNextLevel(level)
        return PlayerLevel(
            level = level,
            title = titleFor(level),
            totalXp = safeTotalXp,
            xpInLevel = xpInLevel,
            xpForNextLevel = required,
            progress = if (required == null) 1f else xpInLevel.toFloat() / required
        )
    }

    fun titleFor(level: Int): String = when (level.coerceIn(1, MAX_LEVEL)) {
        in 1..5 -> "Novato"
        in 6..10 -> "Principiante"
        in 11..20 -> "Aprendiz"
        in 21..30 -> "Estratega"
        in 31..40 -> "Analista"
        in 41..50 -> "Experto"
        in 51..60 -> "Maestro"
        in 61..70 -> "Gran maestro"
        in 71..80 -> "Élite"
        in 81..90 -> "Maestro legendario"
        in 91..99 -> "Leyenda"
        else -> "Leyenda Sudolux"
    }
}
