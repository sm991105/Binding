package com.example.binding.src.login.models

import com.example.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

// 로그인 API response 형식
data class LoginResponse (
    @SerializedName("jwt") val jwt: String
): BaseResponse()