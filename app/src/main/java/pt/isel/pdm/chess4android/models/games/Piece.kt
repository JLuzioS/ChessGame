package pt.isel.pdm.chess4android.models.games


abstract class Piece(val player: Player, var position: Position) {

    protected var _wasFirstMovedMade = false
    val wasFirstMovedMade get() = _wasFirstMovedMade

    fun setFirstMoveMadeFlag(){
        _wasFirstMovedMade = true
    }

    open fun getPositionsInCheck(board: Game): HashSet<Position> {
        return internalGetPositionsInCheck(board)
    }

    abstract fun internalGetPositionsInCheck(board: Game): HashSet<Position>


    open fun getPossibleMoves(board: Game): HashSet<Position> {
        return internalGetPossibleMoves(board)
    }

    protected abstract fun internalGetPossibleMoves(board: Game): HashSet<Position>
}