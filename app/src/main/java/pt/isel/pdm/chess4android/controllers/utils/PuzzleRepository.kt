package pt.isel.pdm.chess4android.controllers.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pt.isel.pdm.chess4android.controllers.puzzle_history_activity.HistoryPuzzleDao
import pt.isel.pdm.chess4android.controllers.puzzle_history_activity.PuzzleEntity
import pt.isel.pdm.chess4android.models.DailyPuzzleService
import pt.isel.pdm.chess4android.models.PuzzleInfo
import retrofit2.await

class PuzzleRepository(
    private val dailyPuzzleService: DailyPuzzleService,
    private val historyDB: HistoryPuzzleDao
) {

    suspend fun maybeGetTodayPuzzleFromDB(): PuzzleEntity? = withContext(Dispatchers.IO) {
        val puzzle = historyDB.getLastPuzzles(1).firstOrNull()
        if (puzzle?.isTodayPuzzle() == true) {
            puzzle
        } else
            null
    }

    suspend fun getTodayPuzzleFromAPI(): PuzzleInfo = withContext(Dispatchers.IO) {
        val puzzleInfo = dailyPuzzleService.getPuzzle().await()
        PuzzleInfo(puzzleInfo.game, puzzleInfo.puzzle)
    }

    suspend fun asyncSaveToDB(dto: PuzzleInfo) = withContext(Dispatchers.IO) {
        historyDB.insert(
            PuzzleEntity(
                dto.puzzle.id,
                dto,
                dto.timestamp
            )
        )
    }

    suspend fun fetchPuzzleOfDay(mustSaveToDB: Boolean = false): PuzzleInfo =
        withContext(Dispatchers.Unconfined) {
            val result = maybeGetTodayPuzzleFromDB()
            if (result != null)
                result.puzzleInfo
            else {
                val puzzleInfo = getTodayPuzzleFromAPI()
                if (mustSaveToDB)
                    asyncSaveToDB(puzzleInfo)
                puzzleInfo
            }
        }
}