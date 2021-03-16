package com.example.binding.src.find_password.models

import com.google.gson.annotations.SerializedName


data class PostFindPwBody(
    @SerializedName("email") val email: String,
    @SerializedName("nickname") val nickname: String)
