package pt.isel.pdm.chess4android.models.games.chess

import pt.isel.pdm.chess4android.models.games.*
import pt.isel.pdm.chess4android.models.games.chess.pieces.*
import java.util.*
import kotlin.math.abs


open class Chess(firstPlayer: Player, MAX_HEIGHT: Int, MAX_WIDTH: Int) :
    Game(firstPlayer, MAX_HEIGHT, MAX_WIDTH) {

    val playersKing: EnumMap<Player, King> = EnumMap(Player::class.java)

    init {
        for (i in 0..7) {
            addPieceToBoard(Pawn(Player.Top, Position(i, 1)))
            addPieceToBoard(Pawn(Player.Bottom, Position(i, MAX_HEIGHT - 2)))
        }

        // Set Top Player Back Row
        addPieceToBoard(Rook(Player.Top, Position(0, 0)))
        addPieceToBoard(Knight(Player.Top, Position(1, 0)))
        addPieceToBoard(Bishop(Player.Top, Position(2, 0)))
        addPieceToBoard(Bishop(Player.Top, Position(5, 0)))
        addPieceToBoard(Knight(Player.Top, Position(6, 0)))
        addPieceToBoard(Rook(Player.Top, Position(7, 0)))

        // Set Bottom Player Back Row
        addPieceToBoard(Rook(Player.Bottom, Position(0, MAX_HEIGHT - 1)))
        addPieceToBoard(Knight(Player.Bottom, Position(1, MAX_HEIGHT - 1)))
        addPieceToBoard(Bishop(Player.Bottom, Position(2, MAX_HEIGHT - 1)))

        addPieceToBoard(Bishop(Player.Bottom, Position(5, MAX_HEIGHT - 1)))
        addPieceToBoard(Knight(Player.Bottom, Position(6, MAX_HEIGHT - 1)))
        addPieceToBoard(Rook(Player.Bottom, Position(7, MAX_HEIGHT - 1)))

        when (firstPlayer) {
            Player.Bottom -> {
                addPieceToBoard(Queen(Player.Top, Position(3, 0)))
                addPieceToBoard(King(Player.Top, Position(4, 0)))

                addPieceToBoard(Queen(Player.Bottom, Position(3, MAX_HEIGHT - 1)))
                addPieceToBoard(King(Player.Bottom, Position(4, MAX_HEIGHT - 1)))
            }
            Player.Top -> {
                addPieceToBoard(King(Player.Top, Position(3, 0)))
                addPieceToBoard(Queen(Player.Top, Position(4, 0)))

                addPieceToBoard(King(Player.Bottom, Position(3, MAX_HEIGHT - 1)))
                addPieceToBoard(Queen(Player.Bottom, Position(4, MAX_HEIGHT - 1)))
            }
        }


    }

    override fun isGameOver(): Boolean {
        playersPieces[currentPlayer]?.forEach {
            if (it.getPossibleMoves(this).isNotEmpty()) return false
        }
        return true
    }

    override fun whichPlayerWon(): Player? {
        if (isGameOver()) {
            Player.values().forEach { player ->
                if (player != currentPlayer) {
                    playersPieces[player]?.forEach { piece ->
                        if (piece.getPossibleMoves(this).contains(
                                playersKing[currentPlayer]?.position
                            )
                        ) return player
                    }
                }
            }
        }
        return null
    }

    private fun checkEnPassant(lastOpponentMove: Movement, currentMove: Movement): Boolean {
        return lastOpponentMove.pieceAtOrigin is Pawn
                && abs(lastOpponentMove.origin.y - lastOpponentMove.destination.y) == 2
                && currentMove.destination.x == lastOpponentMove.destination.x
                && currentMove.origin.y == lastOpponentMove.destination.y
                && (lastOpponentMove.destination.x == currentMove.origin.x + 1
                || lastOpponentMove.destination.x == currentMove.origin.x - 1)
    }

    override fun movePieceAtPosition(oldPosition: Position, newPosition: Position): Boolean {
        val lastOpponentMove: Movement? = if (moveHistory.isNotEmpty()) moveHistory.last() else null
        val res = super.movePieceAtPosition(oldPosition, newPosition)
        val currentMove = moveHistory.last()

        if (res) {
            val piece = board[newPosition.x][newPosition.y]
            when (piece) {
                // En passant
                is Pawn -> {
                    if (lastOpponentMove?.let { checkEnPassant(it, currentMove) } == true) {
                        val pawn: Piece = lastOpponentMove.pieceAtOrigin!!
                        playersPieces[pawn.player]?.remove(pawn)
                        board[pawn.position.x][pawn.position.y] = null
                    }
                }
                // Castling
                is King -> {
                    if (abs(oldPosition.x - newPosition.x) > 1) {
                        val pos: Position
                        if (newPosition.x < oldPosition.x) {
                            pos = Position(newPosition.x + 1, newPosition.y)
                            board[pos.x][pos.y] = board[0][pos.y]
                            board[0][pos.y] = null
                        } else {
                            pos = Position(newPosition.x - 1, newPosition.y)
                            board[pos.x][pos.y] = board[MAX_WIDTH - 1][pos.y]
                            board[MAX_WIDTH - 1][pos.y] = null
                        }
                        board[pos.x][pos.y]?.position = pos
                    }
                }
            }
        }
        //Log.d("this is my deep array", "board: \n" + toString())
        return res
    }

    final override fun addPieceToBoard(piece: Piece): Boolean {
        if (piece is King) {
            if (board[piece.position.x][piece.position.y] != null) throw Error("Position already has a piece.")
            board[piece.position.x][piece.position.y] = piece
            playersKing[piece.player] = piece
            return true
        } else {
            return super.addPieceToBoard(piece)
        }
    }

    fun isReadyForPromotion(position: Position): Boolean {
        return board[position.x][position.y] is Pawn && (position.y == 0 || position.y == MAX_HEIGHT - 1)
    }

    fun promotePawn(position: Position, promotedClass: Any) {

        val pawn = board[position.x][position.y]!!

        val piece: Piece? = when (promotedClass) {
            Queen::class -> {
                Queen(pawn.player, pawn.position)
            }
            Bishop::class -> {
                Bishop(pawn.player, pawn.position)
            }
            Rook::class -> {
                Rook(pawn.player, pawn.position)
            }
            Knight::class -> {
                Knight(pawn.player, pawn.position)
            }
            else -> null
        }
        if (piece != null) {
            playersPieces[pawn.player]?.remove(pawn)
            playersPieces[piece.player]?.add(piece)
            board[position.x][position.y] = piece
        }
    }

    override fun toString(): String {
        var str = ""

        var piece: Piece?
        var player: String
        for (j in 0 until board[0].size) {
            str += "\n"
            for (i in 0 until board.size) {
                piece = board[i][j]
                str += "|"

                player = when (piece?.player) {
                    Player.Top -> "(T)"
                    Player.Bottom -> "(B)"
                    else -> ""
                }
                str += when (piece) {
                    is Bishop -> "B$player"
                    is Queen -> "Q$player"
                    is King -> "K$player"
                    is Knight -> "N$player"
                    is Rook -> "R$player"
                    is Pawn -> "P$player"
                    else -> "    "
                }
            }
            str += "|"
        }

        return str
    }

}

