package com.example.qrattendance.data.models

data class AttendanceResponse(
    val student_login: String,
    val subject_id: Int,
    val status: String,
    val date: String,
    val time: String
)

data class StudentAttendance(
    val login: String,
    val name: String,
    val attendance: List<AttendanceResponse>
)

data class Group(
    val id: Int,
    val title: String,
    val semester: Int
)

data class StudentGroupResponse(
    val login: String,
    val group_id: Int,
    val groups: Group
)



data class Subject(
    val id: Int,
    val title: String,
    val semester: Int,
    val reporting_type: String
)

data class SubjectDetailed(
    val id: Int,
    val title: String,
    val semester: Int,
    val reporting_type: String,
    val professors_login: List<String>,
    val groups_id: List<Int>
)

data class StudentPerformance(
    val student_login: String,
    val subject_id: Int,
    val point1: Int?,
    val point2: Int?,
    val point3: Int?,
    val exam_mark: Int?
)


data class StudentPerformanceResponse(
    val id: Int,
    val title: String,
    val semester: Int,
    val reporting_type: String,
    val student_performance: List<StudentPerformance>,
    val professor_subject: List<ProfessorUser>
)

data class ProfessorUser(
    val users: User
)

data class User(
    val name: String,
    val login: String? = null
)