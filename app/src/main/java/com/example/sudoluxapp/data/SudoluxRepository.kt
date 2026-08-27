package com.example.sudoluxapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.sudoluxapp.domain.progression.CompletedGameRecord
import com.example.sudoluxapp.domain.progression.GameCompletionStatus
import com.example.sudoluxapp.domain.progression.GameMode
import com.example.sudoluxapp.domain.progression.Medal
import com.example.sudoluxapp.domain.progression.PlayerProgress
import com.example.sudoluxapp.domain.sudoku.SudokuDifficulty
import com.example.sudoluxapp.domain.sudoku.SudokuPuzzle
import com.example.sudoluxapp.ui.game.SudokuGameState

interface KeyValueStorage {
    fun get(key: String): String?
    fun replace(removedKeys: Set<String>, values: Map<String, String>)
}

class SharedPreferencesKeyValueStorage(context: Context) : KeyValueStorage {
    private val preferences: SharedPreferences = context.getSharedPreferences(
        FILE_NAME,
        Context.MODE_PRIVATE
    )

    override fun get(key: String): String? = preferences.getString(key, null)

    override fun replace(removedKeys: Set<String>, values: Map<String, String>) {
        preferences.edit(commit = true) {
            removedKeys.forEach { remove(it) }
            values.forEach { (key, value) -> putString(key, value) }
        }
    }

    private companion object {
        const val FILE_NAME = "sudolux_persistent_state_v1"
    }
}

/** Fuente persistente única para progreso global y partida activa. */
class SudoluxRepository(private val storage: KeyValueStorage) {
    fun loadProgress(): PlayerProgress {
        val totalXp = storage.get(PROGRESS_XP)?.toIntOrNull()?.coerceAtLeast(0) ?: 0
        val mastery = storage.get(PROGRESS_MASTERY)?.toIntOrNull()?.coerceAtLeast(0) ?: 0
        val medals = Medal.entries.associateWith { medal ->
            storage.get("$PROGRESS_MEDAL_PREFIX${medal.name}")?.toIntOrNull()?.coerceAtLeast(0) ?: 0
        }
        return PlayerProgress(
            totalXp = totalXp,
            medalCounts = medals,
            absoluteMasteryCount = mastery,
            unlockedIds = decodeSet(storage.get(PROGRESS_UNLOCKS)),
            processedGameIds = decodeSet(storage.get(PROGRESS_PROCESSED_GAMES)),
            completedGameRecords = decodeCompletedGames(storage.get(PROGRESS_COMPLETED_GAMES))
        )
    }

    fun saveProgress(progress: PlayerProgress) {
        val values = buildMap {
            put(PROGRESS_XP, progress.totalXp.toString())
            put(PROGRESS_MASTERY, progress.absoluteMasteryCount.toString())
            put(PROGRESS_UNLOCKS, encodeSet(progress.unlockedIds))
            put(PROGRESS_PROCESSED_GAMES, encodeSet(progress.processedGameIds))
            put(PROGRESS_COMPLETED_GAMES, encodeCompletedGames(progress.completedGameRecords))
            Medal.entries.forEach { medal ->
                put("$PROGRESS_MEDAL_PREFIX${medal.name}", progress.medalCount(medal).toString())
            }
        }
        storage.replace(emptySet(), values)
    }

    fun loadActiveGame(): SudokuGameState? {
        if (storage.get(GAME_ACTIVE) != TRUE) return null
        return runCatching {
            val initialBoard = decodeBoard(required(GAME_INITIAL_BOARD), allowZero = true)
            val solution = decodeBoard(required(GAME_SOLUTION), allowZero = false)
            val puzzle = SudokuPuzzle(
                initialBoard = initialBoard,
                solution = solution,
                difficulty = SudokuDifficulty.valueOf(required(GAME_DIFFICULTY)),
                seed = required(GAME_SEED).toLong()
            )
            SudokuGameState(
                puzzle = puzzle,
                gameId = required(GAME_ID),
                mode = GameMode.valueOf(required(GAME_MODE)),
                values = decodeBoard(required(GAME_VALUES), allowZero = true),
                notes = decodeNotes(required(GAME_NOTES)),
                selectedCell = storage.get(GAME_SELECTED)?.toIntOrNull()?.takeIf { it in 0..80 },
                notesMode = storage.get(GAME_NOTES_MODE).toBooleanStrictOrFalse(),
                errors = required(GAME_ERRORS).toInt(),
                hintsUsed = required(GAME_HINTS_USED).toInt(),
                xpPossible = required(GAME_XP_POSSIBLE).toInt(),
                premiumContinuationUsed = storage.get(GAME_PREMIUM_USED).toBooleanStrictOrFalse(),
                premiumContinuationPending = storage.get(GAME_PREMIUM_PENDING).toBooleanStrictOrFalse(),
                attemptFinished = false
            )
        }.getOrElse {
            clearActiveGame()
            null
        }
    }

    fun saveActiveGame(game: SudokuGameState) {
        if (game.showVictory || game.attemptFinished) {
            clearActiveGame()
            return
        }
        val values = mapOf(
            GAME_ACTIVE to TRUE,
            GAME_ID to game.gameId,
            GAME_DIFFICULTY to game.puzzle.difficulty.name,
            GAME_MODE to game.mode.name,
            GAME_INITIAL_BOARD to encodeBoard(game.puzzle.initialBoard),
            GAME_SOLUTION to encodeBoard(game.puzzle.solution),
            GAME_SEED to game.puzzle.seed.toString(),
            GAME_VALUES to encodeBoard(game.values),
            GAME_NOTES to encodeNotes(game.notes),
            GAME_SELECTED to (game.selectedCell ?: -1).toString(),
            GAME_NOTES_MODE to game.notesMode.toString(),
            GAME_ERRORS to game.errors.toString(),
            GAME_HINTS_USED to game.hintsUsed.toString(),
            GAME_XP_POSSIBLE to game.xpPossible.toString(),
            GAME_PREMIUM_USED to game.premiumContinuationUsed.toString(),
            GAME_PREMIUM_PENDING to game.premiumContinuationPending.toString()
        )
        storage.replace(GAME_KEYS, values)
    }

    fun clearActiveGame() = storage.replace(GAME_KEYS, emptyMap())

    private fun required(key: String): String = requireNotNull(storage.get(key)) {
        "Falta el campo persistido $key."
    }

    private fun encodeBoard(values: List<Int>): String = values.joinToString(separator = "")

    private fun decodeBoard(encoded: String, allowZero: Boolean): List<Int> {
        require(encoded.length == SudokuPuzzle.CELL_COUNT)
        return encoded.map { char ->
            val value = char.digitToInt()
            require(value in (if (allowZero) 0..9 else 1..9))
            value
        }
    }

    private fun encodeNotes(notes: List<Set<Int>>): String = notes.joinToString(",") { cell ->
        cell.fold(0) { mask, number -> mask or (1 shl number) }.toString()
    }

    private fun decodeNotes(encoded: String): List<Set<Int>> {
        val masks = encoded.split(',')
        require(masks.size == SudokuPuzzle.CELL_COUNT)
        return masks.map { rawMask ->
            val mask = rawMask.toInt()
            (1..9).filterTo(mutableSetOf()) { number -> mask and (1 shl number) != 0 }
        }
    }

    private fun encodeSet(values: Set<String>): String = values.sorted().joinToString(SET_SEPARATOR)

    private fun decodeSet(value: String?): Set<String> = value
        ?.takeIf(String::isNotEmpty)
        ?.split(SET_SEPARATOR)
        ?.toSet()
        ?: emptySet()

    /**
     * Migración aditiva: la ausencia de esta clave representa progreso histórico sin detalle.
     * Las medallas siguen siendo la fuente del total y nunca se sintetizan dificultad ni pistas.
     */
    private fun decodeCompletedGames(value: String?): Map<String, CompletedGameRecord> = value
        ?.takeIf(String::isNotEmpty)
        ?.split(RECORD_SEPARATOR)
        ?.mapNotNull(::decodeCompletedGame)
        ?.toMap()
        ?: emptyMap()

    private fun encodeCompletedGames(records: Map<String, CompletedGameRecord>): String =
        records.toSortedMap().entries.joinToString(RECORD_SEPARATOR) { (gameId, record) ->
            listOf(
                COMPLETED_GAME_FORMAT_VERSION,
                gameId,
                record.difficulty.name,
                record.hintsUsed.toString(),
                record.completedAtEpochMillis.toString(),
                record.xpEarned.toString(),
                record.status.name
            ).joinToString(FIELD_SEPARATOR)
        }

    private fun decodeCompletedGame(encoded: String): Pair<String, CompletedGameRecord>? =
        runCatching {
            val fields = encoded.split(FIELD_SEPARATOR)
            require(fields.size == 7 && fields[0] == COMPLETED_GAME_FORMAT_VERSION)
            val gameId = fields[1]
            require(gameId.isNotBlank())
            gameId to CompletedGameRecord(
                difficulty = SudokuDifficulty.valueOf(fields[2]),
                hintsUsed = fields[3].toInt(),
                completedAtEpochMillis = fields[4].toLong(),
                xpEarned = fields[5].toInt(),
                status = GameCompletionStatus.valueOf(fields[6])
            )
        }.getOrNull()

    private fun String?.toBooleanStrictOrFalse(): Boolean = this == TRUE

    private companion object {
        const val TRUE = "true"
        const val SET_SEPARATOR = "\u001F"
        const val RECORD_SEPARATOR = "\u001E"
        const val FIELD_SEPARATOR = "\u001D"
        const val COMPLETED_GAME_FORMAT_VERSION = "1"
        const val PROGRESS_XP = "progress.xp"
        const val PROGRESS_MASTERY = "progress.mastery"
        const val PROGRESS_UNLOCKS = "progress.unlocks"
        const val PROGRESS_PROCESSED_GAMES = "progress.processed_games"
        const val PROGRESS_COMPLETED_GAMES = "progress.completed_games"
        const val PROGRESS_MEDAL_PREFIX = "progress.medal."

        const val GAME_ACTIVE = "game.active"
        const val GAME_ID = "game.id"
        const val GAME_DIFFICULTY = "game.difficulty"
        const val GAME_MODE = "game.mode"
        const val GAME_INITIAL_BOARD = "game.initial_board"
        const val GAME_SOLUTION = "game.solution"
        const val GAME_SEED = "game.seed"
        const val GAME_VALUES = "game.values"
        const val GAME_NOTES = "game.notes"
        const val GAME_SELECTED = "game.selected"
        const val GAME_NOTES_MODE = "game.notes_mode"
        const val GAME_ERRORS = "game.errors"
        const val GAME_HINTS_USED = "game.hints_used"
        const val GAME_XP_POSSIBLE = "game.xp_possible"
        const val GAME_PREMIUM_USED = "game.premium_used"
        const val GAME_PREMIUM_PENDING = "game.premium_pending"

        val GAME_KEYS = setOf(
            GAME_ACTIVE, GAME_ID, GAME_DIFFICULTY, GAME_MODE, GAME_INITIAL_BOARD,
            GAME_SOLUTION, GAME_SEED, GAME_VALUES, GAME_NOTES, GAME_SELECTED,
            GAME_NOTES_MODE, GAME_ERRORS, GAME_HINTS_USED, GAME_XP_POSSIBLE,
            GAME_PREMIUM_USED, GAME_PREMIUM_PENDING
        )
    }
}
