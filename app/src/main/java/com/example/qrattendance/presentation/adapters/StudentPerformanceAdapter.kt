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
                // Set course title and professor information
                courseText.text = item.title ?: "No title"
                teacherText.text = "Professor: ${item.professor_subject.firstOrNull()?.users?.name ?: "Unknown"}"
                reportTypeText.text = "Reporting type: ${item.reporting_type ?: "N/A"}"

                // Handle student performance data
                if (item.student_performance.isNotEmpty()) {
                    val performance = item.student_performance.first()

                    // Use safe calls to handle null values
                    val mt = performance.point1 ?: 0
                    val et = performance.point2 ?: 0
                    val finalExam = performance.exam_mark ?: 0

                    // Calculating average and total points
                    val avg = (mt + et) / 2 // Assuming avg is the simple average of mt and et
                    val totalPoint = mt + et + finalExam

                    // Set the text for various fields
                    mtText.text = mt.toString()
                    etText.text = et.toString()
                    avgText.text = avg.toString()
                    finalText.text = finalExam.toString()
                    ttlText.text = String.format("%.2f", totalPoint.toFloat())

                } else {
                    // If there's no performance data, show "N/A"
                    mtText.text = "N/A"
                    etText.text = "N/A"
                    avgText.text = "N/A"
                    finalText.text = "N/A"
                    ttlText.text = "N/A"
                    resultText.text = "N/A"
                }
            }
        }
    }

}