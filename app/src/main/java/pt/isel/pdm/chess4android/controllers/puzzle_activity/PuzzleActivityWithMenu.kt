package pt.isel.pdm.chess4android.controllers.puzzle_activity

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.controllers.puzzle_history_activity.HistoryActivity

class PuzzleActivityWithMenu : PuzzleActivity() {

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menuHistory -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}