package com.example.qrattendance.presentation.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.qrattendance.data.models.Lesson
import com.example.qrattendance.databinding.ItemCourseBinding

class CourseAdapter: ListAdapter<Lesson, CourseAdapter.LessonViewHolder>(LessonDiffCallback()) {

    inner class LessonViewHolder(private val binding: ItemCourseBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(lesson: Lesson) {
            Log.d("Home Adapter", "${lesson}")
            binding.timeText.text = lesson.time
            binding.courseName.text = lesson.title
            binding.audienceNumber.text = "Audience: ${lesson.place}"
            binding.teacher.text = "Teacher: ${lesson.professorName}"
        }
    }

    class LessonDiffCallback : DiffUtil.ItemCallback<Lesson>() {
        override fun areItemsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem.place == newItem.place && oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Lesson, newItem: Lesson): Boolean {
            return oldItem == newItem
        }
    }
    override fun getItemCount(): Int {
        val count = super.getItemCount()
        Log.d("Adapter", "Item count: $count")
        return count
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LessonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LessonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
