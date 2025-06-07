package com.example.qrattendance.data.models

data class StudentAttendanceSummary(
    val name: String,
    val attended: Int,
    val absent: Int,
    val excused: Int,
    val total: Int
)

fun List<GroupAttendanceResponse>.toAttendanceSummaryList(): List<StudentAttendanceSummary> {
    return this.map { response ->
        val attendanceList = response.student.attendance

        val attended = attendanceList.count { it.status == "P" }
        val absent = attendanceList.count { it.status == "A" }
        val excused = attendanceList.count { it.status == "R" }

        val total = attendanceList.size

        StudentAttendanceSummary(
            name = response.student.name,
            attended = attended,
            absent = absent,
            excused = excused,
            total = total
        )
    }
}
