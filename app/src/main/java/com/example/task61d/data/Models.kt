package com.example.task61d.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Question(
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)

data class LearningTask(
    val id: Int,
    val title: String,
    val description: String,
    val topic: String,
    val questions: List<Question>
)

data class AnswerResult(
    val question: Question,
    val selectedIndex: Int
) {
    val isCorrect: Boolean get() = selectedIndex == question.correctIndex
}

data class HistoryEntry(
    val taskTitle: String,
    val topic: String,
    val score: Int,
    val total: Int,
    val answers: List<AnswerResult> = emptyList(),
    val timestamp: Long = System.currentTimeMillis()
) {
    val percentage: Int get() = if (total > 0) (score * 100) / total else 0

    val formattedDate: String
        get() {
            val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }
}
