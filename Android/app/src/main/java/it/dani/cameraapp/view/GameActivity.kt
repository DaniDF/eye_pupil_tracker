package it.dani.cameraapp.view

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.snackbar.Snackbar
import it.dani.cameraapp.R
import it.dani.cameraapp.game.Question
import it.dani.cameraapp.game.QuestionDB
import it.dani.cameraapp.game.QuestionFetcher
import it.dani.cameraapp.view.utils.ViewUtils

class GameActivity : AppCompatActivity() {

    private lateinit var questionFetcher : QuestionFetcher

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        super.setContentView(R.layout.game_activity)
        ViewUtils.hideSystemBars(this.window)

        this.questionFetcher = QuestionFetcher(this).apply {
            onSuccess += {
                if(it.questions.isNotEmpty()) {
                    this@GameActivity.displayGame(it)
                }
            }
        }

        this.questionFetcher.fetch()
    }

    private fun displayGame(questions : QuestionDB) {
        questions.questions.first().let { q ->
            findViewById<TextView>(R.id.questionText).apply {
                text = q.question
            }

            this.displayAnswerButtons(q)
        }
    }

    private fun displayAnswerButtons(question: Question) {
        val gameActivity = findViewById<View>(R.id.game_activity)

        val buttons = listOf(R.id.aResponse,R.id.bResponse,R.id.cResponse,R.id.dResponse)
        val answers = mutableSetOf(question.correctAnswer).also { a -> a.addAll(question.incorrectAnswer) }.iterator()

        buttons.forEach { b ->
            findViewById<ExtendedFloatingActionButton>(b).apply {
                text = answers.next().value
                setOnClickListener {
                    val response = when(this.text) {
                        question.correctAnswer.value -> {
                            this@GameActivity.questionFetcher.fetch()
                            this@GameActivity.resources.getString(R.string.game_answer_correct)
                        }
                        else -> this@GameActivity.resources.getString(R.string.game_answer_wrong)
                    }
                    Snackbar.make(gameActivity,response,Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }
}