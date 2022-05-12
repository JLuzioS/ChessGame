package pt.isel.pdm.chess4android.controllers.history_dao

import androidx.room.TypeConverter
import com.google.gson.Gson
import pt.isel.pdm.chess4android.models.PuzzleInfo
import java.util.*

class DBConverter {
    @TypeConverter
    fun toPuzzleInfoJson(puzzleInfo: PuzzleInfo): String = Gson().toJson(puzzleInfo)

    @TypeConverter
    fun toPuzzleInfo(json: String): PuzzleInfo = Gson().fromJson(json, PuzzleInfo::class.java)

    @TypeConverter
    fun toDate(milliseconds: Long) = Date(milliseconds)

    @TypeConverter
    fun toLong(date: Date) = date.time
}