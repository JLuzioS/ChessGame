package pt.isel.pdm.chess4android.controllers.main_activity

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import pt.isel.pdm.chess4android.controllers.application.PuzzleApplication
import pt.isel.pdm.chess4android.controllers.utils.PuzzleRepository

class DownloadDailyPuzzleWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val app: PuzzleApplication = applicationContext as PuzzleApplication
                val repo = PuzzleRepository(
                    PuzzleApplication.dailyPuzzleService,
                    app.historyDB.getHistoryPuzzleDao()
                )
                repo.fetchPuzzleOfDay(mustSaveToDB = true)
                Result.success()
            } catch (err: Exception) {
                Result.failure()
            }
        }
    }
}