package com.example.qrattendance.data.models

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val accessToken:String,
)