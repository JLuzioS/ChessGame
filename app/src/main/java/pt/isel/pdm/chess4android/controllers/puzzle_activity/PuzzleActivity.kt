package pt.isel.pdm.chess4android.controllers.puzzle_activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.databinding.ActivityPuzzleBinding
import pt.isel.pdm.chess4android.models.PuzzleInfo
import pt.isel.pdm.chess4android.models.games.Position
import pt.isel.pdm.chess4android.models.games.PromoteCandidate
import pt.isel.pdm.chess4android.models.games.chess.Chess
import pt.isel.pdm.chess4android.models.games.chess.Puzzle
import pt.isel.pdm.chess4android.models.games.chess.pieces.*

open class PuzzleActivity : AppCompatActivity() {
    companion object {
        fun buildIntent(origin: Activity, puzzle: PuzzleInfo): Intent {
            val intent = Intent(origin, PuzzleActivity::class.java)
            intent.putExtra(PUZZLE_ACTIVITY_VIEW_STATE, puzzle)
            return intent
        }
    }

    private val binding by lazy {
        ActivityPuzzleBinding.inflate(layoutInflater)
    }

    private val viewModel: PuzzleViewModel by viewModels()

    private lateinit var pieceViewMapper: HashMap<Any, Array<Int>>
    var isInPromote: Boolean = false

    init {
        mapViewToPiece()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.boardView.initBoard(
            8, 8,
            this::makeMove, this::getPossibleMoves
        )

        val boardModel = viewModel.boardModel.value

        if (viewModel.lichessPuzzle.value == null) {
            viewModel.getLichessPuzzle()
        } else {
            val puzzle = viewModel.lichessPuzzle.value
            if (puzzle?.solved == true) {
                binding.nextBtn.visibility = VISIBLE
                binding.undoBtn.visibility = VISIBLE
            }
        }

        viewModel.lichessPuzzle.observe(this) {
            if (boardModel == null)
                viewModel.playLichessPuzzle(it)
        }

        viewModel.isInPromote.observe(this) {
            promoteIfPossible(it)
        }

        viewModel.boardModel.observe(this) {
            binding.boardView.setBoard(
                it,
                isInPromote,
                this::getPieceDrawableId
            )
        }

        binding.nextBtn.setOnClickListener {
            viewModel.makeNextMove()
        }
        binding.undoBtn.setOnClickListener {
            viewModel.undoLastMove()
        }

        viewModel.isGameOver.observe(this) {
            if (viewModel.isGameOver.value == true) {
                viewModel.updatePuzzleSolved()
                Toast.makeText(this, getText(R.string.end_game), Toast.LENGTH_LONG).show()
            }
        }

        setContentView(binding.root)
    }

    fun makeMove(currPosition: Position, newPosition: Position, boardModel: Chess) {
        val moveWasMade = boardModel.movePieceAtPosition(currPosition, newPosition)
        if (moveWasMade) {
            val piece = boardModel.getPiece(newPosition)

            if (piece != null && boardModel.isReadyForPromotion(newPosition)) {
                viewModel.savePromoteStatus(
                    PromoteCandidate(newPosition, true, boardModel))
                showPromoteOptions(boardModel, newPosition)
            }

            viewModel.setBoardModel(boardModel)
            if ((boardModel as Puzzle).isPuzzleOver()) {
                viewModel.endGame()
            }
            viewModel.makeNextMoveWithDelay()
        }
    }

    fun showPromoteOptions(boardModel: Chess, position: Position) {
        binding.bishopBtn.visibility = View.VISIBLE
        binding.bishopBtn.setOnClickListener {
            promotePiece(Bishop::class, boardModel, position)
        }
        binding.bishopBtn.invalidate()

        binding.knightBtn.visibility = View.VISIBLE
        binding.knightBtn.setOnClickListener {
            promotePiece(Knight::class, boardModel, position)
        }
        binding.knightBtn.invalidate()

        binding.queenBtn.visibility = View.VISIBLE

        binding.queenBtn.setOnClickListener {
            promotePiece(Queen::class, boardModel, position)
        }
        binding.queenBtn.invalidate()

        binding.rookBtn.visibility = View.VISIBLE
        binding.rookBtn.setOnClickListener {
            promotePiece(Rook::class, boardModel, position)

        }
        binding.rookBtn.invalidate()
    }

    fun hidePromoteOptions() {
        binding.bishopBtn.visibility = View.INVISIBLE
        binding.bishopBtn.setOnClickListener {}
        binding.bishopBtn.invalidate()

        binding.knightBtn.visibility = View.INVISIBLE
        binding.knightBtn.setOnClickListener {}
        binding.knightBtn.invalidate()

        binding.queenBtn.visibility = View.INVISIBLE
        binding.queenBtn.setOnClickListener {}
        binding.queenBtn.invalidate()

        binding.rookBtn.visibility = View.INVISIBLE
        binding.rookBtn.setOnClickListener {}
        binding.rookBtn.invalidate()
        viewModel.savePromoteStatus(PromoteCandidate(isInPromote = false))
    }

    fun promotePiece(piece: Any, boardModel: Chess, position: Position) {
        boardModel.promotePawn(position, piece)
        hidePromoteOptions()
        binding.boardView.setBoard(boardModel, isInPromote, this::getPieceDrawableId)
    }

    fun mapViewToPiece() {
        pieceViewMapper = HashMap()
        pieceViewMapper[Rook::class] = arrayOf(R.drawable.ic_white_rook, R.drawable.ic_black_rook)
        pieceViewMapper[Knight::class] =
            arrayOf(R.drawable.ic_white_knight, R.drawable.ic_black_knight)
        pieceViewMapper[Bishop::class] =
            arrayOf(R.drawable.ic_white_bishop, R.drawable.ic_black_bishop)
        pieceViewMapper[Queen::class] =
            arrayOf(R.drawable.ic_white_queen, R.drawable.ic_black_queen)
        pieceViewMapper[King::class] = arrayOf(R.drawable.ic_white_king, R.drawable.ic_black_king)
        pieceViewMapper[Pawn::class] = arrayOf(R.drawable.ic_white_pawn, R.drawable.ic_black_pawn)
    }

    fun getPossibleMoves(pos: Position, boardModel: Chess): HashSet<Position> {
        return boardModel.getPossibleMoves(pos)
    }

    fun getPieceDrawableId(position: Position, boardModel: Chess): Int? {
        val piece = boardModel.getPiece(position)

        if (piece != null) {
            return if (piece.player == viewModel.whitePlayer)
                pieceViewMapper[piece::class]!![0]
            else
                pieceViewMapper[piece::class]!![1]
        }
        return null
    }

    fun promoteIfPossible(promoteCandidate: PromoteCandidate) {
        isInPromote = promoteCandidate.isInPromote
        if (isInPromote) {
            showPromoteOptions(promoteCandidate.boardModel!!, promoteCandidate.position!!)
        }
    }

}