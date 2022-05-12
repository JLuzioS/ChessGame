package pt.isel.pdm.chess4android.controllers.puzzle_history_activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.pdm.chess4android.controllers.puzzle_activity.PuzzleActivity
import pt.isel.pdm.chess4android.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityHistoryBinding.inflate(layoutInflater)
    }
    private val viewModel by viewModels<HistoryActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.puzzleList.layoutManager = LinearLayoutManager(this)


        (viewModel.history ?: viewModel.loadPuzzleHistory()).observe(this) { puzzleList ->
            binding.puzzleList.adapter = HistoryAdapter(puzzleList) {puzzle ->
                startActivity(PuzzleActivity.buildIntent(this, puzzle))
            }
        }
    }
}