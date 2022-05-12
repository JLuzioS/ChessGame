package pt.isel.pdm.chess4android.models

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import kotlinx.parcelize.TypeParceler
import retrofit2.Call
import retrofit2.http.GET
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

const val URL = "https://lichess.org/api/"

object DateClassParceler : Parceler<Date> {
    override fun create(parcel: Parcel) = Date(parcel.readLong())

    override fun Date.write(parcel: Parcel, flags: Int) {
        parcel.writeLong(time)
    }
}

@Parcelize
@TypeParceler<Date, DateClassParceler>()
data class PuzzleInfo(
    val game: @RawValue DailyGame,
    val puzzle: @RawValue DailyPuzzle,
    var solved: Boolean = false,
    val timestamp: Date = Date.from(Instant.now().truncatedTo(ChronoUnit.DAYS))
) : Parcelable

@Parcelize
data class DailyGame(
    val id: String,
    val perf: Perf,
    val rated: Boolean,
    val players: Array<Player>,
    val pgn: String,
    val clock: String
) : Parcelable {
    /**
     * Method equals and hashcode were overridden because of Array of Players.
     * Both of the methods were create with the help of IntelliJ.
     * */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DailyGame

        if (id != other.id) return false
        if (perf != other.perf) return false
        if (rated != other.rated) return false
        if (!players.contentEquals(other.players)) return false
        if (pgn != other.pgn) return false
        if (clock != other.clock) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + perf.hashCode()
        result = 31 * result + rated.hashCode()
        result = 31 * result + players.contentHashCode()
        result = 31 * result + pgn.hashCode()
        result = 31 * result + clock.hashCode()
        return result
    }
}
@Parcelize
data class DailyPuzzle(
    val id: String,
    val rating: Int,
    val plays: Int,
    val initialPly: Int,
    val solution: Array<String>,
    val themes: Array<String>
) : Parcelable {
    /**
     * Method equals and hashcode were overridden because of Array of solution and themes.
     * Both of the methods were create with the help of IntelliJ.
     * */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DailyPuzzle

        if (id != other.id) return false
        if (rating != other.rating) return false
        if (plays != other.plays) return false
        if (initialPly != other.initialPly) return false
        if (!solution.contentEquals(other.solution)) return false
        if (!themes.contentEquals(other.themes)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + rating
        result = 31 * result + plays
        result = 31 * result + initialPly
        result = 31 * result + solution.contentHashCode()
        result = 31 * result + themes.contentHashCode()
        return result
    }
}

@Parcelize
data class Perf(val icon: String, val name: String) : Parcelable

@Parcelize
data class Player(val userId: String, val name: String, val color: String) : Parcelable

interface DailyPuzzleService {
    @GET("puzzle/daily")
    fun getPuzzle(): Call<PuzzleInfo>
}

class ServiceUnavailable(message: String = "", cause: Throwable? = null) : Exception(message, cause)