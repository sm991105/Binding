package com.medium.binding.src.main.my_page.settings.models

import com.google.gson.annotations.SerializedName

// 레트로핏 patchImg의 Body
data class PatchImgBody (
    @SerializedName("image") val imgUrl: String  // 이미지 url
)