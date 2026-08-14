package com.example.sudoluxapp.ui.game

import com.example.sudoluxapp.domain.premium.AccessTier
import com.example.sudoluxapp.domain.progression.ExtraHintConfirmation
import com.example.sudoluxapp.domain.progression.GameMode
import com.example.sudoluxapp.domain.progression.GamePerformance
import com.example.sudoluxapp.domain.progression.GameResult
import com.example.sudoluxapp.domain.progression.PremiumContinuationPolicy
import com.example.sudoluxapp.domain.progression.XpCalculator
import com.example.sudoluxapp.domain.sudoku.CandidateConflict
import com.example.sudoluxapp.domain.sudoku.SudokuCandidateHelper
import com.example.sudoluxapp.domain.sudoku.SudokuPuzzle
import java.util.UUID

data class GameSnapshot(
    val values: List<Int>,
    val notes: List<Set<Int>>,
    val errors: Int,
    val activeNumber: Int?
)

data class GameCompletion(
    val result: GameResult,
    val levelReached: Int?,
    val newlyUnlockedNames: List<String>,
    val newAbsoluteMasteryAchievement: Boolean
)

data class SudokuGameState(
    val puzzle: SudokuPuzzle,
    val gameId: String = UUID.randomUUID().toString(),
    val mode: GameMode = GameMode.WITH_HINTS,
    val values: List<Int> = puzzle.initialBoard,
    val notes: List<Set<Int>> = List(SudokuPuzzle.CELL_COUNT) { emptySet() },
    val selectedCell: Int? = null,
    val activeNumber: Int? = null,
    val notesMode: Boolean = false,
    val errors: Int = 0,
    val history: List<GameSnapshot> = emptyList(),
    val feedback: String? = null,
    val isPaused: Boolean = false,
    val showVictory: Boolean = false,
    val hintsUsed: Int = 0,
    val xpPossible: Int = XpCalculator.potentialXp(puzzle.difficulty, mode, hintsUsed, errors),
    val premiumContinuationUsed: Boolean = false,
    val premiumContinuationPending: Boolean = false,
    val attemptFinished: Boolean = false,
    val pendingExtraHint: ExtraHintConfirmation? = null,
    val completion: GameCompletion? = null
) {
    init {
        require(values.size == SudokuPuzzle.CELL_COUNT) { "El estado debe tener 81 valores." }
        require(notes.size == SudokuPuzzle.CELL_COUNT) { "El estado debe tener 81 conjuntos de notas." }
        require(gameId.isNotBlank()) { "La partida debe tener un identificador." }
        require(hintsUsed >= 0) { "Las pistas utilizadas no pueden ser negativas." }
        require(xpPossible >= 0) { "El XP posible no puede ser negativo." }
        require(mode == GameMode.WITH_HINTS || hintsUsed == 0) {
            "La modalidad Sin pistas no admite pistas utilizadas."
        }
        require(activeNumber == null || activeNumber in 1..9) {
            "El número activo debe estar entre 1 y 9."
        }
    }

    val hasReachedErrorLimit: Boolean
        get() = errors >= MAX_ERRORS && !premiumContinuationUsed
    val hintsRemaining: Int get() = (INCLUDED_HINTS - hintsUsed).coerceAtLeast(0)
    val hintLabel: String
        get() = if (mode == GameMode.NO_HINTS) "Sin pistas"
        else "Pistas $hintsRemaining/$INCLUDED_HINTS"
    val hintButtonLabel: String
        get() = if (hintsUsed < INCLUDED_HINTS) "Pista" else "Pista extra"
    val potentialXp: Int get() = xpPossible
    val completedNumbers: Set<Int>
        get() = (1..9).filterTo(mutableSetOf()) { number ->
            values.indices.count { index ->
                values[index] == number && puzzle.solution[index] == number
            } == 9
        }
    val matchingCandidateCells: Set<Int>
        get() = SudokuCandidateHelper.matchingCandidateCells(notes, activeNumber)

    fun isNumberCompleted(number: Int): Boolean = number in completedNumbers
    fun isGiven(index: Int): Boolean = puzzle.isGiven(index)

    fun select(index: Int): SudokuGameState =
        if (index in values.indices) {
            copy(
                selectedCell = index,
                activeNumber = values[index].takeIf { it != 0 },
                feedback = null
            )
        } else {
            this
        }

    fun toggleNotes(): SudokuGameState = copy(notesMode = !notesMode, feedback = null)

    fun enter(
        number: Int,
        accessTier: AccessTier = AccessTier.FREE,
        autoCleanNotes: Boolean = true,
        showErrorsImmediately: Boolean = true
    ): SudokuGameState {
        val index = selectedCell ?: return withPrompt("Selecciona primero una casilla editable.")
        if (isPaused || attemptFinished || premiumContinuationPending || showVictory || isGiven(index)) return this
        if (number !in 1..9 || isNumberCompleted(number)) return this

        val snapshot = currentSnapshot()
        if (notesMode) {
            if (values[index] != 0) return withPrompt("Borra el número antes de añadir notas.")
            if (number !in notes[index] &&
                !SudokuCandidateHelper.isCandidateAllowed(values, index, number)
            ) {
                val conflict = SudokuCandidateHelper.candidateConflict(values, index, number)
                val message = conflict?.feedbackMessage(number)
                    ?: "El $number no es un candidato válido para esta casilla."
                return copy(activeNumber = number, feedback = message)
            }
            val updatedNotes = notes.toMutableList()
            updatedNotes[index] = if (number in notes[index]) notes[index] - number else notes[index] + number
            return copy(
                notes = updatedNotes,
                activeNumber = number,
                history = history + snapshot,
                feedback = null
            )
        }

        if (number != puzzle.solution[index]) {
            val newErrors = errors + 1
            val shouldOfferPremium = accessTier == AccessTier.PREMIUM &&
                !premiumContinuationUsed && newErrors == MAX_ERRORS
            val updatedXp = when {
                premiumContinuationUsed -> PremiumContinuationPolicy.applyAdditionalErrorPenalty(
                    currentXp = xpPossible,
                    difficulty = puzzle.difficulty
                )
                shouldOfferPremium -> xpPossible
                else -> XpCalculator.potentialXp(puzzle.difficulty, mode, hintsUsed, newErrors)
            }
            return copy(
                activeNumber = number,
                errors = newErrors,
                xpPossible = updatedXp,
                premiumContinuationPending = shouldOfferPremium,
                attemptFinished = !shouldOfferPremium && !premiumContinuationUsed && newErrors >= MAX_ERRORS,
                history = history + snapshot,
                feedback = if (showErrorsImmediately) {
                    "Número incorrecto. Error $newErrors de $MAX_ERRORS."
                } else {
                    null
                }
            )
        }

        val updatedValues = values.toMutableList().apply { this[index] = number }
        val updatedNotes = if (autoCleanNotes) {
            SudokuCandidateHelper.cleanNotesAfterPlacement(notes, index, number)
        } else {
            notes.toMutableList().apply { this[index] = emptySet() }
        }
        val completed = updatedValues == puzzle.solution
        return copy(
            values = updatedValues,
            notes = updatedNotes,
            activeNumber = number,
            history = history + snapshot,
            feedback = if (completed) null else "¡Bien! El $number encaja en esta casilla.",
            showVictory = completed
        )
    }

    fun erase(): SudokuGameState {
        val index = selectedCell ?: return withPrompt("Selecciona primero una casilla editable.")
        if (isPaused || attemptFinished || premiumContinuationPending || showVictory || isGiven(index)) return this
        if (values[index] == 0 && notes[index].isEmpty()) return withPrompt("La casilla ya está vacía.")

        val updatedValues = values.toMutableList().apply { this[index] = 0 }
        val updatedNotes = notes.toMutableList().apply { this[index] = emptySet() }
        return copy(
            values = updatedValues,
            notes = updatedNotes,
            activeNumber = null,
            history = history + currentSnapshot(),
            feedback = "Casilla borrada."
        )
    }

    fun undo(): SudokuGameState {
        val previous = history.lastOrNull() ?: return withPrompt("No hay acciones para deshacer.")
        if (premiumContinuationUsed || premiumContinuationPending) {
            return withPrompt("No se puede deshacer después de alcanzar el límite de errores.")
        }
        return copy(
            values = previous.values,
            notes = previous.notes,
            errors = previous.errors,
            activeNumber = previous.activeNumber,
            xpPossible = XpCalculator.potentialXp(puzzle.difficulty, mode, hintsUsed, previous.errors),
            history = history.dropLast(1),
            feedback = "Última acción deshecha.",
            showVictory = false,
            completion = null
        )
    }

    fun hint(revealNumber: Boolean = false): SudokuGameState {
        if (isPaused || attemptFinished || premiumContinuationPending || showVictory) return this
        if (mode == GameMode.NO_HINTS) return withPrompt("Las pistas están desactivadas en esta modalidad.")
        hintTarget() ?: return withPrompt("El tablero ya está completo.")
        if (hintsUsed >= INCLUDED_HINTS) {
            return copy(
                pendingExtraHint = XpCalculator.extraHintConfirmation(
                    difficulty = puzzle.difficulty,
                    hintsUsed = hintsUsed,
                    errors = errors.coerceAtMost(MAX_ERRORS - 1)
                ),
                feedback = null
            )
        }
        return consumeHint(revealNumber)
    }

    fun confirmExtraHint(revealNumber: Boolean = false): SudokuGameState {
        if (pendingExtraHint == null || mode != GameMode.WITH_HINTS) return this
        return consumeHint(revealNumber).copy(pendingExtraHint = null)
    }

    fun cancelExtraHint(): SudokuGameState =
        if (pendingExtraHint == null) this else copy(pendingExtraHint = null)

    fun togglePause(): SudokuGameState = copy(isPaused = !isPaused, feedback = null)
    fun dismissVictory(): SudokuGameState = copy(showVictory = false)
    fun retry(): SudokuGameState = SudokuGameState(puzzle = puzzle, mode = mode)

    fun continueWithPremiumPenalty(): SudokuGameState {
        if (!premiumContinuationPending || premiumContinuationUsed) return this
        return copy(
            premiumContinuationUsed = true,
            premiumContinuationPending = false,
            xpPossible = PremiumContinuationPolicy.applyInitialPenalty(xpPossible, puzzle.difficulty),
            feedback = "Continuación Premium activada. Se redujo el XP posible un 30 %."
        )
    }

    fun finishPremiumAttempt(): SudokuGameState =
        if (!premiumContinuationPending) this
        else copy(
            premiumContinuationPending = false,
            attemptFinished = true,
            xpPossible = 0,
            feedback = null
        )

    fun performance(completed: Boolean): GamePerformance = GamePerformance(
        difficulty = puzzle.difficulty,
        mode = mode,
        hintsUsed = hintsUsed,
        errors = errors,
        completed = completed,
        premiumContinuationUsed = premiumContinuationUsed,
        premiumAdjustedXp = xpPossible.takeIf { premiumContinuationUsed }
    )

    private fun consumeHint(revealNumber: Boolean): SudokuGameState {
        val index = hintTarget() ?: return withPrompt("El tablero ya está completo.")
        val hintedNumber = puzzle.solution[index]
        val nextHintsUsed = hintsUsed + 1
        val nextXp = if (premiumContinuationUsed) {
            val comparableErrors = errors.coerceAtMost(MAX_ERRORS - 1)
            val before = XpCalculator.potentialXp(puzzle.difficulty, mode, hintsUsed, comparableErrors)
            val after = XpCalculator.potentialXp(puzzle.difficulty, mode, nextHintsUsed, comparableErrors)
            (xpPossible - (before - after)).coerceAtLeast(XpCalculator.minimumReward(puzzle.difficulty))
        } else {
            XpCalculator.potentialXp(puzzle.difficulty, mode, nextHintsUsed, errors)
        }
        if (!revealNumber) return copy(
            selectedCell = index,
            activeNumber = hintedNumber,
            hintsUsed = nextHintsUsed,
            xpPossible = nextXp,
            pendingExtraHint = null,
            feedback = "Pista: revisa esta casilla. El número correcto es $hintedNumber."
        )

        val updatedValues = values.toMutableList().apply { this[index] = hintedNumber }
        return copy(
            values = updatedValues,
            notes = SudokuCandidateHelper.cleanNotesAfterPlacement(notes, index, hintedNumber),
            selectedCell = index,
            activeNumber = hintedNumber,
            hintsUsed = nextHintsUsed,
            xpPossible = nextXp,
            pendingExtraHint = null,
            history = history + currentSnapshot(),
            showVictory = updatedValues == puzzle.solution,
            feedback = "Pista aplicada: se reveló el $hintedNumber."
        )
    }

    private fun hintTarget(): Int? = selectedCell
        ?.takeIf { !isGiven(it) && values[it] == 0 }
        ?: values.indices.firstOrNull { !isGiven(it) && values[it] == 0 }

    private fun currentSnapshot() = GameSnapshot(values, notes, errors, activeNumber)
    private fun withPrompt(message: String) = copy(feedback = message)

    private fun CandidateConflict.feedbackMessage(number: Int): String = when (this) {
        CandidateConflict.ROW -> "El $number ya está presente en esta fila."
        CandidateConflict.COLUMN -> "El $number ya está presente en esta columna."
        CandidateConflict.BLOCK -> "El $number ya está presente en este bloque."
    }

    companion object {
        const val MAX_ERRORS = 3
        const val INCLUDED_HINTS = 3
    }
}
