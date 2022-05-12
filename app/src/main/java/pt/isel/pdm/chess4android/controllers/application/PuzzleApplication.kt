package pt.isel.pdm.chess4android.controllers.application

import android.app.Application
import androidx.room.Room
import androidx.work.*
import pt.isel.pdm.chess4android.controllers.main_activity.DownloadDailyPuzzleWorker
import pt.isel.pdm.chess4android.controllers.puzzle_history_activity.HistoryDataBase
import pt.isel.pdm.chess4android.models.DailyPuzzleService
import pt.isel.pdm.chess4android.models.URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val APP_TAG = "Chess4Android"

class PuzzleApplication : Application() {
    companion object {
        val dailyPuzzleService: DailyPuzzleService by lazy {
            Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(DailyPuzzleService::class.java)
        }
    }

    /** In Memory DB for now */
    val historyDB: HistoryDataBase by lazy {
        Room
            .databaseBuilder(this, HistoryDataBase::class.java, "puzzle_db")
//            .inMemoryDatabaseBuilder(this, HistoryDataBase::class.java)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        val workRequest = PeriodicWorkRequestBuilder<DownloadDailyPuzzleWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .setRequiresStorageNotLow(true)
                    .build()
            )
            .build()

        WorkManager
            .getInstance(this)
            .enqueueUniquePeriodicWork(
                "DownloadDailyPuzzle",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )

//        val array = arrayOf(
//            "{\"game\":{\"id\":\"nf1ESwJr\",\"perf\":{\"icon\":\"\uE01D\",\"name\":\"Blitz\"},\"rated\":true,\"players\":[{\"userId\":\"giuliob100\",\"name\":\"giuliob100 (1979)\",\"color\":\"white\"},{\"userId\":\"yassinetn\",\"name\":\"YassineTN (1731)\",\"color\":\"black\"}],\"pgn\":\"e4 g6 d4 Bg7 Nf3 e6 Bd3 c6 O-O Ne7 Re1 Na6 c3 Nc7 Bg5 d5 Qd2 h6 Bf4 Bd7 a4 g5 Bg3 a6 h4 g4 Ne5 Ng6 Nxg4 Qe7 Bxc7 Nxh4 e5 h5 Bd6 Qd8 Nh2 Bf8 Bxf8 Rxf8 Qh6 Qe7 Qxh5 O-O-O Re3 Rh8 Qe2 Rdg8 Rh3 Rxg2+ Kh1 Qg5 Nd2\",\"clock\":\"3+0\"},\"puzzle\":{\"id\":\"MOe5M\",\"rating\":1787,\"plays\":99705,\"initialPly\":52,\"solution\":[\"g2h2\",\"h3h2\",\"g5g2\",\"h2g2\",\"h4f3\",\"d3h7\",\"h8h7\",\"g2h2\",\"h7h2\"],\"themes\":[\"exposedKing\",\"veryLong\",\"clearance\",\"middlegame\",\"sacrifice\",\"kingsideAttack\",\"discoveredAttack\",\"arabianMate\",\"mateIn5\"]}}\n",
//            "{\"game\":{\"id\":\"RRLJbyi2\",\"perf\":{\"icon\":\"\uE017\",\"name\":\"Rapid\"},\"rated\":true,\"players\":[{\"userId\":\"nusret49\",\"name\":\"Nusret49 (2014)\",\"color\":\"white\"},{\"userId\":\"theuntoldgospel\",\"name\":\"TheUntoldGospel (2018)\",\"color\":\"black\"}],\"pgn\":\"e4 c5 c4 Nc6 Nc3 d6 Nf3 Nf6 d3 Bg4 Be2 g6 O-O Bg7 a3 Bxf3 Bxf3 Nd4 Be3 Nd7 Bxd4 Bxd4 Rb1 O-O Nb5 Bg7 Qd2 Ne5 Be2 a6 Nc3 f5 exf5 Rxf5 f4 Nc6 Bf3 Nd4 Bxb7 Ra7 Be4 Rf8 b4 cxb4 axb4 e5 fxe5 Bxe5 Nd5 Rxf1+ Rxf1 Qh4 g3 Qd8 Kh1 Nb3 Qd1 Nd4 Qa4 Ne2 b5 Bxg3 hxg3 Nxg3+ Kg2 Nxf1 Kxf1 Rf7+ Ke2 Qh4 Kd2 Rf1 bxa6 Qe1+ Kc2 Rf2+ Kb3\",\"clock\":\"15+0\"},\"puzzle\":{\"id\":\"62Jvl\",\"rating\":1960,\"plays\":84412,\"initialPly\":76,\"solution\":[\"e1b1\",\"b3c3\",\"b1b2\"],\"themes\":[\"endgame\",\"short\",\"mateIn2\"]}}",
//            "{\"game\":{\"id\":\"0AzN6JCP\",\"perf\":{\"icon\":\"\",\"name\":\"Rapid\"},\"rated\":true,\"players\":[{\"userId\":\"mferrer\",\"name\":\"mferrer (1867)\",\"color\":\"white\"},{\"userId\":\"theitch\",\"name\":\"TheItch (1909)\",\"color\":\"black\"}],\"pgn\":\"d4 Nf6 e3 g6 f4 Bg7 Bd3 O-O f5 d5 fxg6 fxg6 Nf3 Ne4 Nbd2 Bf5 O-O c5 c3 Nd7 Bxe4 dxe4 Nh4 cxd4 Nxf5 gxf5 exd4 Nb6 Qh5 Qd5 Nb3 Rf7 Bh6 Raf8 Rf4 e5 dxe5 Qxe5 Nd4 Bxh6 Qxh6 Rf6 Qh4 e3 Re1 Nd5 Rf3 f4 Rh3 h6 Nf3 Qe4 Qg4+ Rg6 Qh5 Qc2 Qxd5+ Rf7 Ne5\",\"clock\":\"20+4\"},\"puzzle\":{\"id\":\"mFVkT\",\"rating\":1686,\"plays\":10973,\"initialPly\":58,\"solution\":[\"c2f2\",\"g1h1\",\"f2e1\"],\"themes\":[\"mateIn2\",\"middlegame\",\"short\",\"fork\"]}} "
//        )
//
//        array.forEachIndexed { index, json ->
//            val p = Common.createPuzzleFromJSON(json)
//            val puzzle = PuzzleInfo(
//                p.game,
//                p.puzzle,
//                Date(p.timestamp.time - (index + 1) * (24 * 60 * 60 * 1000))
//            )
//            callbackAfterAsync({}) {
//                historyDB.getHistoryPuzzleDao()
//                    .insert(
//                        PuzzleEntity(puzzle.game.id, puzzle, puzzle.timestamp)
//                    )
//            }
//        }

    }
}