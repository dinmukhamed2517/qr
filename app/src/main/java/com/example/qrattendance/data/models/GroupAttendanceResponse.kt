package com.example.qrattendance.data.models

data class GroupAttendanceResponse(
    val student: StudentWithAttendance
)

data class StudentWithAttendance(
    val login: String,
    val name: String,
    val attendance: List<AttendanceRecord>
)

data class AttendanceRecord(
    val subject_id: Int?,
    val status: String?, // Example: "P", "A", "R"
    val date: String?,
    val time: String?
)
