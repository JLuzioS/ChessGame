package pt.isel.pdm.chess4android.models.games.chess


import pt.isel.pdm.chess4android.models.games.Movement
import pt.isel.pdm.chess4android.models.games.Player
import pt.isel.pdm.chess4android.models.games.Position

class Puzzle(
    private val solutionMoves: Array<Movement>,
    whitePlayerPosition: Player, MAX_HEIGHT: Int, MAX_WIDTH: Int) :
    Chess(whitePlayerPosition, MAX_HEIGHT, MAX_WIDTH) {

    var index = 0
    var settingUp = true

    fun endSetup(){
        settingUp = false
    }

    override fun movePieceAtPosition(oldPosition: Position, newPosition: Position): Boolean {
        if (settingUp)
            return super.movePieceAtPosition(oldPosition, newPosition)
        if (solutionMoves.isEmpty()) return false
        if (index < solutionMoves.size) {
            val nextMovement = solutionMoves[index]
            if (nextMovement.origin == oldPosition && nextMovement.destination == newPosition) {
                val res = super.movePieceAtPosition(oldPosition, newPosition)
                if (res) {
                    index++
                    return true
                }
            }
        }
        return false
    }

    fun makeNextMove(): Boolean {
        if (solutionMoves.isEmpty()) return false
        if (index < solutionMoves.size) {
            val nextMovement = solutionMoves[index]
            val res = super.movePieceAtPosition(nextMovement.origin, nextMovement.destination)
            if (res) {
                index++
                return true
            }
        }
        return false

    }

    fun undoLastMove(): Boolean {
        if (index > 0) {
            // TODO doesn't revert EnPassant or Towering
            val lastMovement = moveHistory.removeLast()
            board[lastMovement.origin.x][lastMovement.origin.y] = lastMovement.pieceAtOrigin
            lastMovement.pieceAtOrigin?.position = lastMovement.origin
            board[lastMovement.destination.x][lastMovement.destination.y] = lastMovement.pieceAtDestination
            lastMovement.pieceAtDestination?.position = lastMovement.destination
            _currentPlayer = if (_currentPlayer == Player.Top) Player.Bottom else Player.Top
            index--
            return true
        }
        return false
    }

    fun isPuzzleOver(): Boolean = index == solutionMoves.size
}

