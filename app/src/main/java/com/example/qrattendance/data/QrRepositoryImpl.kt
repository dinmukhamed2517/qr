package com.example.qrattendance.data

import com.example.qrattendance.data.apis.QrApi
import com.example.qrattendance.data.models.ChangePasswordRequest
import com.example.qrattendance.data.models.CheckAttendanceResponse
import com.example.qrattendance.data.models.DateRange
import com.example.qrattendance.data.models.GroupAttendanceResponse
import com.example.qrattendance.data.models.LoginRequest
import com.example.qrattendance.data.models.LoginResponse
import com.example.qrattendance.data.models.StudentScheduleResponse
import com.example.qrattendance.data.models.SubjectGroupPerformanceResponse
import com.example.qrattendance.data.models.UserInfoResponse
import com.example.qrattendance.domain.QrRepository
import retrofit2.Response

class QrRepositoryImpl(
    private val api: QrApi
) : QrRepository {

    override suspend fun login(request: LoginRequest): LoginResponse? {
        return try {
            val response = api.login(request)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getAllPerformanceForStudent(studentLogin: String) =
        api.getAllPerformanceForStudent(studentLogin)


    override suspend fun getStudentSchedule(
        studentLogin: String,
        startDate: String,
        endDate: String
    ): List<StudentScheduleResponse>? {
        return try {
            val dateRange = DateRange(startDate, endDate)
            val response = api.getStudentSchedule(studentLogin, dateRange)
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getAttendanceForSubjectGroupProfessor(
        professorLogin: String,
        subjectId: Int,
        groupId: Int
    ): List<GroupAttendanceResponse> {
        return api.getAttendanceForSubjectGroupProfessor(professorLogin, subjectId, groupId)
    }

    override suspend fun getPerformanceForSubjectGroup(
        subjectId: Int,
        groupId: Int
    ): List<SubjectGroupPerformanceResponse> {
        return api.getPerformanceForSubjectGroup(subjectId, groupId)
    }

    override suspend fun getUserInfo(): Response<UserInfoResponse> {
        return api.getUserInfo()
    }

    override suspend fun checkAttendance(
        studentLogin: String,
        code: String
    ): Response<CheckAttendanceResponse> {
        return api.checkAttendance(studentLogin, code)
    }

    override suspend fun changePassword(request: ChangePasswordRequest): Response<Unit> {
        return api.changePassword(request)
    }

}
