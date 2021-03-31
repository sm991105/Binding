package com.medium.binding.src.main.my_page.models

import com.google.gson.annotations.SerializedName

data class CommentsBookName (
    // 책 이름
    @SerializedName("bookName") val bookName: String
)