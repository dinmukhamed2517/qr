package com.example.qrattendance.data.models

data class CheckAttendanceResponse(
    val student_login: String,
    val lesson_id: Int,
    val status: String,
    val date:String
)
