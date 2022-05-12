package pt.isel.pdm.chess4android.models.games

import java.util.*
import kotlin.collections.HashSet

abstract class Game(firstPlayer: Player, val MAX_HEIGHT: Int, val MAX_WIDTH: Int) {
    protected var _currentPlayer: Player
    val currentPlayer get() = _currentPlayer
    init {
        _currentPlayer = firstPlayer
    }

    protected val board: Array<Array<Piece?>> = Array(MAX_WIDTH) { Array(MAX_HEIGHT) { null } }
    val playersPieces: EnumMap<Player, HashSet<Piece>> = EnumMap(Player::class.java)

    protected var moveHistory: MutableList<Movement> = mutableListOf()

    abstract fun isGameOver(): Boolean
    abstract fun whichPlayerWon(): Player?

    fun getPiece(position: Position): Piece? {
        if (position.x >= MAX_WIDTH || position.x < 0)
            throw Error("Invalid x position.")
        if (position.y >= MAX_HEIGHT || position.y < 0)
            throw Error("Invalid y position.")
        return board[position.x][position.y]
    }

    fun getPossibleMoves(position: Position): HashSet<Position> {
        val piece: Piece = getPiece(position) ?: return HashSet()
        return piece.getPossibleMoves(this)
    }

    fun getLastMovement(): Movement? {
        return if (moveHistory.isNotEmpty()) { moveHistory.last() } else { null }
    }

    open fun movePieceAtPosition(oldPosition: Position, newPosition: Position) : Boolean {
        if (board[oldPosition.x][oldPosition.y] == null) return false
        if (newPosition !in getPossibleMoves(oldPosition)) return false
        if (board[oldPosition.x][oldPosition.y]?.player != _currentPlayer) return false

        moveHistory.add(
            Movement(
                oldPosition,
                newPosition,
                board[oldPosition.x][oldPosition.y],
                board[newPosition.x][newPosition.y]
            )
        )

        if (board[newPosition.x][newPosition.y] != null) {
            val pieceToRemove = board[newPosition.x][newPosition.y]
            playersPieces[pieceToRemove?.player]?.remove(board[newPosition.x][newPosition.y])
        }

        board[oldPosition.x][oldPosition.y]?.position = newPosition
        board[oldPosition.x][oldPosition.y]?.setFirstMoveMadeFlag()

        board[newPosition.x][newPosition.y] = board[oldPosition.x][oldPosition.y]
        board[oldPosition.x][oldPosition.y] = null

        _currentPlayer = if (_currentPlayer == Player.Top) Player.Bottom else Player.Top
        return true
    }

    fun isPositionValid(positionToCheck: Position): Boolean {
        return positionToCheck.x in 0 until MAX_WIDTH && positionToCheck.y in 0 until MAX_HEIGHT
    }

    protected open fun addPieceToBoard(piece: Piece) : Boolean{
        if (board[piece.position.x][piece.position.y] != null) return false
        board[piece.position.x][piece.position.y] = piece
        if (!playersPieces.containsKey(piece.player)){
            playersPieces[piece.player] = HashSet()
        }
        if (!playersPieces[piece.player]?.contains(piece)!!){
            playersPieces[piece.player]?.add(piece)
        }
        return true
    }
}