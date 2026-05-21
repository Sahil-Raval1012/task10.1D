package com.example.task61d
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.task61d.data.UserSession
class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etConfirmEmail = findViewById<EditText>(R.id.etConfirmEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        findViewById<Button>(R.id.btnCreate).setOnClickListener {
            val u = etUsername.text.toString().trim()
            val e = etEmail.text.toString().trim()
            val ec = etConfirmEmail.text.toString().trim()
            val p = etPassword.text.toString()
            val pc = etConfirmPassword.text.toString()
            val ph = etPhone.text.toString().trim()
            when {
                u.isEmpty() || e.isEmpty() || p.isEmpty() || ph.isEmpty() ->
                    Toast.makeText(this, "Please complete all fields", Toast.LENGTH_SHORT).show()
                e != ec ->
                    Toast.makeText(this, "Emails do not match", Toast.LENGTH_SHORT).show()
                p != pc ->
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                else -> {
                    UserSession.username = u
                    UserSession.email = e
                    startActivity(Intent(this, InterestsActivity::class.java))
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                }
            }
        }
    }
}
