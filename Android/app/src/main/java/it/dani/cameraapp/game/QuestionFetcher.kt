package it.dani.cameraapp.game

import android.util.Log
import it.dani.cameraapp.game.opentdb.data.OpentdbUtils
import java.io.IOException
import java.util.concurrent.Executor

/**
 * @author Daniele
 *
 * This class fetch a list of questions from the specified url ([URL_API])
 */
class QuestionFetcher {

    /**
     * @property[onSuccess] A list of handler fired when the request is completed
     */
    val onSuccess : MutableList<(QuestionDB) -> Any> = ArrayList()

    /**
     * This method requires the fetching action
     */
    fun fetch() {
        Executor { Thread(it).start() }.execute {
            try {
                val result = OpentdbUtils.fetch(URL_API)
                this.onSuccess.forEach { it(result) }
            } catch (e : IOException) {
                Log.e("Game","Error while fetching questions, ${e.message}")
            }
        }
    }

    companion object {
        private const val URL_API = "https://opentdb.com/api.php?amount=10"
    }
}