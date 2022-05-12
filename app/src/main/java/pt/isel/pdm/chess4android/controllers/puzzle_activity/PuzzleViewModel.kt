package pt.isel.pdm.chess4android.controllers.puzzle_activity

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.isel.pdm.chess4android.controllers.application.PuzzleApplication
import pt.isel.pdm.chess4android.controllers.chess_activity.ChessActivityViewModel
import pt.isel.pdm.chess4android.controllers.puzzle_history_activity.PuzzleEntity
import pt.isel.pdm.chess4android.controllers.utils.PuzzleRepository
import pt.isel.pdm.chess4android.models.PuzzleInfo
import pt.isel.pdm.chess4android.models.PuzzleInfoParser
import pt.isel.pdm.chess4android.models.games.Player
import pt.isel.pdm.chess4android.models.games.chess.Puzzle

const val PUZZLE_ACTIVITY_VIEW_STATE = "PuzzleActivity.ViewState"

class PuzzleViewModel(
    application: Application,
    private val state: SavedStateHandle
) : ChessActivityViewModel(application, state) {

    private val puzzleRepository by lazy {
        PuzzleRepository(
            PuzzleApplication.dailyPuzzleService,
            getApplication<PuzzleApplication>().historyDB.getHistoryPuzzleDao()
        )
    }

    val lichessPuzzle: LiveData<PuzzleInfo> = state.getLiveData(PUZZLE_ACTIVITY_VIEW_STATE)

    fun getLichessPuzzle() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                state.set(PUZZLE_ACTIVITY_VIEW_STATE, puzzleRepository.fetchPuzzleOfDay(true))
            } catch (err: Exception) {
                state.set(PUZZLE_ACTIVITY_VIEW_STATE, null)
            }
        }
    }

    fun playLichessPuzzle(dailyGame: PuzzleInfo) {
        _whitePlayer = if (dailyGame.puzzle.initialPly % 2 == 0) {
            Player.Top
        } else {
            Player.Bottom
        }
        this._boardModel.value = PuzzleInfoParser(dailyGame).parsePuzzlePNG()
    }

    fun undoLastMove() {
        val puzzle = _boardModel.value as Puzzle
        puzzle.undoLastMove()
        setBoardModel(puzzle)
        _isGameOver.value = false
    }

    fun makeNextMove() {
        val puzzle = _boardModel.value as Puzzle
        puzzle.makeNextMove()
        setBoardModel(puzzle)
        if (puzzle.isGameOver() && isGameOver.value == false) {
            endGame()
        }
    }

    fun makeNextMoveWithDelay() {
        viewModelScope.launch {
            delay(1000L)
            makeNextMove()
        }
    }

    fun updatePuzzleSolved(){
        viewModelScope.launch(Dispatchers.IO) {
            val puzzleInfo = lichessPuzzle.value
            if (puzzleInfo != null) {
                puzzleInfo.solved = true
                getApplication<PuzzleApplication>().historyDB.getHistoryPuzzleDao().update(
                    PuzzleEntity(
                        puzzleInfo.puzzle.id,
                        puzzleInfo,
                        puzzleInfo.timestamp
                    )
                )
            }
        }
    }
}