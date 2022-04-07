package it.dani.cameraapp.game

/**
 * @author Daniele
 *
 * This class represents a list of questions
 *
 * @param[questions] The list of [Question]
 */
data class QuestionDB(val questions : List<Question>)

/**
 * This class represents a single question
 */
data class Question(
    val category : String,
    val type : String,
    val difficulty : String,
    val question : String,
    val correctAnswer : Answer,
    val incorrectAnswer : List<Answer>
)

/**
 * This class represents an answer of a question (correct or incorrect doesn't matter)
 */
data class Answer(val value : String, val point : Int = 1)