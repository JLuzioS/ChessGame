package pt.isel.pdm.chess4android.controllers.puzzle_history_activity
import androidx.room.*
import pt.isel.pdm.chess4android.controllers.history_dao.DBConverter
import pt.isel.pdm.chess4android.models.PuzzleInfo
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*


@Entity(tableName = "history_puzzle")
data class PuzzleEntity(
    @PrimaryKey val puzzleId: String,
    val puzzleInfo: PuzzleInfo,
    val timestamp: Date = Date.from(Instant.now().truncatedTo(ChronoUnit.DAYS))
) {
    fun isTodayPuzzle(): Boolean =
        timestamp.toInstant().compareTo(Instant.now().truncatedTo(ChronoUnit.DAYS)) == 0
}

@Dao
interface HistoryPuzzleDao {
    @Insert
    fun insert(puzzle: PuzzleEntity)

    @Update
    fun update(puzzle: PuzzleEntity)

    @Delete
    fun delete(puzzle: PuzzleEntity)

    @Query("SELECT * FROM history_puzzle ORDER BY timestamp DESC LIMIT 100")
    fun getAllPuzzles(): List<PuzzleEntity>

    @Query("SELECT * FROM history_puzzle ORDER BY timestamp DESC LIMIT :count")
    fun getLastPuzzles(count: Int): List<PuzzleEntity>

    @Query("SELECT * FROM history_puzzle WHERE puzzleId = :puzzleId")
    fun getPuzzle(puzzleId: String): List<PuzzleEntity>
}

@Database(entities = [PuzzleEntity::class], version = 1, exportSchema = false)
@TypeConverters(DBConverter::class)
abstract class HistoryDataBase : RoomDatabase() {
    abstract fun getHistoryPuzzleDao(): HistoryPuzzleDao
}