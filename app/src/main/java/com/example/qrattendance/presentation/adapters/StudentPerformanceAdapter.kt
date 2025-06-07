package com.example.qrattendance.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.qrattendance.data.models.StudentPerformanceResponse
import com.example.qrattendance.databinding.ItemStudentPerformanceBinding
import com.example.qrattendance.presentation.base.BaseStudentPerformanceItemViewHolder

class StudentPerformanceAdapter: ListAdapter<StudentPerformanceResponse, BaseStudentPerformanceItemViewHolder<*>>(ReceiveItemDiffUtils()) {

    class ReceiveItemDiffUtils: DiffUtil.ItemCallback<StudentPerformanceResponse>(){
        override fun areItemsTheSame(oldItem: StudentPerformanceResponse, newItem: StudentPerformanceResponse): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StudentPerformanceResponse, newItem: StudentPerformanceResponse): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseStudentPerformanceItemViewHolder<*> {
        return ReceiveItemHolder(ItemStudentPerformanceBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: BaseStudentPerformanceItemViewHolder<*>, position: Int) {
        holder.bindView(getItem(position))
    }

    inner class ReceiveItemHolder(binding: ItemStudentPerformanceBinding) : BaseStudentPerformanceItemViewHolder<ItemStudentPerformanceBinding>(binding) {
        override fun bindView(item: StudentPerformanceResponse) {
            with(binding) {
                courseText.text = item.title ?: "No title"
                teacherText.text = "Professor: ${item.professor_subject.firstOrNull()?.users?.name ?: "Unknown"}"
                reportTypeText.text = "Reporting type: ${item.reporting_type ?: "N/A"}"

                if (item.student_performance.isNotEmpty()) {
                    val performance = item.student_performance.first()

                    val mt = performance.point1 ?: 0
                    val et = performance.point2 ?: 0
                    val avg = (mt + et) / 2
                    val finalExam = performance.exam_mark ?: 0


                    val totalPoints = (finalExam * 0.4 + avg * 0.6).toInt()
                    val finalResult = getFinalMarkForPoints(totalPoints)


                    mtText.text = mt.toString()
                    etText.text = et.toString()
                    avgText.text = avg.toString()
                    finalText.text = finalExam.toString()
                    ttlText.text = totalPoints.toString()
                    resultText.text = finalResult
                } else {
                    mtText.text = "N/A"
                    etText.text = "N/A"
                    avgText.text = "N/A"
                    finalText.text = "N/A"
                    ttlText.text = "N/A"
                    resultText.text = "N/A"
                }
            }
        }

        // The function to determine the final result based on total points
        private fun getFinalMarkForPoints(points: Int): String {
            return when {
                points >= 95 -> "Excellent"
                points >= 90 -> "Excellent"
                points >= 85 -> "Good"
                points >= 80 -> "Good"
                points >= 75 -> "Good"
                points >= 70 -> "Good"
                points >= 65 -> "Satisfactory"
                points >= 60 -> "Satisfactory"
                points >= 55 -> "Satisfactory"
                points >= 50 -> "Satisfactory"
                points >= 25 -> "Unsatisfactory"
                points >= 0 -> "Unsatisfactory"
                else -> "Invalid input"
            }
        }
    }

}