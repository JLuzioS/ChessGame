package pt.isel.pdm.chess4android.models.games.chess.pieces

import pt.isel.pdm.chess4android.models.games.Game
import pt.isel.pdm.chess4android.models.games.Piece
import pt.isel.pdm.chess4android.models.games.Player
import pt.isel.pdm.chess4android.models.games.Position
import pt.isel.pdm.chess4android.models.games.chess.Chess
import pt.isel.pdm.chess4android.models.games.chess.ChessPiece

class King(player: Player, position: Position) : ChessPiece(player, position) {

    override fun internalGetPositionsInCheck(board: Game): HashSet<Position> {
        return getMoves(board, true)
    }

    override fun internalGetPossibleMoves(board: Game): HashSet<Position> {
        return getMoves(board, false)
    }

    fun isKingInCheck(board: Game): Piece? {
        var playerToCheck: Player
        var pieces: HashSet<Piece>

        board.playersPieces.forEach {
            playerToCheck = it.key
            if (playerToCheck != player){
                pieces = it.value
                for (piece in pieces) {
                    if (piece.getPositionsInCheck(board).contains(position)){
                        return piece
                    }
                }
            }
        }
        return null
    }

    private fun getMoves(board: Game, addFirstPieceFound: Boolean): HashSet<Position> {
        val possibleMoves: HashSet<Position> = HashSet()

        val otherPlayersMoves: HashSet<Position> = HashSet()

        var playerToCheck: Player
        var pieces: HashSet<Piece>

        board.playersPieces.forEach {
            playerToCheck = it.key
            if (playerToCheck != player){
                pieces = it.value
                for (piece in pieces) {
                    otherPlayersMoves.addAll(piece.getPositionsInCheck(board))
                }
            }
        }

        var kingPosition: Position
        (board as Chess).playersKing.forEach {
            playerToCheck = it.key
            if (playerToCheck != player) {
                kingPosition = it.value.position

                otherPlayersMoves.add(Position(kingPosition.x - 1, kingPosition.y - 1)) // Up Left
                otherPlayersMoves.add(Position(kingPosition.x, kingPosition.y - 1)) // Up
                otherPlayersMoves.add(Position(kingPosition.x + 1, kingPosition.y - 1)) // Up Right
                otherPlayersMoves.add(Position(kingPosition.x + 1, kingPosition.y)) // Right
                otherPlayersMoves.add(Position(kingPosition.x + 1, kingPosition.y + 1)) // Down Right
                otherPlayersMoves.add(Position(kingPosition.x, kingPosition.y + 1)) // Down
                otherPlayersMoves.add(Position(kingPosition.x - 1, kingPosition.y + 1)) // Down Left
                otherPlayersMoves.add(Position(kingPosition.x - 1, kingPosition.y)) // Left
            }
        }

        //Castling
        if (!_wasFirstMovedMade && !addFirstPieceFound) {
            if (!otherPlayersMoves.contains(position)){
                val pos = Position(position.x - 1, position.y)
                var checkLeft = true
                // Check left
                while(board.isPositionValid(pos) && pos.x > 1) {
                    if (board.getPiece(pos) != null || otherPlayersMoves.contains(pos)) {
                        checkLeft = false
                        break
                    }
                    pos.x -= 1
                }
                val towerLeft = board.getPiece(Position(0, position.y))
                if (towerLeft != null && !towerLeft.wasFirstMovedMade && checkLeft) {
                    possibleMoves.add(Position(position.x - 2, position.y))
                }

                pos.x = position.x + 1
                var checkRight = true
                // Check right
                while(board.isPositionValid(pos) && pos.x < 7) {
                    if (board.getPiece(pos) != null || otherPlayersMoves.contains(pos)) {
                        checkRight = false
                        break
                    }
                    pos.x += 1
                }

                val towerRight = board.getPiece(Position(board.MAX_WIDTH - 1, position.y))
                if (towerRight != null && !towerRight.wasFirstMovedMade && checkRight) {
                    possibleMoves.add(Position(position.x + 2, position.y))
                }
            }
        }

        val allMoves: Array<Position> = arrayOf(
            Position(position.x - 1, position.y - 1), // Up Left
            Position(position.x, position.y - 1), // Up
            Position(position.x + 1, position.y - 1), // Up Right
            Position(position.x + 1, position.y), // Right
            Position(position.x + 1, position.y + 1), // Down Right
            Position(position.x, position.y + 1), // Down
            Position(position.x - 1, position.y + 1), // Down Left
            Position(position.x - 1, position.y) // Left
        )

        for (pos in allMoves) {
            if (board.isPositionValid(pos)) {
                val piece: ChessPiece? = board.getPiece(pos) as ChessPiece?
                if (!otherPlayersMoves.contains(pos) && (piece == null || piece.player != player || (addFirstPieceFound && piece.player == player))) {
                    possibleMoves.add(pos)
                }
            }
        }

        return possibleMoves
    }
}