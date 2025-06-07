package com.example.qrattendance.presentation.adapters


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qrattendance.R
import com.example.qrattendance.data.models.StudentPerformance
import com.example.qrattendance.data.models.SubjectGroupPerformanceResponse

class SubjectPerformanceAdapter(
    private var items: List<SubjectGroupPerformanceResponse>
) : RecyclerView.Adapter<SubjectPerformanceAdapter.SubjectPerformanceViewHolder>() {

    inner class SubjectPerformanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.student_name)
        val midterm: TextView = itemView.findViewById(R.id.midterm)
        val endterm: TextView = itemView.findViewById(R.id.endterm)
        val avg: TextView = itemView.findViewById(R.id.avg)
        val final: TextView = itemView.findViewById(R.id.fr)
        val total: TextView = itemView.findViewById(R.id.total)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectPerformanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_performance, parent, false)
        return SubjectPerformanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectPerformanceViewHolder, position: Int) {
        val student = items[position]
        holder.name.text = student.name

        if (student.student_performance.isNotEmpty()) {
            val performance = student.student_performance.first()

            val mt = performance.point1 ?: 0
            val et = performance.point2 ?: 0
            val avg = (mt + et) / 2
            val finalExam = performance.exam_mark ?: 0


            val totalPoints = (finalExam * 0.4 + avg * 0.6).toInt()


            holder.midterm.text = mt.toString()
            holder.endterm.text  = et.toString()
            holder.avg.text = avg.toString()
            holder.final.text  = finalExam.toString()
            holder.total.text  = totalPoints.toString()

        } else {
            holder.midterm.text = "N/A"
            holder.endterm.text = "N/A"
            holder.avg.text = "N/A"
            holder.final.text = "N/A"
            holder.total.text = "N/A"
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(data: List<SubjectGroupPerformanceResponse>) {
        items = data
        notifyDataSetChanged()
    }

    fun getData(): List<SubjectGroupPerformanceResponse> = items
}
