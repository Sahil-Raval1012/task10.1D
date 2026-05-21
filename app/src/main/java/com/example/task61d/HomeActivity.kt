package com.example.task61d
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.task61d.adapters.TaskAdapter
import com.example.task61d.data.DummyData
import com.example.task61d.data.UserSession
class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        findViewById<TextView>(R.id.tvName).text = UserSession.username
        val tasks = DummyData.generateTasksFor(UserSession.interests)
        findViewById<TextView>(R.id.tvDue).text = getString(R.string.task_due, tasks.size)
        val rv = findViewById<RecyclerView>(R.id.rvTasks)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = TaskAdapter(tasks) { task ->
            UserSession.lastTask = task
            startActivity(Intent(this, TaskActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
        findViewById<FrameLayout>(R.id.flAvatar).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }
}
