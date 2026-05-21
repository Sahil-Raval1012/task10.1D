package com.example.task61d
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.task61d.data.UserSession
import com.example.task61d.llm.LlmClient
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
class ProfileActivity : AppCompatActivity() {
    private lateinit var tvAiSummaryText: TextView
    private var aiQueryInProgress = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tvProfileName).text = UserSession.username
        val emailDisplay = UserSession.email.ifEmpty { "username@example.com" }
        findViewById<TextView>(R.id.tvProfileEmail).text = emailDisplay
        tvAiSummaryText = findViewById(R.id.tvAiSummaryText)
        refreshStats()
        findViewById<LinearLayout>(R.id.cardAiSummary).setOnClickListener {
            queryAiSummary()
        }
        findViewById<Button>(R.id.btnHistory).setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        findViewById<Button>(R.id.btnUpgrade).setOnClickListener {
            startActivity(Intent(this, UpgradeActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        findViewById<Button>(R.id.btnShare).setOnClickListener {
            shareProfile()
        }
    }
    override fun onResume() {
        super.onResume()
        refreshStats()
    }
    private fun refreshStats() {
        findViewById<TextView>(R.id.tvStatTotal).text = UserSession.totalQuestionsAnswered.toString()
        findViewById<TextView>(R.id.tvStatCorrect).text = UserSession.totalCorrect.toString()
        findViewById<TextView>(R.id.tvStatIncorrect).text = UserSession.totalIncorrect.toString()
        val tvPremium = findViewById<TextView>(R.id.tvPremiumBadge)
        tvPremium.text = if (UserSession.isPremium) getString(R.string.premium) else getString(R.string.free_plan)
    }
    private fun queryAiSummary() {
        if (aiQueryInProgress) return
        val incorrectAnswers = UserSession.getAllIncorrectAnswers()
        if (incorrectAnswers.isEmpty()) {
            Toast.makeText(this, "Complete some quizzes first to get a summary!", Toast.LENGTH_SHORT).show()
            return
        }
        aiQueryInProgress = true
        tvAiSummaryText.text = getString(R.string.loading)
        val prompt = "I got these quiz answers wrong. Please give me a brief 3-sentence study tip summary:\n" +
            incorrectAnswers.take(5).joinToString("\n") { ar ->
                "Q: ${ar.question.text} | Correct: ${ar.question.options[ar.question.correctIndex]}"
            }
        lifecycleScope.launch {
            when (val result = LlmClient.generate(prompt)) {
                is LlmClient.Result.Success -> {
                    val summary = result.data.text
                    tvAiSummaryText.text = summary
                    MaterialAlertDialogBuilder(this@ProfileActivity)
                        .setTitle(getString(R.string.summarized_by_ai))
                        .setMessage(summary)
                        .setPositiveButton("Got it", null)
                        .show()
                }
                is LlmClient.Result.Failure -> {
                    tvAiSummaryText.text = getString(R.string.ai_summary_hint)
                    Toast.makeText(this@ProfileActivity, "Could not load summary: ${result.message}", Toast.LENGTH_SHORT).show()
                }
            }
            aiQueryInProgress = false
        }
    }
    private fun shareProfile() {
        val interests = if (UserSession.interests.isEmpty()) "None"
        else UserSession.interests.joinToString(", ")
        val text = buildString {
            appendLine("Check out my Learning Assistant profile!")
            appendLine()
            appendLine("Name: ${UserSession.username}")
            appendLine("Total questions answered: ${UserSession.totalQuestionsAnswered}")
            appendLine("Correctly answered: ${UserSession.totalCorrect}")
            appendLine("Average score: ${UserSession.averageScore}%")
            appendLine("Interests: $interests")
            appendLine()
            appendLine("Powered by LLM Learning Assistant")
        }
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "${UserSession.username}'s Learning Profile")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        startActivity(Intent.createChooser(shareIntent, "Share Profile via"))
    }
}
