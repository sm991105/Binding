package com.medium.binding.src.main.my_page.models

import com.google.gson.annotations.SerializedName

// 사용자가 쓴 글
data class WritingData(
    @SerializedName("bookIdx") val bookIdx: Int,        // 책 인덱스 번호
    @SerializedName("bookName") val bookName: String    // 책 이름
    )
