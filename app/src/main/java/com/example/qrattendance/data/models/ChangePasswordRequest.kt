package com.example.qrattendance.data.models

data class ChangePasswordRequest(
    val login:String,
    val oldPassword:String,
    val newPassword:String
)