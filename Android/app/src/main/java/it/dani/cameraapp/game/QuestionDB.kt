package it.dani.cameraapp.game

data class QuestionDB(val questions : List<Question>)

data class Question(
    val category : String,
    val type : String,
    val difficulty : String,
    val question : String,
    val correctAnswer : Answer,
    val incorrectAnswer : List<Answer>
)

data class Answer(val value : String, val point : Int = 1)