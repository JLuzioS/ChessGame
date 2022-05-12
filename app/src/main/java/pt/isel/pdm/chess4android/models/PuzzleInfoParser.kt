package pt.isel.pdm.chess4android.models

import pt.isel.pdm.chess4android.models.games.Movement
import pt.isel.pdm.chess4android.models.games.Piece
import pt.isel.pdm.chess4android.models.games.Player
import pt.isel.pdm.chess4android.models.games.Position
import pt.isel.pdm.chess4android.models.games.chess.Chess
import pt.isel.pdm.chess4android.models.games.chess.Puzzle
import pt.isel.pdm.chess4android.models.games.chess.pieces.*

class PuzzleInfoParser(private val dailyGame: PuzzleInfo) {
    private var initialPlayer: Player =
        if (dailyGame.puzzle.initialPly % 2 == 0) Player.Top else Player.Bottom

    fun parsePuzzlePNG(): Chess {
        val solution: Array<Movement> = dailyGame.puzzle.solution.map {
            Movement(
                convertPGNPosition(it[0], it[1]),
                convertPGNPosition(it[2], it[3]),
                null,
                null
            )
        }.toTypedArray()

        val chess = Puzzle(solution, initialPlayer, 8, 8)
        val moves = dailyGame.game.pgn.split(" ").toTypedArray()

        moves.forEach { pgnMove ->
            parsePGN(pgnMove, chess)
        }
        chess.endSetup()

        return chess
    }

    private fun convertPGNPosition(xChar: Char?, yChar: Char?): Position {
        var x: Int = (xChar?.code ?: 'a'.code) - 'a'.code
        var y: Int = (yChar?.digitToInt() ?: '1'.digitToInt()) - 1

        if (initialPlayer == Player.Bottom) y = 7 - y
        else x = 7 - x

        return Position(x, y)
    }

    private fun parseNewPosition(pgnMove: String): Position {
        var pgnLen: Int = pgnMove.length
        if (pgnMove[pgnLen - 1] == '+') pgnLen -= 1
        if (pgnMove[pgnLen - 1] in "BNQR" && pgnMove[pgnLen - 2] == '=') pgnLen -= 2

        return convertPGNPosition(pgnMove[pgnLen - 2], pgnMove[pgnLen - 1])
    }

    private fun parseCurrPosition(pgnMove: String, piece: Piece): Boolean {
        var pgnLen: Int = pgnMove.length
        if (pgnMove[pgnLen - 1] == '+') pgnLen -= 1
        if (pgnMove[pgnLen - 1] in "BNQR" && pgnMove[pgnLen - 2] == '=') pgnLen -= 2

        if (pgnLen <= 3) return true

        if (pgnMove[pgnLen - 3] == 'x') pgnLen -= 3
        else pgnLen -= 2

        if (pgnMove[pgnLen - 1].code >= 'A'.code && pgnMove[pgnLen - 1].code <= 'Z'.code) return true

        if (pgnMove[pgnLen - 1].code >= 'a'.code && pgnMove[pgnLen - 1].code <= 'z'.code) {
            if (piece.position.x == convertPGNPosition(pgnMove[pgnLen - 1], null).x) return true
        }

        if (pgnMove[pgnLen - 1].code >= '0'.code && pgnMove[pgnLen - 1].code <= '9'.code) {
            if (piece.position.y == convertPGNPosition(null, pgnMove[pgnLen - 1]).y) return true
        }

        return false
    }

    private fun samePieceType(pgnMove: String, piece: Piece): Boolean {
        if (pgnMove[0] in ("BNQR")) {
            if (pgnMove[0] == 'B' && piece is Bishop) return true
            if (pgnMove[0] == 'N' && piece is Knight) return true
            if (pgnMove[0] == 'Q' && piece is Queen) return true
            if (pgnMove[0] == 'R' && piece is Rook) return true
        } else
            if (piece is Pawn) return true

        return false
    }

    private fun verifyPromote(pgnMove: String): Any? {
        val pgnLen: Int = pgnMove.length
        if (pgnMove[pgnLen - 1] !in "BNQR" || pgnMove[pgnLen - 2] != '=') return null

        if (pgnMove[pgnLen - 1] == 'B') return Bishop::class
        if (pgnMove[pgnLen - 1] == 'N') return Knight::class
        if (pgnMove[pgnLen - 1] == 'Q') return Queen::class
        if (pgnMove[pgnLen - 1] == 'R') return Rook::class

        return null
    }

    private fun parsePGN(pgnMove: String, chess: Chess) {
        val newPosition: Position

        if (pgnMove[0] in "KO") {
            val piece = chess.playersKing[chess.currentPlayer]!!

            if (piece.getPossibleMoves(chess).size > 0) {
                newPosition = if (pgnMove[0] == 'K')
                    parseNewPosition(pgnMove)
                else {
                    if ((initialPlayer == Player.Bottom && pgnMove.length > 3) ||
                        (initialPlayer == Player.Top && pgnMove.length <= 3)
                    )
                        Position(piece.position.x - 2, piece.position.y)
                    else
                        Position(piece.position.x + 2, piece.position.y)
                }

                if (piece.getPossibleMoves(chess).contains(newPosition))
                    chess.movePieceAtPosition(piece.position, newPosition)
            }
        } else {
            newPosition = parseNewPosition(pgnMove)
            chess.playersPieces[chess.currentPlayer]?.forEach { piece ->
                if (samePieceType(pgnMove, piece) &&
                    piece.getPossibleMoves(chess).contains(newPosition) &&
                    parseCurrPosition(pgnMove, piece)
                ) {
                    chess.movePieceAtPosition(piece.position, newPosition)
                    val promotedClass = verifyPromote(pgnMove)
                    if (promotedClass != null)
                        chess.promotePawn(newPosition, promotedClass)
                    return
                }
            }
        }
    }
}