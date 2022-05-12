package pt.isel.pdm.chess4android.models.games.chess

import pt.isel.pdm.chess4android.models.games.Game
import pt.isel.pdm.chess4android.models.games.Piece
import pt.isel.pdm.chess4android.models.games.Player
import pt.isel.pdm.chess4android.models.games.Position
import pt.isel.pdm.chess4android.models.games.chess.pieces.*
import kotlin.math.abs

abstract class ChessPiece(player: Player, position: Position) : Piece(player, position) {


    override fun getPossibleMoves(board: Game): HashSet<Position> {
        val king = (board as Chess).playersKing[player]!!
        val moves = internalGetPossibleMoves(board)
        if (this == king) return moves
        // Validate if piece is in Check Proxy
        val proxyHorizontal = checkProxyCheckToLeft(board) || checkProxyCheckToBottomRight(board)
        val proxyVertical = checkProxyCheckToTop(board) || checkProxyCheckToBottom(board)
        val proxyDiagonal = checkProxyCheckToBottomLeft(board) || checkProxyCheckToBottomRight(board) ||
                checkProxyCheckToTopLeft(board) || checkProxyCheckToTopRight(board)

        val filteredMoves = hashSetOf<Position>()
        moves.forEach {
            if (!proxyHorizontal && !proxyVertical && !proxyDiagonal)
                filteredMoves.add(it)
            else if (proxyHorizontal && it.y == position.y)
                filteredMoves.add(it)
            else if (proxyVertical && it.x == position.x)
                filteredMoves.add(it)
            else if (proxyDiagonal && abs(it.x - position.x) == abs(it.y - position.y))
                filteredMoves.add(it)
        }

        // Validate if King for the current Player is in check
        val pieceCheckingKing: Piece? = king.isKingInCheck(board)
        if (pieceCheckingKing != null) {
            val possibleMovesWhileChecked = hashSetOf<Position>()
            // Can the current piece eat the piece that is checking
            if (filteredMoves.contains(pieceCheckingKing.position))
                possibleMovesWhileChecked.add(pieceCheckingKing.position)

            // check if en passant can eat
            if (pieceCheckingKing is Pawn){
                val upPosition = Position(pieceCheckingKing.position.x, position.y - 1)
                if (filteredMoves.contains(upPosition))
                    possibleMovesWhileChecked.add(upPosition)
                val downPosition = Position(pieceCheckingKing.position.x, position.y + 1)
                if (filteredMoves.contains(downPosition))
                    possibleMovesWhileChecked.add(downPosition)
            }

            // Can the current piece block horizontal movements
            filteredMoves.forEach {
                if (it.isPositionInBetween(pieceCheckingKing.position, king.position))
                    possibleMovesWhileChecked.add(it)
            }
            return possibleMovesWhileChecked
        }

        return filteredMoves
        //if (possibleMoves == null){
        //    possibleMoves = internalGetPossibleMoves(board)
        //}
        //return possibleMoves!!
    }

    protected fun getPositionsToRight(board: Game, addFirstPieceFound: Boolean): HashSet<Position> {
        val positions: HashSet<Position> = HashSet()
        val pos = Position(position.x, position.y)
        var piece: ChessPiece?
        for (i in position.x + 1 until board.MAX_WIDTH) {
            pos.x = i
            if (board.isPositionValid(pos)) {
                piece = board.getPiece(pos) as ChessPiece?
                if (piece == null) {
                    positions.add(Position(pos.x, pos.y))
                } else if (piece is King && piece.player != player) {
                    positions.add(Position(pos.x, pos.y))
                } else if (piece.player != player) {
                    positions.add(Position(pos.x, pos.y))
                    break
                } else if (piece.player == player) {
                    if (addFirstPieceFound)
                        positions.add(Position(pos.x, pos.y))
                    break
                }
            }
        }
        return positions
    }

    protected fun getPositionsToLeft(board: Game, addFirstPieceFound: Boolean): HashSet<Position> {
        val positions: HashSet<Position> = HashSet()
        val pos = Position(position.x, position.y)
        var piece: ChessPiece?
        for (i in position.x - 1 downTo 0) {
            pos.x = i
            if (board.isPositionValid(pos)) {
                piece = board.getPiece(pos) as ChessPiece?
                if (piece == null) {
                    positions.add(Position(pos.x, pos.y))
                } else if (piece is King && piece.player != player) {
                    positions.add(Position(pos.x, pos.y))
                } else if (piece.player != player) {
                    positions.add(Position(pos.x, pos.y))
                    break
                } else if (piece.player == player) {
                    if (addFirstPieceFound)
                        positions.add(Position(pos.x, pos.y))
                    break
                }
            }
        }
        return positions
    }

    protected fun getPositionsToBottom(
        board: Game,
        addFirstPieceFound: Boolean
    ): HashSet<Position> {
        val positions: HashSet<Position> = HashSet()
        val pos = Position(position.x, position.y)
        var piece: ChessPiece?
        for (i in position.y + 1 until board.MAX_HEIGHT) {
            pos.y = i
            if (board.isPositionValid(pos)) {
                piece = board.getPiece(pos) as ChessPiece?
                if (piece == null) {
                    positions.add(Position(pos.x, pos.y))
                } else if (piece is King && piece.player != player) {
                    positions.add(Position(pos.x, pos.y))
                } else if (piece.player != player) {
                    positions.add(Position(pos.x, pos.y))
                    break
                } else if (piece.player == player) {
                    if (addFirstPieceFound)
                        positions.add(Position(pos.x, pos.y))
                    break
                }
            }
        }
        return positions
    }

    protected fun getPositionsToTop(board: Game, addFirstPieceFound: Boolean): HashSet<Position> {
        val positions: HashSet<Position> = HashSet()
        val pos = Position(position.x, position.y)
        var piece: ChessPiece?
        for (i in position.y - 1 downTo 0) {
            pos.y = i
            if (board.isPositionValid(pos)) {
                piece = board.getPiece(pos) as ChessPiece?
                if (piece == null) {
                    positions.add(Position(pos.x, pos.y))
                } else if (piece is King && piece.player != player) {
                    positions.add(Position(pos.x, pos.y))
                } else if (piece.player != player) {
                    positions.add(Position(pos.x, pos.y))
                    break
                } else if (piece.player == player) {
                    if (addFirstPieceFound)
                        positions.add(Position(pos.x, pos.y))
                    break
                }
            }
        }
        return positions
    }

    protected fun getPositionsToTopLeft(
        board: Game,
        addFirstPieceFound: Boolean
    ): HashSet<Position> {
        val positions: HashSet<Position> = HashSet()
        val pos = Position(position.x - 1, position.y - 1)
        var piece: ChessPiece?
        // Top right
        while (board.isPositionValid(pos)) {
            piece = board.getPiece(pos) as ChessPiece?
            if (piece == null) {
                positions.add(Position(pos.x, pos.y))
            } else if (piece is King && piece.player != player) {
                positions.add(Position(pos.x, pos.y))
            } else if (piece.player != player) {
                positions.add(Position(pos.x, pos.y))
                break
            } else if (piece.player == player) {
                if (addFirstPieceFound)
                    positions.add(Position(pos.x, pos.y))
                break
            }
            pos.x -= 1
            pos.y -= 1
        }
        return positions
    }

    protected fun getPositionsToBottomRight(
        board: Game,
        addFirstPieceFound: Boolean
    ): HashSet<Position> {
        val positions: HashSet<Position> = HashSet()
        val pos = Position(position.x + 1, position.y + 1)
        var piece: ChessPiece?
        // Top right
        while (board.isPositionValid(pos)) {
            piece = board.getPiece(pos) as ChessPiece?
            if (piece == null) {
                positions.add(Position(pos.x, pos.y))
            } else if (piece is King && piece.player != player) {
                positions.add(Position(pos.x, pos.y))
            } else if (piece.player != player) {
                positions.add(Position(pos.x, pos.y))
                break
            } else if (piece.player == player) {
                if (addFirstPieceFound)
                    positions.add(Position(pos.x, pos.y))
                break
            }
            pos.x += 1
            pos.y += 1
        }
        return positions
    }

    protected fun getPositionsToTopRight(
        board: Game,
        addFirstPieceFound: Boolean
    ): HashSet<Position> {
        val positions: HashSet<Position> = HashSet()
        val pos = Position(position.x + 1, position.y - 1)
        var piece: ChessPiece?
        // Top right
        while (board.isPositionValid(pos)) {
            piece = board.getPiece(pos) as ChessPiece?
            if (piece == null) {
                positions.add(Position(pos.x, pos.y))
            } else if (piece is King && piece.player != player) {
                positions.add(Position(pos.x, pos.y))
            } else if (piece.player != player) {
                positions.add(Position(pos.x, pos.y))
                break
            } else if (piece.player == player) {
                if (addFirstPieceFound)
                    positions.add(Position(pos.x, pos.y))
                break
            }
            pos.x += 1
            pos.y -= 1
        }
        return positions
    }

    protected fun getPositionsToBottomLeft(
        board: Game,
        addFirstPieceFound: Boolean
    ): HashSet<Position> {
        val positions: HashSet<Position> = HashSet()
        val pos = Position(position.x - 1, position.y + 1)
        var piece: ChessPiece?
        // Top right
        while (board.isPositionValid(pos)) {
            piece = board.getPiece(pos) as ChessPiece?
            if (piece == null) {
                positions.add(Position(pos.x, pos.y))
            } else if (piece is King && piece.player != player) {
                positions.add(Position(pos.x, pos.y))
            } else if (piece.player != player) {
                positions.add(Position(pos.x, pos.y))
                break
            } else if (piece.player == player) {
                if (addFirstPieceFound)
                    positions.add(Position(pos.x, pos.y))
                break
            }
            pos.x -= 1
            pos.y += 1
        }
        return positions
    }

    protected fun checkProxyCheckToRight(board: Game): Boolean {
        return horizontalVerticalProxyCheckExists(board as Chess, 1, 0)
    }

    protected fun checkProxyCheckToLeft(board: Game): Boolean {
        return horizontalVerticalProxyCheckExists(board as Chess, -1, 0)
    }

    protected fun checkProxyCheckToBottom(board: Game): Boolean {
        return horizontalVerticalProxyCheckExists(board as Chess, 0, 1)
    }

    protected fun checkProxyCheckToTop(board: Game): Boolean {
        return horizontalVerticalProxyCheckExists(board as Chess, 0, -1)
    }

    protected fun checkProxyCheckToTopLeft(board: Game): Boolean {
        return diagonalProxyCheckExists(board as Chess, -1, -1)
    }

    protected fun checkProxyCheckToBottomRight(board: Game): Boolean {
        return diagonalProxyCheckExists(board as Chess, 1, 1)
    }

    protected fun checkProxyCheckToTopRight(board: Game): Boolean {
        return diagonalProxyCheckExists(board as Chess, 1, -1)
    }

    protected fun checkProxyCheckToBottomLeft(board: Game): Boolean {
        return diagonalProxyCheckExists(board as Chess, -1, 1)
    }

    private fun diagonalProxyCheckExists(board: Chess, xIncrement: Int, yIncrement: Int): Boolean {
        val playersKing = board.playersKing[player]!!
        // Is the king in the same diagonal?
        val pos = Position(position.x + xIncrement, position.y + yIncrement)

        if (abs(playersKing.position.x - pos.x) == abs(playersKing.position.y - pos.y)) {
            pos.x = playersKing.position.x + xIncrement
            pos.y = playersKing.position.y + yIncrement
            var piece: Piece?
            // Check Between Players King and Current piece.
            while (board.isPositionValid(pos)) {
                piece = board.getPiece(pos)
                if (piece != null) {
                    if (piece == this) {
                        pos.x = position.x + xIncrement
                        pos.y = position.y + yIncrement
                        break
                    } else {
                        return false
                    }
                }
                pos.x += xIncrement
                pos.y += yIncrement
            }

            // Check Between current piece and end of the board.
            while (board.isPositionValid(pos)) {
                piece = board.getPiece(pos)
                if (piece != null) {
                    if (piece.player == player) {
                        return false
                    } else return piece is Bishop || piece is Queen
                }
                pos.x += xIncrement
                pos.y += yIncrement
            }
        }
        return false
    }

    private fun horizontalVerticalProxyCheckExists(
        board: Chess,
        xIncrement: Int,
        yIncrement: Int
    ): Boolean {
        val playersKing = board.playersKing[player]!!
        // Is the king in the same diagonal?
        if (playersKing.position.x == position.x || playersKing.position.y == position.y) {
            val pos =
                Position(playersKing.position.x + xIncrement, playersKing.position.y + yIncrement)
            var piece: Piece?
            // Check Between Players King and Current piece.
            while (board.isPositionValid(pos)) {
                piece = board.getPiece(pos)
                if (piece != null) {
                    if (piece == this) {
                        pos.x = position.x + xIncrement
                        pos.y = position.y + yIncrement
                        break
                    } else {
                        return false
                    }
                }
                pos.x += xIncrement
                pos.y += yIncrement
            }

            // Check Between current piece and end of the board.
            while (board.isPositionValid(pos)) {
                piece = board.getPiece(pos)
                if (piece != null) {
                    if (piece.player == player) {
                        return false
                    } else return piece is Rook || piece is Queen
                }
                pos.x += xIncrement
                pos.y += yIncrement
            }
        }
        return false
    }
}