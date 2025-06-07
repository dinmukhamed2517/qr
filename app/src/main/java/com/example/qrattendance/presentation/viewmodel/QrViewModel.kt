package com.example.qrattendance.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.qrattendance.data.models.ChangePasswordRequest
import com.example.qrattendance.data.models.CheckAttendanceResponse
import com.example.qrattendance.data.models.GroupAttendanceResponse
import com.example.qrattendance.data.models.LoginRequest
import com.example.qrattendance.data.models.LoginResponse
import com.example.qrattendance.data.models.StudentPerformanceResponse
import com.example.qrattendance.data.models.StudentScheduleResponse
import com.example.qrattendance.data.models.SubjectGroupPerformanceResponse
import com.example.qrattendance.data.models.UserInfoResponse
import com.example.qrattendance.data.storage.UserPreferences
import com.example.qrattendance.domain.QrRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log


sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

@HiltViewModel
class QrViewModel @Inject constructor(
    private val repository: QrRepository,
    private val dataStore: UserPreferences,
) : ViewModel() {

    private var _loginState = MutableStateFlow<UiState<LoginResponse>>(UiState.Loading)
    var loginState: MutableStateFlow<UiState<LoginResponse>> = _loginState

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loginState.value = UiState.Loading
            try {
                val result = repository.login(request)
                if (result != null) {
                    _loginState.value = UiState.Success(result)

                    Log.d("QrViewModel", "data store access token saved")
                    dataStore.saveAccessToken(result.accessToken)

                    fetchAndStoreUserInfo()

                    _isLoggedIn.value = true
                } else {
                    _loginState.value = UiState.Error("Login failed")
                    _isLoggedIn.value = false
                }
            } catch (e: Exception) {
                _loginState.value = UiState.Error(e.message ?: "Unknown Error")
                _isLoggedIn.value = false
            }
        }
    }


    private val _userInfoState = MutableStateFlow<UiState<UserInfoResponse>>(UiState.Loading)
    val userInfoState: StateFlow<UiState<UserInfoResponse>> = _userInfoState

    fun fetchAndStoreUserInfo() {
        viewModelScope.launch {
            Log.d("QrViewModel", "Calling fetchAndStoreUserInfo()")
            _userInfoState.value = UiState.Loading
            try {
                val response = repository.getUserInfo()
                if (response.isSuccessful && response.body() != null) {
                    val userInfo = response.body()!!
                    dataStore.saveUserLogin(userInfo.login)
                    Log.d("QrViewModel", "data store userInfo saved")

                    _userInfoState.value = UiState.Success(userInfo)
                } else {
                    _userInfoState.value = UiState.Error("Failed to load user info")
                }
            } catch (e: Exception) {
                _userInfoState.value = UiState.Error(e.message ?: "Unexpected error")
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            try {
                clearTokens()
                loginState.value = UiState.Success(LoginResponse(""))
            } catch (e: Exception) {
                loginState.value = UiState.Error("Logout failed")
            }
        }
    }


    fun clearTokens() {
        viewModelScope.launch {
            dataStore.clearTokens()
        }
    }

    fun checkIfLoggedIn() {
        viewModelScope.launch {
            val token = dataStore.getAccessToken().first()
            _isLoggedIn.value = !token.isNullOrEmpty()
        }
    }
    suspend fun getUserLoginFromPreferences(): String? {
        return dataStore.getUserLogin().first()
    }

    private val _studentPerformance =
        MutableLiveData<UiState<List<StudentPerformanceResponse>>>(UiState.Loading)
    val studentPerformance: LiveData<UiState<List<StudentPerformanceResponse>>> =
        _studentPerformance


    fun getAllPerformanceForStudent(studentLogin: String) {
        viewModelScope.launch {
            _studentPerformance.value = UiState.Loading
            try {
                val result = repository.getAllPerformanceForStudent(studentLogin)
                _studentPerformance.value = UiState.Success(result)
            } catch (e: Exception) {
                _studentPerformance.value = UiState.Error(e.message ?: "Failed to load data")
            }
        }
    }

    private val _scheduleState = MutableLiveData<UiState<List<StudentScheduleResponse>>>(UiState.Loading)
    val scheduleState: LiveData<UiState<List<StudentScheduleResponse>>> = _scheduleState


    fun getStudentSchedule(studentLogin: String, startDate: String, endDate: String) {
        viewModelScope.launch {
            _scheduleState.value = UiState.Loading
            try {
                val result = repository.getStudentSchedule(studentLogin, startDate, endDate)
                if (result != null) {
                    _scheduleState.value = UiState.Success(result)
                    Log.d("HomeViewModel", "Schedule fetched: ${result.size} entries")
                } else {
                    _scheduleState.value = UiState.Error("Failed to fetch schedule")
                }
            } catch (e: Exception) {
                _scheduleState.value = UiState.Error(e.message ?: "Unknown Error")
            }
        }
    }


    private val _groupAttendanceState = MutableStateFlow<UiState<List<GroupAttendanceResponse>>>(UiState.Loading)
    val groupAttendanceState: StateFlow<UiState<List<GroupAttendanceResponse>>> = _groupAttendanceState

    fun loadGroupAttendance(professor: String, subjectId: Int, groupId: Int) {
        viewModelScope.launch {
            _groupAttendanceState.value = UiState.Loading
            try {
                val result = repository.getAttendanceForSubjectGroupProfessor(professor, subjectId, groupId)
                _groupAttendanceState.value = UiState.Success(result)
            } catch (e: Exception) {
                _groupAttendanceState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private val _subjectGroupPerformanceState =
        MutableStateFlow<UiState<List<SubjectGroupPerformanceResponse>>>(UiState.Loading)
    val subjectGroupPerformanceState: StateFlow<UiState<List<SubjectGroupPerformanceResponse>>> = _subjectGroupPerformanceState

    fun loadSubjectGroupPerformance(subjectId: Int, groupId: Int) {
        viewModelScope.launch {
            _subjectGroupPerformanceState.value = UiState.Loading
            try {
                val result = repository.getPerformanceForSubjectGroup(subjectId, groupId)
                _subjectGroupPerformanceState.value = UiState.Success(result)
            } catch (e: Exception) {
                _subjectGroupPerformanceState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private val _attendanceState = MutableLiveData<UiState<CheckAttendanceResponse>>(UiState.Loading)
    val attendanceState: LiveData<UiState<CheckAttendanceResponse>> = _attendanceState


    fun checkAttendance(studentLogin: String, code: String) {
        viewModelScope.launch {
            _attendanceState.value = UiState.Loading
            try {
                val response = repository.checkAttendance(studentLogin, code)
                if (response.isSuccessful && response.body() != null) {
                    _attendanceState.value = UiState.Success(response.body()!!)
                } else {
                    _attendanceState.value = UiState.Error("Failed to check attendance")
                }
            } catch (e: Exception) {
                _attendanceState.value = UiState.Error(e.message ?: "Unexpected error")
            }
        }

    }
    private val _passwordChangeState = MutableStateFlow<UiState<Unit>>(UiState.Loading)
    val passwordChangeState: StateFlow<UiState<Unit>> = _passwordChangeState

    fun changePassword(login: String, oldPassword: String, newPassword: String) {
        viewModelScope.launch {
            _passwordChangeState.value = UiState.Loading  // Set state to Loading before API call

            try {
                val request = ChangePasswordRequest(login, oldPassword, newPassword)
                val response = repository.changePassword(request)
                if (response.isSuccessful) {
                    _passwordChangeState.value = UiState.Success(Unit)  // On success, update state
                    Log.d("QrViewModel", "Password changed successfully")
                } else {
                    _passwordChangeState.value = UiState.Error("Password change failed")  // On failure, update state
                    Log.e("QrViewModel", "Password change failed")
                }
            } catch (e: Exception) {
                _passwordChangeState.value = UiState.Error("Error changing password: ${e.message}")  // On error, update state
                Log.e("QrViewModel", "Error changing password: ${e.message}")
            }
        }
    }
}
