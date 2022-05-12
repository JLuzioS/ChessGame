package pt.isel.pdm.chess4android.controllers.puzzle_history_activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.isel.pdm.chess4android.controllers.application.PuzzleApplication
import pt.isel.pdm.chess4android.models.PuzzleInfo

class HistoryActivityViewModel(application: Application) : AndroidViewModel(application) {

    var history: LiveData<List<PuzzleInfo>>? = null
        private set

    private val puzzleDao: HistoryPuzzleDao by lazy {
        getApplication<PuzzleApplication>().historyDB.getHistoryPuzzleDao()
    }

    suspend fun loadPuzzleHistoryAsync(): List<PuzzleInfo> = withContext(Dispatchers.IO) {
        try {
            puzzleDao.getAllPuzzles().map {
                it.puzzleInfo
            }
        } catch (err: Exception) {
            emptyList()
        }
    }

    fun loadPuzzleHistory(): LiveData<List<PuzzleInfo>> {
        val publish = MutableLiveData<List<PuzzleInfo>>()
        history = publish
        viewModelScope.launch(Dispatchers.Main) {
            publish.value = loadPuzzleHistoryAsync()!!
        }
        return publish
    }
}