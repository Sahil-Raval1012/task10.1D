package com.example.task61d
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.task61d.data.DummyData
import com.example.task61d.data.UserSession
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
class InterestsActivity : AppCompatActivity() {
    private val maxSelectable = 10
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interests)
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }
        val chipGroup = findViewById<ChipGroup>(R.id.chipGroup)
        chipGroup.isSingleSelection = false
        val checkedColor = ContextCompat.getColor(this, R.color.brand_green_dark)
        val uncheckedColor = ContextCompat.getColor(this, R.color.brand_blue_deep)
        val chipColors = ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            ),
            intArrayOf(checkedColor, uncheckedColor)
        )
        DummyData.topics.forEach { topic ->
            val chip = Chip(this).apply {
                text = topic
                isCheckable = true
                isClickable = true
                chipBackgroundColor = chipColors
                setTextColor(ContextCompat.getColor(context, R.color.white))
                contentDescription = "Interest: $topic"
            }
            chipGroup.addView(chip, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.size > maxSelectable) {
                val lastId = checkedIds.last()
                (group.findViewById<Chip>(lastId)).isChecked = false
                Toast.makeText(this, "You may select up to $maxSelectable topics", Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<Button>(R.id.btnNext).setOnClickListener {
            val selected = chipGroup.checkedChipIds.mapNotNull { id ->
                chipGroup.findViewById<Chip>(id)?.text?.toString()
            }
            if (selected.isEmpty()) {
                Toast.makeText(this, "Please pick at least one topic", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            UserSession.interests = selected.toMutableList()
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            finish()
        }
    }
}
