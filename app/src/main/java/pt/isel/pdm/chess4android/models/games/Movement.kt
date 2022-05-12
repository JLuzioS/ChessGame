package pt.isel.pdm.chess4android.models.games


data class Movement(
    val origin: Position,
    val destination: Position,
    val pieceAtOrigin: Piece?,
    val pieceAtDestination: Piece?
)