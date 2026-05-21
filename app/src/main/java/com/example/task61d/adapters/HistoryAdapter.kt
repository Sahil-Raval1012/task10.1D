package com.example.task61d.adapters

import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.task61d.R
import com.example.task61d.data.AnswerResult

class HistoryAdapter(
    private val items: List<AnswerResult>,
    private val questionNumbers: List<Int>
) : RecyclerView.Adapter<HistoryAdapter.VH>() {

    private val expanded = mutableSetOf<Int>()

    private val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val llHeader: LinearLayout = itemView.findViewById(R.id.llHistoryHeader)
        val tvHeader: TextView = itemView.findViewById(R.id.tvHistoryHeader)
        val tvArrow: TextView = itemView.findViewById(R.id.tvExpandArrow)
        val llContent: LinearLayout = itemView.findViewById(R.id.llHistoryContent)
        val tvQuestion: TextView = itemView.findViewById(R.id.tvHistoryQuestion)
        val llAnswers: LinearLayout = itemView.findViewById(R.id.llAnswers)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val ar = items[position]
        val qNum = questionNumbers[position]
        val isExpanded = position in expanded

        val truncated = if (ar.question.text.length > 45)
            ar.question.text.take(45) + "…"
        else ar.question.text
        holder.tvHeader.text = "$qNum. $truncated"
        holder.tvArrow.text = if (isExpanded) "▲" else "▼"
        holder.llContent.visibility = if (isExpanded) View.VISIBLE else View.GONE

        if (isExpanded) {
            holder.tvQuestion.text = ar.question.text
            holder.llAnswers.removeAllViews()

            ar.question.options.forEachIndexed { i, option ->
                val ctx = holder.itemView.context

                val row = LinearLayout(ctx).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    val lp = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    lp.setMargins(0, 0, 0, 8.dp)
                    layoutParams = lp
                }

                // Colored circle indicator
                val dot = View(ctx).apply {
                    val size = 12.dp
                    val lp = LinearLayout.LayoutParams(size, size)
                    lp.marginEnd = 10.dp
                    lp.gravity = Gravity.CENTER_VERTICAL
                    layoutParams = lp
                    val color = when {
                        i == ar.question.correctIndex -> 0xFF32CD32.toInt() // green — correct answer
                        i == ar.selectedIndex -> 0xFFE74C3C.toInt()         // red — wrong selection
                        else -> 0x4DFFFFFF.toInt()                           // faded — not selected
                    }
                    background = GradientDrawable().apply {
                        shape = GradientDrawable.OVAL
                        setColor(color)
                    }
                }

                val label = when {
                    i == ar.question.correctIndex && i == ar.selectedIndex -> "\nCorrect!"
                    i == ar.question.correctIndex -> "\nCorrect Answer"
                    i == ar.selectedIndex -> "\nYour Answer"
                    else -> ""
                }

                val tv = TextView(ctx).apply {
                    text = if (label.isEmpty()) option else "$option$label"
                    setTextColor(0xFFFFFFFF.toInt())
                    textSize = 13f
                    layoutParams = LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1f
                    )
                }

                row.addView(dot)
                row.addView(tv)
                holder.llAnswers.addView(row)
            }
        }

        holder.llHeader.setOnClickListener {
            val pos = holder.adapterPosition
            if (pos == RecyclerView.NO_ID.toInt()) return@setOnClickListener
            if (pos in expanded) expanded.remove(pos) else expanded.add(pos)
            notifyItemChanged(pos)
        }

        // Entrance animation
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 30f
        holder.itemView.animate()
            .alpha(1f).translationY(0f)
            .setStartDelay(position * 60L)
            .setDuration(280)
            .start()
    }

    override fun getItemCount(): Int = items.size
}
