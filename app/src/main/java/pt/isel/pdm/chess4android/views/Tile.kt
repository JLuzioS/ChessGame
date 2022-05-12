package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.R

/**
 * Custom view that implements a chess board tile.
 * Tiles are either black or white and can they can be empty or occupied by a chess piece.
 *
 * Implementation note: This view is not to be used with the designer tool.
 * You need to adapt this view to suit your needs. ;)
 *
 * @property type           The tile's type (i.e. black or white)
 * @property tilesPerSide   The number of tiles in each side of the chess board
 */
@SuppressLint("ViewConstructor")
class Tile(
    private val ctx: Context,
    private val type: Type,
    private val tilesPerSide: Int,
    var piece: Int?
) : View(ctx) {
    var inPreview: Boolean = false
    var isCurrPlayer = false


    enum class Type { WHITE, BLACK }

    private val brush = Paint().apply {
        color = ctx.resources.getColor(
            if (type == Type.WHITE) R.color.chess_board_white else R.color.chess_board_black,
            null
        )
        style = Paint.Style.FILL_AND_STROKE
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val side = Integer.min(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
        setMeasuredDimension(side / tilesPerSide, side / tilesPerSide)
    }

    override fun onDraw(canvas: Canvas) {
        val padding = 8
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), brush)
        var drawPiece: VectorDrawableCompat? = null

        if (piece in setOf(
                R.drawable.ic_white_pawn,
                R.drawable.ic_white_bishop,
                R.drawable.ic_white_king,
                R.drawable.ic_white_knight,
                R.drawable.ic_white_queen,
                R.drawable.ic_white_rook,

                R.drawable.ic_black_pawn,
                R.drawable.ic_black_bishop,
                R.drawable.ic_black_king,
                R.drawable.ic_black_knight,
                R.drawable.ic_black_queen,
                R.drawable.ic_black_rook,
            )
        ) {
            drawPiece = VectorDrawableCompat
                .create(ctx.resources, piece!!, null)
            drawPiece?.setBounds(padding, padding, width - padding, height - padding)
            drawPiece?.draw(canvas)
        }

        if (inPreview) {
            val spacePiece =
                if (drawPiece == null && piece == null) {
                    if(isCurrPlayer) {
                        R.drawable.ic_empty_squares_possible_move
                    } else {
                        R.drawable.ic_empty_squares_possible_move_other_player
                    }
                } else {
                    R.drawable.ic_can_attack_piece
                }

            val possibleMoves = VectorDrawableCompat
                .create(ctx.resources, spacePiece, null)
            possibleMoves?.setBounds(padding, padding, width - padding, height - padding)
            possibleMoves?.draw(canvas)
        }
    }

}