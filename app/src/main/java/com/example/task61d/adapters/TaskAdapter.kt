package com.example.task61d.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.task61d.R
import com.example.task61d.data.LearningTask

class TaskAdapter(
    private val tasks: List<LearningTask>,
    private val onClick: (LearningTask) -> Unit
) : RecyclerView.Adapter<TaskAdapter.VH>() {

    class VH(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val desc: TextView = itemView.findViewById(R.id.tvDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_task_card, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = tasks[position]
        holder.title.text = item.title
        holder.desc.text = item.description
        holder.itemView.setOnClickListener { onClick(item) }
        holder.itemView.alpha = 0f
        holder.itemView.translationY = 40f
        holder.itemView.animate()
            .alpha(1f).translationY(0f)
            .setStartDelay(position * 80L)
            .setDuration(350)
            .start()
    }

    override fun getItemCount(): Int = tasks.size
}
