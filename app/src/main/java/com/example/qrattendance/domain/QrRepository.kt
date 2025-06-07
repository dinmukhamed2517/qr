package com.example.qrattendance.domain

import com.example.qrattendance.data.models.ChangePasswordRequest
import com.example.qrattendance.data.models.CheckAttendanceResponse
import com.example.qrattendance.data.models.GroupAttendanceResponse
import com.example.qrattendance.data.models.LoginRequest
import com.example.qrattendance.data.models.LoginResponse
import com.example.qrattendance.data.models.StudentPerformanceResponse
import com.example.qrattendance.data.models.StudentScheduleResponse
import com.example.qrattendance.data.models.SubjectGroupPerformanceResponse
import com.example.qrattendance.data.models.UserInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Path

interface QrRepository {

    suspend fun login(request: LoginRequest): LoginResponse?


    suspend fun getAllPerformanceForStudent(studentLogin: String): List<StudentPerformanceResponse>

    suspend fun getStudentSchedule(studentLogin: String, startDate: String, endDate: String): List<StudentScheduleResponse>?

    suspend fun getAttendanceForSubjectGroupProfessor(
        professorLogin: String,
        subjectId: Int,
        groupId: Int
    ): List<GroupAttendanceResponse>

    suspend fun getPerformanceForSubjectGroup(subjectId: Int, groupId: Int): List<SubjectGroupPerformanceResponse>
    suspend fun getUserInfo(): Response<UserInfoResponse>
    suspend fun checkAttendance(studentLogin: String, code: String):Response<CheckAttendanceResponse>
    suspend fun changePassword(request:ChangePasswordRequest): Response<Unit>
}
