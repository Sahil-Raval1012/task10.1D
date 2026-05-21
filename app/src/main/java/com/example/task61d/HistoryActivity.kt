package com.example.task61d
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.task61d.adapters.HistoryAdapter
import com.example.task61d.data.AnswerResult
import com.example.task61d.data.UserSession
class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        val tvEmpty = findViewById<TextView>(R.id.tvEmpty)
        val rv = findViewById<RecyclerView>(R.id.rvHistory)
        val allAnswers = mutableListOf<AnswerResult>()
        val allNumbers = mutableListOf<Int>()
        var qNum = 1
        UserSession.history.forEach { entry ->
            entry.answers.forEach { ar ->
                allAnswers.add(ar)
                allNumbers.add(qNum++)
            }
        }
        if (allAnswers.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rv.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rv.visibility = View.VISIBLE
            rv.layoutManager = LinearLayoutManager(this)
            rv.adapter = HistoryAdapter(allAnswers, allNumbers)
        }
    }
}
