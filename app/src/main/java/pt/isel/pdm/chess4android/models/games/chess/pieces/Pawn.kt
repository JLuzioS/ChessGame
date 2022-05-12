package pt.isel.pdm.chess4android.models.games.chess.pieces

import pt.isel.pdm.chess4android.models.games.Game
import pt.isel.pdm.chess4android.models.games.Movement
import pt.isel.pdm.chess4android.models.games.Player
import pt.isel.pdm.chess4android.models.games.Position
import pt.isel.pdm.chess4android.models.games.chess.ChessPiece
import kotlin.math.abs

class Pawn(player: Player, position: Position) : ChessPiece(player, position) {

    private val originalRow: Int by lazy {
        when (player) {
            Player.Top -> 1
            Player.Bottom -> 6
        }
    }

    override fun internalGetPositionsInCheck(board: Game): HashSet<Position> {
        return getMoves(board, true)
    }

    override fun internalGetPossibleMoves(board: Game): HashSet<Position> {
        return getMoves(board, false)
    }

    private fun getMoves(board: Game, addFirstPieceFound: Boolean): HashSet<Position> {
        return when (player) {
            Player.Top -> {
                calculateUpOrDown(position, board, 1, 2, addFirstPieceFound)
            }
            Player.Bottom -> {
                calculateUpOrDown(position, board, -1, -2, addFirstPieceFound)
            }
        }
    }

    private fun calculateUpOrDown(
        position: Position,
        board: Game,
        oneMove: Int,
        twoMoves: Int,
        addFirstPieceFound: Boolean
    ): HashSet<Position> {

        val positions: HashSet<Position> = HashSet()
        if (!addFirstPieceFound) {
            val pos1 = Position(position.x, position.y + oneMove) // Down 1
            if (board.getPiece(pos1) == null) {
                positions.add(pos1)
            }

            val pos2 = Position(position.x, position.y + twoMoves) // Down 2
            if (position.y == originalRow && board.getPiece(pos1) == null && board.getPiece(pos2) == null) {
                positions.add(pos2)
            }
        }
        val lastMovement: Movement? = board.getLastMovement()

        val captureLeft = Position(position.x - 1, position.y + oneMove)


        if (board.isPositionValid(captureLeft)) {
            val pieceAtLeft: ChessPiece? = board.getPiece(captureLeft) as ChessPiece?
            if (addFirstPieceFound || pieceAtLeft != null && pieceAtLeft.player != player) {
                positions.add(captureLeft)
            }
        }

        val captureRight =
            Position(position.x + 1, position.y + oneMove) // Right Down needs capture


        if (board.isPositionValid(captureRight)) {
            val pieceAtRight: ChessPiece? = board.getPiece(captureRight) as ChessPiece?
            if (addFirstPieceFound || pieceAtRight != null && pieceAtRight.player != player) {
                positions.add(captureRight)
            }
        }
        // Check En Passant by checking that the last piece that move was a Pawn and it move more than 2 spaces (first move)
        if (lastMovement != null &&
            lastMovement.pieceAtOrigin is Pawn &&
            abs(lastMovement.origin.y - lastMovement.destination.y) == 2 //
        ) {
            if (board.isPositionValid(captureLeft)) {
                val pieceAtLeft: ChessPiece? =
                    board.getPiece(
                        Position(
                            captureLeft.x,
                            captureLeft.y - oneMove
                        )
                    ) as ChessPiece?
                if (pieceAtLeft is Pawn && pieceAtLeft.player != player && captureLeft.x == lastMovement.destination.x) {
                    positions.add(captureLeft)
                }
            }

            if (board.isPositionValid(captureRight)) {
                val pieceAtRight: ChessPiece? =
                    board.getPiece(Position(captureRight.x, captureRight.y - oneMove)) as ChessPiece?
                if (pieceAtRight is Pawn && pieceAtRight.player != player && captureRight.x == lastMovement.destination.x) {
                    positions.add(captureRight)
                }
            }
        }

        return positions
    }
}