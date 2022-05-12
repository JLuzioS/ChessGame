package pt.isel.pdm.chess4android.models.games.chess.pieces

import pt.isel.pdm.chess4android.models.games.Game
import pt.isel.pdm.chess4android.models.games.Player
import pt.isel.pdm.chess4android.models.games.Position
import pt.isel.pdm.chess4android.models.games.chess.ChessPiece

class Bishop(player: Player, position: Position) : ChessPiece(player, position) {
    override fun internalGetPositionsInCheck(board: Game): HashSet<Position> {
        return getMoves(board, true)
    }

    override fun internalGetPossibleMoves(board: Game): HashSet<Position> {
        return getMoves(board, false)
    }

    private fun getMoves(board: Game, addFirstPieceFound: Boolean): HashSet<Position> {
        val possibleMoves: HashSet<Position> = HashSet()

        // Get diagonals
        possibleMoves.addAll(getPositionsToTopLeft(board, addFirstPieceFound))
        possibleMoves.addAll(getPositionsToTopRight(board, addFirstPieceFound))
        possibleMoves.addAll(getPositionsToBottomLeft(board, addFirstPieceFound))
        possibleMoves.addAll(getPositionsToBottomRight(board, addFirstPieceFound))

        return possibleMoves
    }
}