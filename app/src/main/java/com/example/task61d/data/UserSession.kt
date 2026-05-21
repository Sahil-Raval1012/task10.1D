package com.example.task61d.data

object UserSession {
    var username: String = "Student"
    var email: String = ""
    var interests: MutableList<String> = mutableListOf()
    var lastResults: List<AnswerResult> = emptyList()
    var lastTask: LearningTask? = null
    var history: MutableList<HistoryEntry> = mutableListOf()
    var isPremium: Boolean = false

    fun addHistoryEntry(entry: HistoryEntry) {
        history.add(0, entry)
    }

    val totalQuizzesDone: Int get() = history.size
    val totalQuestionsAnswered: Int get() = history.sumOf { it.total }
    val totalCorrect: Int get() = history.sumOf { it.score }
    val totalIncorrect: Int get() = totalQuestionsAnswered - totalCorrect

    val averageScore: Int
        get() {
            if (history.isEmpty()) return 0
            return history.sumOf { it.percentage } / history.size
        }

    fun getAllIncorrectAnswers(): List<AnswerResult> =
        history.flatMap { it.answers.filter { ar -> !ar.isCorrect } }
}
