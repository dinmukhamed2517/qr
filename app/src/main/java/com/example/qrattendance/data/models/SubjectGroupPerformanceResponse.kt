package com.example.qrattendance.data.models

data class SubjectGroupPerformanceResponse(
    val login: String,
    val rights_id: Int,
    val name: String,
    val student_performance: List<StudentPerformance>
)
