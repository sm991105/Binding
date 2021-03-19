package com.makeus6.binding.src.join.join3.models

import com.google.gson.annotations.SerializedName

data class PostJoinBody (
    @SerializedName("email") val email: String,
    @SerializedName("password") val pwd: String,
    @SerializedName("passwordCheck") val pwdChk: String,
    @SerializedName("nickname") val nickname: String
)