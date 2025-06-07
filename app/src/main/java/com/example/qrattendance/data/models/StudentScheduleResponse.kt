package com.example.qrattendance.data.models

data class StudentScheduleResponse(
    val date: String,
    val lessons: List<Lesson>
)

data class Lesson(
    val time: String,
    val title: String,
    val attendance: String,
    val place: String,
    val professorName: String
)
data class DateRange(
    val start_date: String,
    val end_date: String
)
