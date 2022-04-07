package it.dani.cameraapp.game

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import it.dani.cameraapp.game.opentdb.data.OpentdbUtils
import java.io.IOException

class QuestionFetcher(private val context: AppCompatActivity) {

    val onSuccess : MutableList<(QuestionDB) -> Any> = ArrayList()

    fun fetch() {
        Thread {
            try {
                val result = OpentdbUtils.fetch(URL_API)
                this.onSuccess.forEach { this.context.runOnUiThread { it(result) } }
            } catch (e : IOException) {
                Log.e("Game","Error while fetching questions, ${e.message}")
            }
        }.start()
    }

    companion object {
        private const val URL_API = "https://opentdb.com/api.php?amount=10"
    }
}