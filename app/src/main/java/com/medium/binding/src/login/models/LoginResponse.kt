package com.medium.binding.src.login.models

import com.medium.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

// 로그인 API response 형식
data class LoginResponse (
    @SerializedName("jwt") val jwt: String,
    @SerializedName("userIdx") val userIdx: Int
): BaseResponse()