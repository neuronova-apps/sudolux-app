package com.neuronovaapps.sudolux.domain.progression

enum class BoardStyle(
    val displayName: String,
    val requirementLabel: String
) {
    DEFAULT("Predeterminado del tema", "Disponible desde el inicio"),
    ALTERNATIVE("Tablero alternativo", "Nivel 5"),
    ADVANCED("Tablero avanzado", "Nivel 25"),
    EXPERT("Tablero experto", "Nivel 40"),
    GRAND_MASTER("Tablero Gran maestro", "Nivel 60"),
    EXCLUSIVE("Tablero exclusivo", "Maestría absoluta ×10");

    fun isUnlocked(progress: PlayerProgress): Boolean = when (this) {
        DEFAULT -> true
        ALTERNATIVE -> progress.currentLevel >= 5
        ADVANCED -> progress.currentLevel >= 25
        EXPERT -> progress.currentLevel >= 40
        GRAND_MASTER -> progress.currentLevel >= 60
        EXCLUSIVE -> progress.legendMedalCount >= 10
    }
}

enum class ProfileFrame(
    val displayName: String,
    val requiredLevel: Int
) {
    INITIAL("Marco inicial", 1),
    ADVANCED_1("Marco avanzado I", 15),
    ADVANCED_2("Marco avanzado II", 30),
    MASTER("Marco Maestro", 50),
    ELITE("Marco Élite", 70),
    LEGEND("Marco Leyenda", 100);

    val requirementLabel: String get() = "Nivel $requiredLevel"

    fun isUnlocked(progress: PlayerProgress): Boolean = progress.currentLevel >= requiredLevel

    companion object {
        fun currentFor(level: Int): ProfileFrame = entries.last { level >= it.requiredLevel }
    }
}

enum class AchievementBadge(
    val displayName: String,
    val requirementLabel: String
) {
    FIRST_STEP("Primer paso", "Completar el primer Sudoku"),
    ASCENT("Ascenso", "Alcanzar nivel 15"),
    ADVANCED("Avanzado", "Alcanzar nivel 30"),
    CHALLENGE_COMPLETE("Desafío superado", "Obtener la primera Medalla Oro"),
    MASTER("Maestro", "Obtener la primera Medalla Diamante"),
    FIRST_LEGEND("Primera Leyenda", "Obtener la primera Medalla Leyenda"),
    GRAND_MASTER("Gran maestro", "Alcanzar nivel 60"),
    SUDOLUX_ELITE("Élite Sudolux", "Alcanzar nivel 70"),
    SUDOLUX_LEGEND("Leyenda Sudolux", "Alcanzar nivel 100");

    fun isUnlocked(progress: PlayerProgress): Boolean = when (this) {
        FIRST_STEP -> progress.completedSudokus >= 1
        ASCENT -> progress.currentLevel >= 15
        ADVANCED -> progress.currentLevel >= 30
        CHALLENGE_COMPLETE -> progress.medalCount(Medal.GOLD) >= 1
        MASTER -> progress.medalCount(Medal.DIAMOND) >= 1
        FIRST_LEGEND -> progress.legendMedalCount >= 1
        GRAND_MASTER -> progress.currentLevel >= 60
        SUDOLUX_ELITE -> progress.currentLevel >= 70
        SUDOLUX_LEGEND -> progress.currentLevel >= 100
    }
}
