package com.example.qrattendance.data.apis

import com.example.qrattendance.data.models.ChangePasswordRequest
import com.example.qrattendance.data.models.CheckAttendanceResponse
import com.example.qrattendance.data.models.DateRange
import com.example.qrattendance.data.models.GroupAttendanceResponse
import com.example.qrattendance.data.models.LoginRequest
import com.example.qrattendance.data.models.LoginResponse
import com.example.qrattendance.data.models.StudentPerformanceResponse
import com.example.qrattendance.data.models.StudentScheduleResponse
import com.example.qrattendance.data.models.SubjectGroupPerformanceResponse
import com.example.qrattendance.data.models.UserInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface QrApi {


    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/auth/me")
    suspend fun getUserInfo(): Response<UserInfoResponse>

    @GET("api/student-performance/findAllForStudent/{studentLogin}")
    suspend fun getAllPerformanceForStudent(
        @Path("studentLogin") studentLogin: String
    ): List<StudentPerformanceResponse>


    @POST("api/student-performance/getStudentSchedule/{student_login}")
    suspend fun getStudentSchedule(
        @Path("student_login") studentLogin: String,
        @Body dateRange: DateRange
    ): Response<List<StudentScheduleResponse>>

    @GET("api/attendance/findAllForSubjectGroupProfessor/{professor_login}/{subject_id}/{group_id}")
    suspend fun getAttendanceForSubjectGroupProfessor(
        @Path("professor_login") professorLogin: String,
        @Path("subject_id") subjectId: Int,
        @Path("group_id") groupId: Int
    ): List<GroupAttendanceResponse>


    @GET("api/student-performance/findAllForSubjectGroup/{subject_id}/{group_id}")
    suspend fun getPerformanceForSubjectGroup(
        @Path("subject_id") subjectId: Int,
        @Path("group_id") groupId: Int
    ): List<SubjectGroupPerformanceResponse>

    @POST("api/attendance/checkAttendance/{student_login}/{code}")
    suspend fun checkAttendance(
        @Path("student_login") studentLogin: String,
        @Path("code")code: String
    ): Response<CheckAttendanceResponse>

    @PATCH("api/users/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ):Response<Unit>




}
