package com.medium.binding.src.login.models

import com.google.gson.annotations.SerializedName

data class PostLoginBody (
    @SerializedName("email") val email: String,
    @SerializedName("password") val pwd: String
)