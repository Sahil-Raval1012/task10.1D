package com.example.task61d
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.task61d.data.AnswerResult
import com.example.task61d.data.UserSession
import com.example.task61d.llm.LlmClient
import kotlinx.coroutines.launch
class TaskActivity : AppCompatActivity() {
    private val selections = mutableMapOf<Int, Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        val task = UserSession.lastTask ?: run { finish(); return }
        @Suppress("UNCHECKED_CAST")
        val restored = savedInstanceState?.getSerializable("selections") as? HashMap<Int, Int>
        if (restored != null) selections.putAll(restored)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<TextView>(R.id.tvTitle).text = task.title
        findViewById<TextView>(R.id.tvDesc).text = task.description
        val container = findViewById<LinearLayout>(R.id.questionsContainer)
        val inflater = LayoutInflater.from(this)
        task.questions.forEachIndexed { qIndex, question ->
            val qView = inflater.inflate(R.layout.item_question, container, false)
            qView.findViewById<TextView>(R.id.tvQuestionNum).text = "${qIndex + 1}. Question ${qIndex + 1}"
            qView.findViewById<TextView>(R.id.tvQuestionText).text = question.text
            val rg = qView.findViewById<RadioGroup>(R.id.rgAnswers)
            question.options.forEachIndexed { aIndex, option ->
                val white = ContextCompat.getColor(this, R.color.white)
                val rb = RadioButton(this).apply {
                    id = View.generateViewId()
                    text = option
                    setTextColor(white)
                    buttonTintList = android.content.res.ColorStateList.valueOf(white)
                    contentDescription = "Answer option ${aIndex + 1}: $option"
                }
                rg.addView(rb)
                if (selections[qIndex] == aIndex) rb.isChecked = true
                rb.setOnClickListener { selections[qIndex] = aIndex }
            }
            val tvHint = qView.findViewById<TextView>(R.id.tvHint)
            qView.findViewById<Button>(R.id.btnHint).setOnClickListener {
                val prompt = "Give a single concise hint (max 2 sentences) for this question " +
                        "without revealing the answer: \"${question.text}\""
                tvHint.visibility = View.VISIBLE
                tvHint.text = getString(R.string.loading)
                lifecycleScope.launch {
                    when (val result = LlmClient.generate(prompt)) {
                        is LlmClient.Result.Success -> tvHint.text = result.data.text
                        is LlmClient.Result.Failure -> tvHint.text = "Could not load hint: ${result.message}"
                    }
                }
            }
            qView.alpha = 0f
            qView.translationY = 40f
            qView.animate().alpha(1f).translationY(0f)
                .setStartDelay(qIndex * 70L).setDuration(300).start()
            container.addView(qView)
        }
        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            if (selections.size < task.questions.size) {
                Toast.makeText(this, "Please answer all questions", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            UserSession.lastResults = task.questions.mapIndexed { i, q ->
                AnswerResult(q, selections[i] ?: -1)
            }
            startActivity(Intent(this, ResultsActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable("selections", HashMap(selections))
    }
}
