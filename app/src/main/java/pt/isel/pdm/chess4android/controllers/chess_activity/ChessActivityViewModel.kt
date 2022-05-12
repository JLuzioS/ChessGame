package pt.isel.pdm.chess4android.controllers.chess_activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import pt.isel.pdm.chess4android.models.games.Player
import pt.isel.pdm.chess4android.models.games.PromoteCandidate
import pt.isel.pdm.chess4android.models.games.chess.Chess

const val CHESS_ACTIVITY_VIEW_STATE = "ChessActivity.ViewState"

open class ChessActivityViewModel(
    application: Application,
    private val state: SavedStateHandle
) : AndroidViewModel(application) {

    protected var _whitePlayer = Player.Bottom
    val whitePlayer get() = _whitePlayer

    protected val _boardModel: MutableLiveData<Chess> = MutableLiveData()
    val boardModel: LiveData<Chess> get() = _boardModel

    private val _isInPromote: MutableLiveData<PromoteCandidate> = MutableLiveData()
    val isInPromote: LiveData<PromoteCandidate> get() = _isInPromote

    protected val _isGameOver: MutableLiveData<Boolean> = MutableLiveData(false)
    val isGameOver: LiveData<Boolean> get() = _isGameOver

    fun setBoardModel(boardModel: Chess) {
        this._boardModel.value = boardModel
    }

    fun savePromoteStatus(isInPromote: PromoteCandidate) {
        this._isInPromote.value = isInPromote
    }

    fun endGame() {
        _isGameOver.value = true
    }


}