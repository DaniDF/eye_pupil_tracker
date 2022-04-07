package it.dani.cameraapp.game.opentdb.data

import com.google.gson.Gson
import it.dani.cameraapp.game.Answer
import it.dani.cameraapp.game.Question
import it.dani.cameraapp.game.QuestionDB
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

/**
 * @author Daniele
 *
 * This class represents the json data fetched from Opentbd website
 */
private data class OpentdbData(
    var response_code : Int,
    var results : MutableList<Result>
)

/**
 * This class represents the json data fetched from Opentbd website
 */
private data class Result(
    val category : String,
    val type : String,
    val difficulty : String,
    val question : String,
    val correct_answer : String,
    val incorrect_answers : MutableList<String>
)

/**
 * This class is an utils for fetching data from Opentdb website via HTTP connection
 */
class OpentdbUtils {
    companion object {

        /**
         * This function fetch [OpentdbData] from a given url
         *
         * @param[urlS] Url where object are fetched from
         * @return Returns an [QuestionDB] that is a [OpentdbData] converted
         */
        fun fetch(urlS : String) : QuestionDB {
            val url = URL(urlS)
            val conn = url.openConnection() as HttpURLConnection
            val urlIn = BufferedReader(InputStreamReader(conn.inputStream))

            val resultJson = StringBuilder()

            var line = urlIn.readLine()
            while(line != null) {
                resultJson.append(line)
                line = urlIn.readLine()
            }

            val gson = Gson()
            return convert(gson.fromJson(resultJson.toString(),OpentdbData::class.java))
        }

        /**
         * This function converts an [OpentdbData] object into a [QuestionDB] object
         *
         * @param[data] The [OpentdbData] object
         * @return The [QuestionDB] object converted
         */
        private fun convert(data: OpentdbData) : QuestionDB {
            val questionList = ArrayList<Question>()

            data.results.forEach { r ->
                questionList += Question(
                    r.category,r.type,r.difficulty,r.question,Answer(r.correct_answer),r.incorrect_answers.map { Answer(it) }
                )
            }

            return QuestionDB(questionList)
        }
    }
}