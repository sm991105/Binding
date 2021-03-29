package com.medium.binding.src.main.my_page.settings.models

import com.google.gson.annotations.SerializedName

// 레트로핏 patchPW의 Body
data class PatchPWBody (
    @SerializedName("currentPassword") val currentPW: String,   // 현재 비밀번호
    @SerializedName("newPassword") val newPW: String,           // 새로운 비밀번호
    @SerializedName("passwordCheck") val newPWChk: String       // 새로운 비밀번호 확인
)