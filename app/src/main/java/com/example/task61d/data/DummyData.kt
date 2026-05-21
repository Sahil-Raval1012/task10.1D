package com.example.task61d.data

object DummyData {
    val topics = listOf(
        "Algorithms", "Data Structures", "Web Development", "Testing",
        "Machine Learning", "Databases", "Mobile Dev", "Cloud",
        "Cyber Security", "DevOps", "UI Design", "Networking",
        "Operating Systems", "AI Ethics", "Computer Vision", "NLP"
    )

    fun generateTasksFor(interests: List<String>): List<LearningTask> {
        val pool = if (interests.isEmpty()) topics.take(3) else interests
        return pool.take(3).mapIndexed { index, topic ->
            LearningTask(
                id = index + 1,
                title = "Generated Task ${index + 1}",
                description = "A short personalised quiz on $topic based on your interests.",
                topic = topic,
                questions = sampleQuestionsFor(topic)
            )
        }
    }

    private fun sampleQuestionsFor(topic: String): List<Question> = when (topic) {
        "Algorithms" -> listOf(
            Question(
                "What is the average time complexity of binary search?",
                listOf("O(n)", "O(log n)", "O(n log n)"),
                1
            ),
            Question(
                "Which algorithm is used for shortest path in a weighted graph?",
                listOf("BFS", "Dijkstra", "DFS"),
                1
            ),
            Question(
                "Quick sort is best described as a ____ algorithm.",
                listOf("Divide and conquer", "Greedy", "Dynamic programming"),
                0
            )
        )
        "Data Structures" -> listOf(
            Question(
                "Which data structure uses LIFO order?",
                listOf("Queue", "Stack", "Heap"),
                1
            ),
            Question(
                "A hash map provides average lookup of?",
                listOf("O(1)", "O(log n)", "O(n)"),
                0
            ),
            Question(
                "Which structure is ideal for BFS traversal?",
                listOf("Stack", "Queue", "Tree"),
                1
            )
        )
        else -> listOf(
            Question(
                "What is a key benefit of studying $topic?",
                listOf("It builds practical skills", "It is unrelated", "It is only theory"),
                0
            ),
            Question(
                "Which best describes a good practice in $topic?",
                listOf("Skip fundamentals", "Practice consistently", "Avoid testing"),
                1
            ),
            Question(
                "Why include $topic in your study plan?",
                listOf("It is trending only", "It expands your toolkit", "It has no use"),
                1
            )
        )
    }
}
