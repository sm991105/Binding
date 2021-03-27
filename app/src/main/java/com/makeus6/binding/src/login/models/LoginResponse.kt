package com.makeus6.binding.src.login.models

import com.makeus6.binding.config.BaseResponse
import com.google.gson.annotations.SerializedName

// 로그인 API response 형식
data class LoginResponse (
    @SerializedName("jwt") val jwt: String,
    @SerializedName("userIdx") val userIdx: Int
): BaseResponse()