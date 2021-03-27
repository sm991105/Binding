package com.makeus6.binding.src.main.my_page.settings.models

import com.google.gson.annotations.SerializedName

// 레트로핏 patchImg의 Body
// 둘 중 하나는 변경 안하려면 null 값 전달?
data class PatchProfileBody (
    @SerializedName("image") val imgUrl: String?,  // 변경할 이미지 url, 삭제는 "-1" 전달
    @SerializedName("nickname") val nickname: String?    // 변경할 닉네임,
)