package com.example.task61d
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.task61d.data.HistoryEntry
import com.example.task61d.data.UserSession
import com.example.task61d.llm.LlmClient
import kotlinx.coroutines.launch
class ResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)
        val results = UserSession.lastResults
        val score = results.count { it.isCorrect }
        if (savedInstanceState == null) {
            val task = UserSession.lastTask
            if (task != null) {
                UserSession.addHistoryEntry(
                    HistoryEntry(
                        taskTitle = task.title,
                        topic = task.topic,
                        score = score,
                        total = results.size,
                        answers = results
                    )
                )
            }
        }
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        val tvScore = findViewById<TextView>(R.id.tvScore)
        tvScore.text = "Score: $score / ${results.size}"
        val container = findViewById<LinearLayout>(R.id.resultsContainer)
        val inflater = LayoutInflater.from(this)
        results.forEachIndexed { index, ar ->
            val v = inflater.inflate(R.layout.item_result, container, false)
            v.findViewById<TextView>(R.id.tvNum).text = "${index + 1}. Question ${index + 1}"
            val correctText = ar.question.options[ar.question.correctIndex]
            val pickedText = if (ar.selectedIndex in ar.question.options.indices)
                ar.question.options[ar.selectedIndex] else "(no answer)"
            val verdict = if (ar.isCorrect) "Correct ✓" else "Incorrect ✗"
            v.findViewById<TextView>(R.id.tvBody).text =
                "$verdict\nYour answer: $pickedText\nCorrect answer: $correctText"
            val tvPrompt = v.findViewById<TextView>(R.id.tvPrompt)
            val tvExpl = v.findViewById<TextView>(R.id.tvExplanation)
            val btnExplain = v.findViewById<Button>(R.id.btnExplain)
            btnExplain.text = if (ar.isCorrect) "Why is this correct?" else "Why is this incorrect?"
            btnExplain.setOnClickListener {
                val prompt = if (ar.isCorrect) {
                    "In 2-3 sentences, explain why \"$correctText\" is the CORRECT answer to: \"${ar.question.text}\"."
                } else {
                    "In 2-3 sentences, explain why \"$pickedText\" is INCORRECT for: \"${ar.question.text}\", and why \"$correctText\" is the right answer."
                }
                tvPrompt.visibility = View.VISIBLE
                tvExpl.visibility = View.VISIBLE
                tvPrompt.text = "Prompt: $prompt"
                tvExpl.text = getString(R.string.loading)
                lifecycleScope.launch {
                    when (val r = LlmClient.generate(prompt)) {
                        is LlmClient.Result.Success -> tvExpl.text = r.data.text
                        is LlmClient.Result.Failure -> tvExpl.text = "Could not load explanation: ${r.message}"
                    }
                }
            }
            v.alpha = 0f
            v.translationY = 40f
            v.animate().alpha(1f).translationY(0f)
                .setStartDelay(index * 90L).setDuration(320).start()
            container.addView(v)
        }
        findViewById<Button>(R.id.btnContinue).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            finish()
        }
    }
}
