package com.medium.binding.src.main.my_page.settings.models

import com.google.gson.annotations.SerializedName

// 레트로핏 patchPW의 Body
data class PatchNicknameBody (
    @SerializedName("nickname") val nickname: String  // 바꿀 닉네임
)