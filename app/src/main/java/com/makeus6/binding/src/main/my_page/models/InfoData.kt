package com.makeus6.binding.src.main.my_page.models

import com.google.gson.annotations.SerializedName

// 사용자 정보
data class InfoData(
    @SerializedName("userImgUrl") val userImgUrl: String,   // 유저 프로필 이미지
    @SerializedName("nickname") val nickname: String,       // 유저 닉네임
    @SerializedName("email") val email: String              // 유저 이메일
    )
