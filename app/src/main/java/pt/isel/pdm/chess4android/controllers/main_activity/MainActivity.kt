package pt.isel.pdm.chess4android.controllers.main_activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.controllers.about_activity.AboutActivity
import pt.isel.pdm.chess4android.controllers.chess_activity.ChessActivity
import pt.isel.pdm.chess4android.controllers.puzzle_activity.PuzzleActivityWithMenu
import pt.isel.pdm.chess4android.controllers.puzzle_history_activity.HistoryActivity
import pt.isel.pdm.chess4android.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.playLocalBtn.setOnClickListener {
            startActivity(Intent(this, ChessActivity::class.java))
        }
        binding.playOnlineBtn.setOnClickListener {
            startActivity(Intent(this, ChessActivity::class.java))
        }
        binding.playPuzzleBtn.setOnClickListener {
            startActivity(Intent(this, PuzzleActivityWithMenu::class.java))
        }
        binding.puzzleHistoryBtn.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
        binding.aboutBtn.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        setContentView(binding.root)
    }
}