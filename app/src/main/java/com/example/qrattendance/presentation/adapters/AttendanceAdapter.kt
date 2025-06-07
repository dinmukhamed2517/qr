package com.example.qrattendance.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qrattendance.R
import com.example.qrattendance.data.models.GroupAttendanceResponse
import com.example.qrattendance.data.models.StudentAttendanceSummary

class AttendanceAdapter(private val list: List<StudentAttendanceSummary>) :
    RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    inner class AttendanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.student_name)
        val attended: TextView = itemView.findViewById(R.id.attended)
        val absent: TextView = itemView.findViewById(R.id.absent)
        val excused: TextView = itemView.findViewById(R.id.excused)
        val total: TextView = itemView.findViewById(R.id.all)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance_row, parent, false)
        return AttendanceViewHolder(view)
    }
    fun getData(): List<StudentAttendanceSummary> = list
    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val record = list[position]
        holder.name.text = record.name
        holder.attended.text = record.attended.toString()
        holder.absent.text = record.absent.toString()
        holder.excused.text = record.excused.toString()
        holder.total.text = record.total.toString()
    }

    override fun getItemCount(): Int = list.size
}

